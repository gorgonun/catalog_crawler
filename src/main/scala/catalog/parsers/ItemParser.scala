package catalog.parsers

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}

import catalog.pojos.{CompleteItem, HabitationEnum, RawItem}
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
    val ciTemp = CompleteItem(
      id = rawItem.id,
      category = "",
      date = localDateAsTimestamp(parseDate(rawItem.postDate).get),
      title = rawItem.title,
      link = rawItem.link,
      image = "",
      description = rawItem.description,
      seller = rawItem.sellerName,
      email = rawItem.sellerEmail.flatMap(parseEmail),
      price = rawItem.price.flatMap(parseInt),
      street = rawItem.street,
      neighborhood = rawItem.neighborhood,
      city = rawItem.city,
      gender = rawItem.gender.flatMap(parseGender),
      contract = rawItem.contract.flatMap(textToBoolean),
      basicExpenses = rawItem.waterIncluded.flatMap(textToBoolean),
      laundry = rawItem.laundry.flatMap(textToBoolean),
      internet = rawItem.internetIncluded.flatMap(textToBoolean),
      animals = rawItem.animalsAllowed.flatMap(textToBoolean)
    )
    ciTemp.copy(category = s"${ciTemp.habitation}_ofertada_pelo_${ciTemp.negotiator}_para_${ciTemp.contractType}")
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

  def inferHabitationTypeFromRawCategory(normalizedRawCategory: Option[String]): Option[HabitationEnum.Value] = {
    val habitationTypes = Map("apart" -> HabitationEnum.Apartment, "cas" -> HabitationEnum.Home, "kit" -> HabitationEnum.Kitnet)
    habitationTypes
      .keys
      .flatMap{ht =>
        normalizedRawCategory
          .filter(_.contains(ht))
      }
      .lastOption
      .flatMap(habitationTypes.get)
  }
}
