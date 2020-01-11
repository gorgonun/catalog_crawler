package catalog.crawlers

import org.scalatest.{FunSpec, Matchers}

import scala.util.Try

class CrawlerSpec extends FunSpec with Matchers with Crawler {

  it("should get pages respecting the interval") {
    val pages = Seq("file://../../", "http://localhost")

    val startTimeMillis = System.currentTimeMillis()

    pages.map(p => Try(page(p, 1000)))

    val endTimeMillis = System.currentTimeMillis()
    val durationMillis = endTimeMillis - startTimeMillis

    durationMillis shouldBe >= (2000.toLong)
  }
}
