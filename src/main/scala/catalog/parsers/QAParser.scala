package catalog.parsers

import catalog.crawlers.UFSCCrawler.logger
import catalog.pojos.{RawItem, RawQA}
import catalog.utils.Common
import catalog.utils.Utils.normalize
import org.apache.spark.sql.SparkSession

object QAParser extends Common {
  def main(args: Array[String]): Unit = {
    val qaRaw = RawQA(
      Some("1"),
      Some("3"),
      Some("0"),
      Some("3"),
      Some("Rodovia Admar Gonzaga"),
      Some(List("Tomadas 3 pinos", "Chuveiro elétrico", "Box de vidro", "Energia no imóvel", "Espelho no banheiro", "Sol da tarde", "Pode ter animais de estimação", "Armários na cozinha", "Armários no banheiro", "Armários no quarto", "TOMADAS_NOVAS", "CHUVEIRO_ELETRICO", "BOX_BLINDEX", "ENERGIA", "ESPELHO_NO_BANHEIRO", "SOL_DA_TARDE", "PODE_TER_ANIMAIS_DE_ESTIMACAO", "ARMARIOS_NA_COZINHA", "ARMARIOS_NOS_BANHEIROS", "ARMARIOS_EMBUTIDOS_NO_QUARTO", "NaoMobiliado")),
      Some("Apartamento"),
      Some(List("Gás encanado", "GAS_ENCANADO")),
      Some("1930"),
      Some("true"),
      Some("-27.5856489,-48.5000758"),
      Some("0"),
      Some("Itacorubi"),
      Some("1700"),
      Some("capa893011229542_7411704225813alta9046.jpg"),
      Some(List("893011229-542.7411704225813alta9046.jpg", "893011229-784.9847804760177alta9045.jpg", "893011229-610.7787276875408alta9047.jpg", "893011229-136.0429850201099alta9048.jpg", "893011229-745.6999926941334alta9049.jpg")),
      Some("28"),
      Some("230"),
      Some("2020-01-15T15:56:14Z"),
      Some(List("Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda", "Foto sem legenda")),
      Some(List("0_893011229-542.7411704225813alta9046.jpg", "0_893011229-784.9847804760177alta9045.jpg", "0_893011229-610.7787276875408alta9047.jpg", "0_893011229-136.0429850201099alta9048.jpg", "0_893011229-745.6999926941334alta9049.jpg", "1_893011229-542.7411704225813alta9046.jpg", "1_893011229-784.9847804760177alta9045.jpg", "1_893011229-610.7787276875408alta9047.jpg", "1_893011229-136.0429850201099alta9048.jpg", "1_893011229-745.6999926941334alta9049.jpg")),
      Some("1958"),
      Some("Florianópolis"),
      Some("BLOCKED"),
      Some(List("SeguroFairfax")),
      Some("false"),
      Some("2020-01-04T21:58:43Z"),
      Some("230"),
      Some("0"),
      Some("60"),
      Some("893011229"))

    logger.info("Starting ufsc crawler")

    val spark = SparkSession
      .builder()
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val b = RawItem(
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
    println(b)
  }

  def parsePrice(price: String): Option[Int] = {
    val noDecimal = price.split(",").head
    "[\\d+]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.toInt)
      case _ => None
    }
  }
}