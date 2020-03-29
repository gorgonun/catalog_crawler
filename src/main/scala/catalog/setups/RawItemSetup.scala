package catalog.setups

import catalog.converters.Converters
import catalog.crawlers.{QACrawler, UFSCCrawler, ZICrawler}
import catalog.parsers.{QAParser, UFSCParser, ZIParser}
import catalog.pojos.RawItem
import catalog.utils.Common
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Try

object RawItemSetup extends Common with Converters with Setup {

  override val spark: SparkSession = SparkSession
    .builder()
    .master("local")
    .getOrCreate()

  def crawlRawItem(): Unit = {
    import spark.implicits._

    val ufscData = UFSCCrawler.crawl()
    val ufscTable = UFSCParser.parse(ufscData)(spark)

    val qaData = QACrawler.crawl()
    val qaTable = QAParser.parse(qaData)(spark).map(convert)

    val ziData = ZICrawler.crawl()
    val ziTable = ZIParser.parse(ziData)(spark).map(convert)

    Try {
      spark.read.jdbc(dbUrl, "rawitems", connectionProperties).as[RawItem]
        .union(ufscTable)
        .union(qaTable)
        .union(ziTable)
        .distinct()
    }
      .getOrElse(ufscTable.union(qaTable).union(ziTable))
      .write
      .mode(SaveMode.Overwrite)
      .jdbc(url = dbUrl, table = "rawitems", connectionProperties = connectionProperties)
  }
}
