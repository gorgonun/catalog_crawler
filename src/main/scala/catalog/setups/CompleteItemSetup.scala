package catalog.setups

import catalog.parsers.ItemParser
import catalog.pojos.{CompleteItem, RawItem}
import catalog.utils.Common
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Try

object CompleteItemSetup extends Common with Setup {
  override val spark: SparkSession = SparkSession
    .builder()
    .master("local")
    .getOrCreate()

  def parseItem(): Unit = {
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
