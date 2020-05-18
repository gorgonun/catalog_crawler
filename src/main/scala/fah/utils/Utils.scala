package fah.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.text.Normalizer
import java.time.LocalDate
import java.util.regex.Pattern
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import org.apache.commons.io.IOUtils
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString
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
    val noDecimal = price.replace(".", "").split(",").head
    "[\\d]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.toInt)
      case _ => None
    }
  }

  def parseInt(price: Option[String]): Option[Int] = price.flatMap(parseInt)

  def parseStringByPrimitive(str: String, primitiveMap: Map[String, String]): Option[String] = {
    primitiveMap
      .keys
      .flatMap(primitive => if (str.contains(primitive)) primitiveMap.get(primitive) else None)
      .headOption
  }

  def parseStringByRegex(str: String, regexMap: Map[String, String]): Option[String] = {
    regexMap
      .keys
      .flatMap(_.r findFirstIn str)
      .headOption
  }

  def toB64Compressed(text: String): String = {
    val bos = new ByteArrayOutputStream()
    val gzs = new GZIPOutputStream(bos)
    gzs.write(text.getBytes(Charsets.UTF_8))
    gzs.close()
    val b64String = BaseEncoding.base64().encode(bos.toByteArray)
    b64String
  }

  def decodeString(text: String): String = {
    val bytes = BaseEncoding.base64().decode(text)
    val zipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes))
    IOUtils.toString(zipInputStream)
  }

  object LocalDateSerializer extends CustomSerializer[LocalDate](format => ({
    case JString(str) =>
      LocalDate.parse(str)
  }, {
    case date: LocalDate => JString(date.toString)
  }))
}
