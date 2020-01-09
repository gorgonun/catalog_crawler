package catalog.crawlers

import java.lang.VerifyError
import java.time.LocalDate

import catalog.pojos.RawItem
import catalog.utils.Utils.logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class UFSCCrawlerSpec extends FunSpec with Matchers{
//  val mockedCrawler = new UFSCCrawler with Crawler {
//    override def page(url: String, sleep: Int): Document = {
//      Thread.sleep(sleep)
//      logger.info(s"Getting local file $url")
//      val input = Source.fromFile(url)
//      val parsedPage = Jsoup.parse(input.getLines.mkString)
//      input.close()
//      parsedPage
//    }
//  }

  it("should retrieve a stream of numbers respecting the step") {
    val result = UFSCCrawler.getPagesNumber(15)
    result.take(5).sum shouldBe 150
  }

  it("should throw error if the limit is reached") {
    val result = UFSCCrawler.getPagesNumber(15)
    an[VerifyError] should be thrownBy result.take(6).sum
  }

  it("should get the relevant table rows") {
    val expectedFile = Source.fromResource("catalog/crawlers/UFSCCrawlerSpec/table_rows.html")
    val expected = Jsoup.parse(expectedFile.getLines.mkString).selectFirst("table").select("tr")
    val resultFile = Source.fromResource("catalog/crawlers/UFSCCrawlerSpec/complete_page.html")
    val result = UFSCCrawler.getBodyElements(Jsoup.parse(resultFile.getLines.mkString))
    expectedFile.close()
    resultFile.close()

    result.size shouldBe expected.size
  }

  it("should return true when date is greater or equals today") {
    val pageFile = Source.fromResource("catalog/crawlers/UFSCCrawlerSpec/complete_page.html")
    val page = UFSCCrawler.getBodyElements(Jsoup.parse(pageFile.getLines.mkString))
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    page.get(1).select("td").get(2).text(s"${today.getDayOfMonth}/${today.getMonthValue}/${today.getYear}")
    val todayResult = UFSCCrawler.pageDateIsGreaterOrEqual(page, today)
    page.get(1).select("td").get(2).text(s"${tomorrow.getDayOfMonth}/${tomorrow.getMonthValue}/${tomorrow.getYear}")
    val tomorrowResult = UFSCCrawler.pageDateIsGreaterOrEqual(page, today)

    todayResult shouldBe true
    tomorrowResult shouldBe true
  }

  it("should return false when date is lower than today") {
    val pageFile = Source.fromResource("catalog/crawlers/UFSCCrawlerSpec/complete_page.html")
    val page = UFSCCrawler.getBodyElements(Jsoup.parse(pageFile.getLines.mkString))
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    page.get(1).select("td").get(2).text(s"${yesterday.getDayOfMonth}/${yesterday.getMonthValue}/${yesterday.getYear}")
    val yesterdayResult = UFSCCrawler.pageDateIsGreaterOrEqual(page, today)

    yesterdayResult shouldBe false
  }

  it("should parse a valid raw item") {
    val rawFile = Source.fromResource("catalog/crawlers/UFSCCrawlerSpec/raw_item.html")
    val rawItem = Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr")

    val expected = RawItem(
      link = "",
      category = "",
      date = "",
      title = "",
      image = ""
    )

    UFSCCrawler.parse(rawItem) shouldBe expected
  }
}
