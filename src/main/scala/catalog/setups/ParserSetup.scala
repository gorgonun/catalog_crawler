package catalog.setups

import catalog.parsers.UFSCParser
import catalog.utils.Common

object ParserSetup extends Common {

  def main(args: Array[String]): Unit = {
    logger.info("Starting parsers")

    UFSCParser.start()

    logger.info("Finished")
  }
}
