package catalog.utils

import org.slf4j.{Logger, LoggerFactory}

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
