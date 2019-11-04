import java.time.LocalDate

import CatalogParser._
import collection.JavaConverters._

object CatalogCrawler {

  val url = "https://classificados.inf.ufsc.br/latestads.php?offset="

  def main(args: Array[String]): Unit = {
    require(args.length == 1, "Usage: CatalogCrawler category1,category2...,categoryN")
    val sleep = 2000

    val price = 600
    val laundry = true
    val internet = true
    val basicExpenses = true
    val minimumScore = 1

    val today = LocalDate.now()
    val pages = pagesToParse(url, today, sleep)

    val rows = pages.flatMap(_.iterator.asScala.map(_.select("td")))

    val items = rows
      .map(parse)
      .flatMap(_.toOption)
      .toList
      .filter(Filter.itemFilter(_, args))
      .map(item => item.copy(completeInfo = Some(item.getCompleteInfo)))
      .filter(Filter.completeFilter(_, minimumScore ,price, laundry, internet, basicExpenses))

    sendNotification(items, today)
  }

}
