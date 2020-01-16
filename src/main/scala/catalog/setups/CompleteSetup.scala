package catalog.setups

object CompleteSetup {
  def main(args: Array[String]): Unit = {
    RawItemSetup.crawlRawItem()
    CompleteItemSetup.parseItem()
  }
}
