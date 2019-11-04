import CatalogParser.Item

object Filter {

  def itemFilter(item: Item, categories: Array[String]): Boolean = {
    categories.mkString.split(",") contains item.category
  }

  def value(item: Int, price: Int): Double = {
    1/(item.toDouble/price.toDouble)
  }

  def bool(item: Boolean, desirable: Boolean): Double = {
    if (!desirable || item) 1.0 else 0
  }

  def completeFilter(item: Item, minimumScore: Double, price: Int, laundry: Boolean, internet: Boolean, basicExpenses: Boolean): Boolean = {
    val boolSum = Seq((item.completeInfo.get.laundry.getOrElse(false), laundry),
      (item.completeInfo.get.internet.getOrElse(false), internet),
      (item.completeInfo.get.basicExpenses.getOrElse(false), basicExpenses)).map(x => bool(x._1, x._2)).sum
    val average = (value(item.completeInfo.get.price.getOrElse(price + 1), price) + boolSum) / 4
    average >= minimumScore
  }
}
