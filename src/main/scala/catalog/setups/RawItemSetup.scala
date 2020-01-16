package catalog.setups

import java.util.Properties

import catalog.crawlers.{QACrawler, UFSCCrawler}
import catalog.parsers.QAParser
import catalog.pojos.RawItem
import catalog.utils.Common
import catalog.converters.Converters
import org.apache.spark.sql.{SaveMode, SparkSession}

object RawItemSetup extends Common with Converters with Setup {

  def crawlRawItem(): Unit = {
    import spark.implicits._

    val ufscTable = UFSCCrawler.start()(spark)

    val qaData = QACrawler.crawl()
    val qaTable = QAParser.parse(qaData)(spark).map(convert)

    spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]
      .union(ufscTable)
      .union(qaTable)
      .dropDuplicates("id")
      .write
      .mode(SaveMode.Overwrite)
      .jdbc(url = dbUrl, table = "rawitems", connectionProperties = connectionProperties)
  }
}
