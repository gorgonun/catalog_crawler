package catalog.crawlers

import java.time.LocalDate
import java.util.Properties

import catalog.pojos._
import catalog.utils.Utils.{logger, normalize, page}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.util.Try

object UFSCCrawler {

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
    val pages = pagesToParse(url, today, sleep)

    val rows = pages.flatMap(_.iterator.asScala.map(_.select("td"))).filter(_.size >= 4)
    val items = spark.createDataset(rows.map(parse).flatMap(_.toOption))
    val infos = items.map(item => getCompleteInfo(item.link))
    val table = items.join(infos, Seq("link"))

    table.write.mode(SaveMode.Append).jdbc(url = dbUrl, table = "rawitems", connectionProperties = connectionProperties)

    logger.info("Success")
  }

  def pagesToParse(url: String, date: LocalDate, sleep: Int, limit: Int = 5): Stream[Elements] = {
    Stream.iterate(0)(_ + 15)
      .map{
        pageNumber =>
          if (pageNumber > (limit - 1) * 15) throw new VerifyError("Page number was greater than the limit")
          page(url + pageNumber.toString, sleep)
            .selectFirst("table[class=box]")
            .select("tr")
      }
      .takeWhile{
        doc =>
          val dateAsText = doc.get(1).select("td").get(2).text.split("/")
          val pageDate = LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)

          (pageDate isAfter date) || (pageDate isEqual date)
      }
  }

  def parse(items: Elements): Try[RawItem] = {
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

  def getCompleteInfo(link: String): RawInfo = {
    val doc = page(link)
    val data = doc
      .selectFirst("table tr td form table tbody")
      .select("tbody tr")

    val description = data.select("td").get(1).text
    val mp = data.iterator.asScala
      .map(_.select("td").iterator.asScala.toSeq)
      .filter(_.length == 2)
      .map(x => (normalize(x.head.text), x(1).text))
      .toMap

    RawInfo(
      link = link,
      description = description,
      seller = mp("vendido_por"),
      email = mp.get("email"),
      expiration = mp("anúncio_expira"),
      postDate = mp("adicionado"),
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