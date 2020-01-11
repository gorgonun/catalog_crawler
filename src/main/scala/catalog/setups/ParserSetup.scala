package catalog.setups

import catalog.parsers.UFSCParser
import catalog.utils.Commom

object ParserSetup extends Commom {

  def main(args: Array[String]): Unit = {
    logger.info("Starting parsers")

    UFSCParser.start()

    logger.info("Finished")
  }
}
