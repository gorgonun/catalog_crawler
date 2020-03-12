package catalog.parsers

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}

import catalog.pojos.{CompleteItem, ContractEnum, HabitationEnum, NegotiatorEnum, RawItem}
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
      categories = List.empty,
      postDate = localDateAsTimestamp(parseDate(rawItem.postDate).get),
      expiration = rawItem.expirationDate.map(date => localDateAsTimestamp(parseDate(date).get)),
      title = rawItem.title,
      link = rawItem.link,
      images = rawItem.images,
      description = rawItem.description,
      sellerName = rawItem.sellerName,
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
      animals = rawItem.animalsAllowed.flatMap(textToBoolean),
      habitation = inferHabitationTypeFromRawNormalizedText(rawItem.category.getOrElse("")).orElse(inferHabitationTypeFromRawNormalizedText(normalize(rawItem.description.getOrElse("")))),
      negotiator = inferNegotiatorTypeFromRawNormalizedText(rawItem.category.getOrElse("")).orElse(inferNegotiatorTypeFromRawNormalizedText(normalize(rawItem.description.getOrElse("")))),
      contractType = inferContractTypeFromRawNormalizedText(rawItem.category.getOrElse("")).orElse(inferContractTypeFromRawNormalizedText(normalize(rawItem.description.getOrElse(""))))
    )
    ciTemp
//    ciTemp.copy(categories = List(ciTemp.habitation, ciTemp.negotiator, ciTemp.contractType).flatMap(a => _.toString))
  }

  def textToBoolean(text: String): Option[Boolean] = {
    val normalizedText = normalize(text)
    if (normalizedText.contains("si")) Some(true) else if (normalizedText.contains("na")) Some(false) else None
  }

  def parseEmail(email: String): Option[String] = {
    "\\w\\S+[@]\\w+[.]\\w+".r findFirstMatchIn email match {
      case Some(r) => Some(r.toString)
      case _ => None
    }
  }

  def parseGender(gender: String): Option[String] = {
    val genders = Map("masc" -> "M", "hom" -> "M", "fem" -> "F", "mulh" -> "F")
    parseStringByPrimitive(normalize(gender), genders)
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

  // TODO: Use Levenshtein distance algorithm with synonymous

  def inferHabitationTypeFromRawNormalizedText(normalizedRawText: String): Option[String] = {
    val habitationTypes = Map("apart" -> HabitationEnum.Apartment, "cas" -> HabitationEnum.Home, "kit" -> HabitationEnum.Kitnet)
    parseStringByPrimitive(normalizedRawText, habitationTypes)
  }

  def inferNegotiatorTypeFromRawNormalizedText(normalizedRawText: String): Option[String] = {
    val negotiatorType = Map("don" -> NegotiatorEnum.Owner, "proprietari" -> NegotiatorEnum.Owner, "imobiliari" -> NegotiatorEnum.RealState)
    parseStringByPrimitive(normalizedRawText, negotiatorType)
  }

  def inferContractTypeFromRawNormalizedText(normalizedRawText: String): Option[String] = {
    val contractType = Map("alug" -> ContractEnum.Rent)
    parseStringByPrimitive(normalizedRawText, contractType)
  }
}
