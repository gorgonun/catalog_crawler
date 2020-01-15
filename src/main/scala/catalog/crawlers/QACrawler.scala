package catalog.crawlers
import java.util.Properties

import catalog.pojos.{RawItem, RawQA}
import catalog.utils.Common
import catalog.utils.Utils.normalize
import org.apache.spark.sql.{SaveMode, SparkSession}
import scalaj.http.Http
import org.json4s._
import org.json4s.native.JsonMethods._

object QACrawler extends Common {

  def main(args: Array[String]): Unit = {
    logger.info("Starting qa crawler")

    val spark = SparkSession
      .builder()
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val dbUrl = "jdbc:" + sys.env("DATABASE_URL")

    val connectionProperties = new Properties()
    connectionProperties.setProperty("Driver", "org.postgresql.Driver")

    implicit val formats: DefaultFormats.type = DefaultFormats

    val url = "https://www.quintoandar.com.br/api/search?q=for_rent:%27true%27&fq=local:[%27-27.553723535794024,-48.56940561570027%27,%27-27.643555064205977,-48.46804018429974%27]&return=banheiros,quartos,iptu,andar,endereco,amenidades,tipo,instalacoes,aluguel_condominio,for_rent,local,suites,bairro,aluguel,foto_capa,photos,home_insurance,condominio,ultima_publicacao,variant_images_titles,variant_images,custo,cidade,visit_status,garantias,for_sale,first_publication,condo_iptu,vagas,id,area&start=0&size=40&q.parser=structured&format=json&sort=ultima_publicacao%20desc"

    val jsonResp = (parse(Http(url).asString.body) \ "hits")
      .findField(_._1 == "hit")
      .map {_._2.extract[JArray].arr
        .flatMap(_.findField(_._1 == "fields")
          .map(_._2.extract[RawQA]))}
          .getOrElse(List.empty)

    val ds = spark.createDataset(jsonResp)

    ds.map{ qaRaw =>
      RawItem(
        category = normalize(qaRaw.tipo.getOrElse("") + " para alugar direto com proprietario"),
        date = qaRaw.first_publication.get,
        title = normalize(qaRaw.tipo.getOrElse("") + " " + qaRaw.endereco.getOrElse("")),
        image = "yes",
        link = "https://www.quintoandar.com.br/imovel/" + qaRaw.id.get,
        description = Some("descrição no link"),
        seller = None,
        expiration = None,
        postDate = qaRaw.first_publication,
        email = None,
        price = Some((parsePrice(qaRaw.aluguel_condominio.getOrElse("0")).getOrElse(0) + parsePrice(qaRaw.home_insurance.getOrElse("0")).getOrElse(0)).toString),
        street = qaRaw.endereco,
        neighborhood = qaRaw.bairro,
        city = qaRaw.cidade,
        gender = None,
        contract = Some("sim"),
        basicExpenses = None,
        laundry = qaRaw.amenidades.flatMap(_.find(_ == "MAQUINA_DE_LAVAR").map(_ => "sim")),
        internet = None,
        animals = qaRaw.amenidades.flatMap(_.find(_ == "PODE_TER_ANIMAIS_DE_ESTIMACAO").map(_ => "sim")),
        rent = qaRaw.aluguel,
        cooker = qaRaw.amenidades.flatMap(_.find(_ == "FOGAO_INCLUSO").map(_ => "sim")),
        fridge = qaRaw.amenidades.flatMap(_.find(_ == "GELADEIRA_INCLUSO").map(_ => "sim"))
      )
    }

    ds.write.mode(SaveMode.Append).jdbc(url = dbUrl, table = "rawitems", connectionProperties = connectionProperties)

    logger.info("Finished qa crawler")
  }

  def parsePrice(price: String): Option[Int] = {
    val noDecimal = price.split(",").head
    "[\\d+]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.toInt)
      case _ => None
    }
  }
}
