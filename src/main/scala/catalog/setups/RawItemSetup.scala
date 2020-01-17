package catalog.setups

import catalog.converters.Converters
import catalog.crawlers.{QACrawler, UFSCCrawler}
import catalog.parsers.QAParser
import catalog.pojos.RawItem
import catalog.utils.Common
import org.apache.spark.sql.SaveMode

import scala.util.Try

object RawItemSetup extends Common with Converters with Setup {

  def crawlRawItem(): Unit = {
    import spark.implicits._

    val ufscTable = UFSCCrawler.start()(spark)

    val qaData = QACrawler.crawl()
    val qaTable = QAParser.parse(qaData)(spark).map(convert)

    Try {
      spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]
        .union(ufscTable)
        .union(qaTable)
        .dropDuplicates("id")
    }
      .getOrElse(ufscTable.union(qaTable))
      .write
      .mode(SaveMode.Overwrite)
      .jdbc(url = dbUrl, table = "rawitems", connectionProperties = connectionProperties)
  }
}
