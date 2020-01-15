package catalog.parsers

import java.sql.Timestamp
import java.time.LocalDate
import java.util.Properties

import catalog.pojos.{CompleteItem, RawItem}
import catalog.utils.Common
import catalog.utils.Utils.normalize
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Try

object UFSCParser extends Common {

  def start(): Unit = {
    logger.info("Starting ufsc parser")
    val spark = SparkSession
      .builder()
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val dbUrl = "jdbc:" + sys.env("DATABASE_URL")
    val connectionProperties = new Properties()
    connectionProperties.setProperty("Driver", "org.postgresql.Driver")

    val rawItems = spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]
    val completeItems = rawItems.map(parse)

    completeItems.write.mode(SaveMode.Append).jdbc(url = dbUrl, table = "completeitems", connectionProperties = connectionProperties)

    logger.info("Finished ufsc parser")
  }

  def parse(rawItem: RawItem): CompleteItem = {
    CompleteItem(
      category = normalize(rawItem.category),
      date = localDateAsTimestamp(parseDate(rawItem.date).get),
      title = rawItem.title,
      image = rawItem.image,
      link = rawItem.link,
      description = rawItem.description,
      seller = rawItem.seller,
      expiration = rawItem.expiration.flatMap(r => parseDate(r).toOption.map(localDateAsTimestamp)),
      postDate = rawItem.postDate.flatMap(r => parseDate(r).toOption.map(localDateAsTimestamp)),
      email = rawItem.email.flatMap(parseEmail),
      price = rawItem.price.flatMap(parsePrice),
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

  def parsePrice(price: String): Option[Int] = {
    val noDecimal = price.split(",").head
    "[\\d+]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.toInt)
      case _ => None
    }
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
      val finalDate = Try(LocalDate.parse(date))
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
