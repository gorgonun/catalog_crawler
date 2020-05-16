package fah.parsers

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}

import fah.pojos._
import fah.utils.Common
import fah.utils.Utils._

import scala.util.Try

object ItemParser extends Common {

  def parse(rawItems: LazyList[RawItem]): LazyList[CompleteItem] = {
    rawItems.map(parse)
  }

  def parse(rawItem: RawItem): CompleteItem = {
    val inferenceTargets = Seq(rawItem.category.getOrElse(""), normalize(rawItem.description.getOrElse("")))
    val ciTemp = CompleteItem(
      id = rawItem.id,
      categories = Array.empty,
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
      habitation = inferFromList(inferenceTargets, inferHabitationTypeFromRawNormalizedText),
      negotiator = inferFromList(inferenceTargets, inferNegotiatorTypeFromRawNormalizedText),
      contractType = inferFromList(inferenceTargets, inferContractTypeFromRawNormalizedText)
    )
    ciTemp.copy(categories = Array(ciTemp.habitation, ciTemp.negotiator, ciTemp.contractType).collect {
      case Some(s) => s
    })
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
    val withSecondsDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    val withMillisDatePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    Try {
      val finalDate = Try(ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(withSecondsDatePattern).withZone(ZoneId.of("UTC"))).toLocalDate)
        .orElse(Try(ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(withMillisDatePattern).withZone(ZoneId.of("UTC"))).toLocalDate))
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

  def inferFromList(targets: Seq[String], inferFunction: String => Option[String]): Option[String] = {
    targets.flatMap(inferFunction).headOption
  }
}
