package catalog.utils

import java.text.Normalizer
import java.util.regex.Pattern

import org.slf4j.{Logger, LoggerFactory}

trait Common {
  val logger: Logger = LoggerFactory.getLogger(getClass)
}

object Utils {
  def normalize(text: String): String = {
    val normalizedString = Normalizer.normalize(text, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    pattern.matcher(normalizedString).replaceAll("")
      .replaceAll("[^a-zA-Z0-9 /\\-_]+", "")
      .replaceAll("[ ]", "_")
      .replaceAll("[ /-]", "_")
      .replaceAll("[_]+", "_")
      .toLowerCase
  }

  def parseInt(price: String): Option[Int] = {
    val noDecimal = price.split(",").head
    "[\\d+]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.toInt)
      case _ => None
    }
  }
}
