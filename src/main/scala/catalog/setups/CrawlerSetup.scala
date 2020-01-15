package catalog.setups

import catalog.crawlers.UFSCCrawler
import catalog.utils.Common

object CrawlerSetup extends Common {

  def main(args: Array[String]): Unit = {
    logger.info("Starting crawlers")

    UFSCCrawler.start()

    logger.info("Finished")
  }
}
