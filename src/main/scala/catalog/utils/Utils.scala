package catalog.utils

import java.time.LocalDate

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object Utils {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def page(url: String, sleep: Int = 2000): Document = {
    Thread.sleep(sleep)
    logger.info(s"Getting page $url")
    Jsoup.connect(url).get()
  }

  def normalize(text: String): String = {
    text
      .replace(" ", "_")
      .replace("/", "_")
      .replace("_-_", "_")
      .replace(",", "")
      .replace(".", "")
      .replace(":", "")
      .replaceAll("""(?m)\s+$""", "")
      .toLowerCase
  }

  def parseDate(date: String): Try[LocalDate] = {
    Try {
      val dateAsText = date.split("/")
      LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)
    }
  }
}
