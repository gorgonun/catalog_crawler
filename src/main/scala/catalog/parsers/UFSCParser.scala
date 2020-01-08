package catalog.parsers

import java.sql.Timestamp
import java.time.LocalDate
import java.util.Properties

import catalog.pojos.{CompleteItem, RawItem}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, SaveMode, SparkSession}
import catalog.utils.Utils.{normalize, parseDate}

object UFSCParser {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val dbUrl = "jdbc:" + sys.env("DATABASE_URL")
    val connectionProperties = new Properties()
    connectionProperties.setProperty("Driver", "org.postgresql.Driver")

    val rawItems = spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]

    val completeItems = rawItems.map {
      rawItem =>
        CompleteItem(
          category = normalize(rawItem.category),
          date = localDateAsTimestamp(parseDate(rawItem.date).get),
          title = normalize(rawItem.title),
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

    completeItems.write.mode(SaveMode.Append).jdbc(url = dbUrl, table = "completeitems", connectionProperties = connectionProperties)
  }

    def textToBoolean(text: String): Option[Boolean] =
    normalize(text).toLowerCase match {
      case "sim" => Some(true)
      case _ => Some(false)
    }

  def parsePrice(price: String): Option[Int] = {
    val noDecimal = price.split(",").head
    "[\\d.]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.replace(".", "").toInt)
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
    gender match {
      case "Feminino" => Some("F")
      case "Masculino" => Some("M")
      case _ => None
    }
  }

  def localDateAsTimestamp(date: LocalDate): Timestamp = {
    Timestamp.valueOf(date.atStartOfDay)
  }
}
