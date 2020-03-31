package fah

import java.util.Properties

import fah.parsers.ItemParser
import fah.pojos.{CompleteItem, RawItem}
import fah.utils.Common
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Try

object CompleteItemSetup extends Common {
  val spark: SparkSession = SparkSession
    .builder()
    .master("local")
    .getOrCreate()

  val dbUrl: String = "jdbc:" + sys.env("DATABASE_URL")

  val connectionProperties = new Properties()
  connectionProperties.setProperty("Driver", "org.postgresql.Driver")

  def main(args: Array[String]): Unit = {
    import spark.implicits._

    val rawItems = spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]
    val completeItems = ItemParser.parse(rawItems)(spark)

    Try {
      spark
        .read
        .jdbc(dbUrl, "completeitems", connectionProperties)
        .as[CompleteItem]
        .union(completeItems)
        .distinct()
    }
      .getOrElse(completeItems)
      .write
      .mode(SaveMode.Overwrite)
      .jdbc(url = dbUrl, table = "completeitems", connectionProperties = connectionProperties)
  }
}
