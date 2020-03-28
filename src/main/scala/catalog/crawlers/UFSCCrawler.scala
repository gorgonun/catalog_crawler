package catalog.crawlers

import java.time.LocalDate

import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConverters._

object UFSCCrawler extends Crawler {

  def crawl(startDate: Option[LocalDate] = None, endDate: Option[LocalDate] = None): Stream[Elements] = {
    val url = "https://classificados.inf.ufsc.br/latestads.php?offset="
    val sleep = 2000

    val totalPages = getPagesNumber(15)
      .map(pageNumber => getBodyElements(page(url + pageNumber.toString, sleep)))
      .takeWhile(page => pageDateIsBetween(page, startDate.getOrElse(LocalDate.now()), endDate.getOrElse(LocalDate.now().plusDays(1))))

    totalPages
      .flatMap{
        _.iterator.asScala.map(_.select("td"))}
      .filter(_.size >= 4)
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

  def pageDateIsBetween(page: Elements, startDate: LocalDate, endDate: LocalDate): Boolean = {
    val dateAsText = page.get(1).select("td").get(2).text.split("/")
    val pageDate = LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)

    ((pageDate isAfter startDate) || (pageDate isEqual startDate)) && ((pageDate isBefore endDate) || (pageDate isEqual endDate))
  }

  def getTableRows(page: Elements): Iterator[Elements] = page.iterator.asScala.map(_.select("td"))
}
