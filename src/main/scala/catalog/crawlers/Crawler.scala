package catalog.crawlers

import catalog.utils.Common
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

trait Crawler extends Common {

  def page(url: String, sleep: Int = 2000): Document = {
    Thread.sleep(sleep)
    logger.info(s"Getting page $url")
    Jsoup.connect(url)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/74.0")
      .referrer("http://www.google.com")
      .get()
  }
}
