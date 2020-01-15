package catalog.crawlers

import catalog.utils.Common
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

trait Crawler extends Common {

  def page(url: String, sleep: Int = 2000): Document = {
    Thread.sleep(sleep)
    logger.info(s"Getting page $url")
    Jsoup.connect(url).get()
  }
}
