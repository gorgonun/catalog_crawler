package catalog.parsers

import catalog.crawlers.Crawler
import catalog.pojos.{CompleteItem, HabitationEnum, RawItem}
import catalog.utils.Utils.{normalize, parseInt}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.util.Try

// FIXME: Remove page and Crawler with pojo
object UFSCParser extends Crawler {

  def parse(preProcessedPageRows: Stream[Elements])
           (implicit spark: SparkSession): Dataset[RawItem] = {
    import spark.implicits._

    spark
      .createDataset(preProcessedPageRows.map(parse)
        .flatMap(_.toOption)
        .map(ri => getCompleteInfo(page(ri.link), ri)))
  }

  def getIdFromLink(link: String): Option[Int] = parseInt(link.split("=")(1))

  def parse(items: Elements): Try[RawItem] = {
    Try {
      val link = "https://classificados.inf.ufsc.br/" + items.get(1).selectFirst("a").attr("href")
      val ri = RawItem(
        id = getIdFromLink(link).get,
        category = Some(normalize(items.get(0).text)),
        title = items.get(1).attr("title"),
        link = link,
        postDate = items.get(2).text)
      if(ri.category.isEmpty || ri.title.isEmpty || ri.link.isEmpty || ri.postDate.isEmpty) throw new Exception("") else ri
    }
  }

  def getCompleteInfo(doc: Document, incompleteRawItem: RawItem): RawItem = {
    val data = doc
      .selectFirst("table tr td form table tbody")
      .select("tbody tr")

    val description = data.select("td").get(1).text
    val mp = data.iterator.asScala
    .map(_.select("td").iterator.asScala.toSeq)
    .filter(_.length == 2)
    .map(x => (normalize(x.head.text), x(1).text))
    .toMap

    val category = incompleteRawItem.category match {
      case Some(r) => Some(r + "_" + mp.getOrElse("complemento", ""))
      case _ => mp.get("complemento")
    }

    incompleteRawItem.copy(
      category = category,
      description = Option(description),
      sellerName = mp.get("vendido_por"),
      sellerEmail = mp.get("email"),
      expirationDate = mp.get("anuncio_expira"),
      city = mp.get("cidade"),
      neighborhood = mp.get("bairro"),
      street = mp.get("logradouro_n"),
      price = mp.get("preco"),
      gender = mp.get("genero"),
      animalsAllowed = mp.get("permitido_animais"),
      contract = mp.get("necessita_contrato"),
      laundry = mp.get("lavanderia_disponível"),
      internetIncluded = mp.get("conexão_c_internet"),
      waterIncluded = mp.get("agua_cond_e_iptu_inclusos"),
      habitationType = mp.get("complemento")
    )
  }
}
