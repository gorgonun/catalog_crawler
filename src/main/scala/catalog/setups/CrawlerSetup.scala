package catalog.setups

import catalog.crawlers.UFSCCrawler
import catalog.utils.Commom

object CrawlerSetup extends Commom {

  def main(args: Array[String]): Unit = {
    logger.info("Starting crawlers")

    UFSCCrawler.start()

    logger.info("Finished")
  }
}
