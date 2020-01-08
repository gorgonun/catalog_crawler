package catalog.crawlers

import java.lang.VerifyError

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

    result.toString shouldBe expected.toString
  }
}
