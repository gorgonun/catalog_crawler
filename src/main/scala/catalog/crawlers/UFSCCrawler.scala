package catalog.crawlers

import java.time.LocalDate

import catalog.pojos._
import catalog.utils.Utils._
import org.apache.spark.sql.{Dataset, SparkSession}
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.util.Try

object UFSCCrawler extends Crawler {

  def start()(implicit spark: SparkSession): Dataset[RawItem] = {
    import spark.implicits._

    logger.info("Starting ufsc crawler")

    val url = "https://classificados.inf.ufsc.br/latestads.php?offset="
    val sleep = 2000
    val today = LocalDate.now()

    val totalPages = getPagesNumber(15)
      .map(pageNumber => getBodyElements(page(url + pageNumber.toString, sleep)))
      .takeWhile(page => pageDateIsGreaterOrEqual(page, today))

    val rows = totalPages
      .flatMap{
        _.iterator.asScala.map(_.select("td"))}
      .filter(_.size >= 4)

    val table = spark
      .createDataset(rows.map(parse)
        .flatMap(_.toOption)
        .map(ri => getCompleteInfo(page(ri.link), ri)))

    logger.info("Finished ufsc crawler")

    table
  }

  def getPagesNumber(step: Int, limit: Int = 5): Stream[Int] = {
    Stream.iterate(0)(_ + step)
      .map{
        pageNumber =>
          if (pageNumber > (limit - 1) * 15) throw new VerifyError("Page number was greater than the limit")
          pageNumber
      }
  }

  def getBodyElements(completePage: Document): Elements = completePage.selectFirst("table[class=box]").select("tr")

  def pageDateIsGreaterOrEqual(page: Elements, date: LocalDate): Boolean = {
    val dateAsText = page.get(1).select("td").get(2).text.split("/")
    val pageDate = LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)

    (pageDate isAfter date) || (pageDate isEqual date)
  }

  def getTableRows(page: Elements): Iterator[Elements] = page.iterator.asScala.map(_.select("td"))

  def getIdFromLink(link: String): Int = parseInt(link.split("=")(1)).get

  def parse(items: Elements): Try[RawItem] = {
    val link = "https://classificados.inf.ufsc.br/" + items.get(1).selectFirst("a").attr("href")
    val id = getIdFromLink(link)
    val temp = Try(
      RawItem(
        id = id,
        category = normalize(items.get(0).text),
        title = items.get(1).text,
        link = link,
        date = items.get(2).text,
        image = items.get(3).selectFirst("img").attr("src"))
    )

    temp.map(ri => if(ri.category.isEmpty || ri.title.isEmpty || ri.link.isEmpty || ri.date.isEmpty || ri.image.isEmpty) throw new Exception("") else ri)
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

    incompleteRawItem.copy(
      description = Option(description),
      seller = mp.get("vendido_por"),
      email = mp.get("email"),
      expiration = mp.get("anuncio_expira"),
      postDate = mp.get("adicionado"),
      city = mp.get("cidade"),
      neighborhood = mp.get("bairro"),
      street = mp.get("logradouro_n"),
      price = mp.get("preco"),
      gender = mp.get("genero"),
      animals = mp.get("permitido_animais"),
      contract = mp.get("necessita_contrato"),
      laundry = mp.get("lavanderia_disponível"),
      internet = mp.get("conexão_c_internet"),
      basicExpenses = mp.get("agua_cond_e_iptu_inclusos"),
    )
  }
}
