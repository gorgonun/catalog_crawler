package catalog.parsers

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}

import catalog.pojos.{CompleteItem, RawItem}
import catalog.utils.Common
import catalog.utils.Utils._
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.util.Try

object ItemParser extends Common {

  def parse(rawItems: Dataset[RawItem])(implicit spark: SparkSession): Dataset[CompleteItem] = {
    import spark.implicits._

    rawItems.map(parse)
  }

  def parse(rawItem: RawItem): CompleteItem = {
    CompleteItem(
      id = rawItem.id,
      category = Some(normalize(rawItem.category.getOrElse(""))),
      postDate = localDateAsTimestamp(parseDate(rawItem.date).get),
      title = rawItem.title,
      image = rawItem.image,
      link = rawItem.link,
      description = rawItem.description,
      seller = rawItem.seller,
      expiration = rawItem.expiration.flatMap(r => parseDate(r).toOption.map(localDateAsTimestamp)),
      postDate = rawItem.postDate.flatMap(r => parseDate(r).toOption.map(localDateAsTimestamp)),
      email = rawItem.email.flatMap(parseEmail),
      price = rawItem.price.flatMap(parseInt),
      street = rawItem.street,
      neighborhood = rawItem.neighborhood,
      city = rawItem.city,
      gender = rawItem.gender.flatMap(parseGender),
      contract = rawItem.contract.flatMap(textToBoolean),
      basicExpenses = rawItem.basicExpenses.flatMap(textToBoolean),
      laundry = rawItem.laundry.flatMap(textToBoolean),
      internet = rawItem.internet.flatMap(textToBoolean),
      animals = rawItem.animals.flatMap(textToBoolean)
    )
  }

    def textToBoolean(text: String): Option[Boolean] =
    normalize(text).toLowerCase match {
      case "sim" => Some(true)
      case _ => Some(false)
    }

  def parseEmail(email: String): Option[String] = {
    "\\w\\S+[@]\\w+[.]\\w+".r findFirstMatchIn email match {
      case Some(r) => Some(r.toString)
      case _ => None
    }
  }

  def parseGender(gender: String): Option[String] = {
    normalize(gender) match {
      case "feminino" => Some("F")
      case "masculino" => Some("M")
      case _ => None
    }
  }

  def parseDate(date: String): Try[LocalDate] = {
    Try {
      val finalDate = Try(ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.systemDefault())).toLocalDate)
      finalDate.getOrElse{
        val dateAsText = date.split("/")
        LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)
      }
    }
  }

  def localDateAsTimestamp(date: LocalDate): Timestamp = {
    Timestamp.valueOf(date.atStartOfDay)
  }
}
