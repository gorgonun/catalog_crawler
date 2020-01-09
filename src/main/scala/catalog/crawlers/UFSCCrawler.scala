package catalog.crawlers

import java.time.LocalDate
import java.util.Properties

import catalog.pojos._
import catalog.utils.Utils.{logger, normalize}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.util.Try

object UFSCCrawler extends Crawler {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val url = "https://classificados.inf.ufsc.br/latestads.php?offset="
    val dbUrl = "jdbc:" + sys.env("DATABASE_URI")

    val connectionProperties = new Properties()
    connectionProperties.setProperty("Driver", "org.postgresql.Driver")

    val sleep = 2000

    val today = LocalDate.now()
    val totalPages = getPagesNumber(15)
      .map(pageNumber => getBodyElements(page(url + pageNumber.toString, sleep)))
      .takeWhile(page => pageDateIsGreaterOrEqual(page, today))

    val rows = totalPages.flatMap(_.iterator.asScala.map(_.select("td"))).filter(_.size >= 4)
    val table = spark.createDataset(rows.map(parse).flatMap(_.toOption).map(getCompleteInfo))

    table.write.mode(SaveMode.Append).jdbc(url = dbUrl, table = "rawitems", connectionProperties = connectionProperties)

    logger.info("Success")
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

  def getTableRows(page: Elements) = page.iterator.asScala.map(_.select("td"))

  def parse(items: Elements): Try[RawItem] = {
    println(items)
    val link = "https://classificados.inf.ufsc.br/" + items.get(1).selectFirst("a").attr("href")
    Try(
      RawItem(
        category = normalize(items.get(0).text),
        title = items.get(1).text,
        link = link,
        date = items.get(2).text,
        image = items.get(3).selectFirst("img").attr("src"))
    )
  }

  def getCompleteInfo(incompleteRawItem: RawItem): RawItem = {
    val doc = page(incompleteRawItem.link)
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
      expiration = mp.get("anúncio_expira"),
      postDate = mp.get("adicionado"),
      city = mp.get("cidade"),
      neighborhood = mp.get("bairro"),
      street = mp.get("logradouro_nº"),
      price = mp.get("preço"),
      gender = mp.get("gênero"),
      animals = mp.get("permitido_animais?"),
      contract = mp.get("necessita_contrato?"),
      laundry = mp.get("lavanderia_disponível?"),
      internet = mp.get("conexão_c_internet?"),
      basicExpenses = mp.get("água_cond_e_iptu_inclusos?"),
    )
  }
}
