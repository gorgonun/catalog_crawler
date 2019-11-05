package catalog

import java.time.LocalDate

import catalog.CatalogParser.{logger, pagesToParse, parse, sendNotification}
import catalog.Filters.filters

import scala.collection.JavaConverters._

object CatalogCrawler {

  def main(args: Array[String]): Unit = {
    val url = "https://classificados.inf.ufsc.br/latestads.php?offset="

    val sleep = 2000
    val minimumScore = 1

    val today = LocalDate.now()
    val pages = pagesToParse(url, today, sleep)

    val rows = pages.flatMap(_.iterator.asScala.map(_.select("td")))

    val items = rows
      .map(parse)
      .flatMap(_.toOption)
      .toList
      .filter(Filter.itemFilter(_, filters.flatMap(_.categories)))
      .map(item => item.copy(completeInfo = Some(item.getCompleteInfo)))
      .filter(Filter.filterByScoreItems(_, minimumScore, filters))

    sendNotification(items, today)

    logger.info("Success")
  }

}
