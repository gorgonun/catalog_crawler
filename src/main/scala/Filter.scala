import CatalogCrawler.Item

object Filter {

  def filter(item: Item, categories: List[String], price: Int) = {
    if ((categories contains item.category) && (item.completeInfo.price.getOrElse(0) <= price))
  }
}
