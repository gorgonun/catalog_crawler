package catalog.utils

import java.time.LocalDate

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

trait Commom {
  val logger: Logger = LoggerFactory.getLogger(getClass)
}

object Utils {
  def normalize(text: String): String = {
    text
      .replaceAll("""(?m)\s+$""", "")
      .replaceAll("[,.:]", "")
      .replaceAll("[ /-]", "_")
      .replaceAll("[_]+", "_")
      .toLowerCase
  }
}
