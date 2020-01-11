package catalog.crawlers

import catalog.crawlers.Crawler
import org.scalatest.{FunSpec, Matchers}

class CrawlerSpec extends FunSpec with Matchers with Crawler {

  it("should get pages respecting the interval") {
    val pages = Seq("a", "b")

    val startTimeMillis = System.currentTimeMillis()

    pages.map(p => page(p, 1000))

    val endTimeMillis = System.currentTimeMillis()
    val durationMillis = endTimeMillis - startTimeMillis

    durationMillis shouldBe >= (2000.toLong)
  }
}
