import CatalogCrawler.Item

object Filter {

  def itemFilter(item: Item, categories: Array[String]): Boolean = {
    categories.mkString.split(",") contains item.category
  }

  def completeFilter(item: Item, price: Int, laundry: Boolean, internet: Boolean, basicExpenses: Boolean) = {
    (item.completeInfo.get.price.getOrElse(price + 1) <= price) && (item.completeInfo.get.laundry.getOrElse(false) == laundry) &&
      item.completeInfo.get.internet.getOrElse(false) == internet && item.completeInfo.get.basicExpenses.getOrElse(false) == basicExpenses
  }
}
