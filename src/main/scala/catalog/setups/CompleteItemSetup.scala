package catalog.setups

import catalog.parsers.ItemParser
import catalog.pojos.RawItem
import catalog.utils.Common
import org.apache.spark.sql.SaveMode

object CompleteItemSetup extends Common with Setup {

  def parseItem(): Unit = {
    import spark.implicits._

    val rawItems = spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]
    val completeItems = ItemParser.parse(rawItems)(spark)
    completeItems.write.mode(SaveMode.Append).jdbc(url = dbUrl, table = "completeitems", connectionProperties = connectionProperties)
  }
}
