package catalog

import catalog.pojo.{Item, ScoreItem}

object Filter {

  def itemFilter(item: Item, categories: List[String]): Boolean = {
    categories contains item.category
  }

  def value(item: Int, value: Option[Int]): Double = {
    if (value.isDefined) 1/(item.toDouble/value.get.toDouble) else 1
  }

  def bool(item: Boolean, desirable: Boolean): Double = {
    if (!desirable || item) 1.0 else 0
  }

  def str(item: Option[String], text: Option[String]): Double = {
    if (text.isDefined && text.get == item.getOrElse("")) 1.0 else 0.0
  }

  def completeFilter(item: Item, minimumScore: Double, scoreItem: ScoreItem): Option[ScoreItem] = {
    if (scoreItem.gender.isDefined && item.completeInfo.get.gender.getOrElse(-1) != scoreItem.gender.get) return None
    if (!(scoreItem.categories contains item.category)) return None

    val boolSum = Seq(
      (item.completeInfo.get.email.isDefined, scoreItem.email.getOrElse(false)),
      (item.completeInfo.get.animals.getOrElse(false), scoreItem.animals.getOrElse(false)),
      (item.completeInfo.get.basicExpenses.getOrElse(false), scoreItem.basicExpenses.getOrElse(false)),
      (item.completeInfo.get.contract.getOrElse(false), scoreItem.contract.getOrElse(false)),
      (item.completeInfo.get.laundry.getOrElse(false), scoreItem.laundry.getOrElse(false)),
      (item.completeInfo.get.internet.getOrElse(false), scoreItem.internet.getOrElse(false))
    )
      .map(x => bool(x._1, x._2)).sum
    val strSum = Seq(
      (item.completeInfo.get.neighborhood, scoreItem.neighborhood),
      (item.completeInfo.get.city, scoreItem.city)
    )
      .map(x => str(x._1, x._2)).sum
    val average = (value(item.completeInfo.get.price.getOrElse(1), scoreItem.price) + boolSum + strSum) / 4
    if (average >= minimumScore) Some(scoreItem) else None
  }

  def filterByScoreItems(item: Item, minimumScore: Double, scoreItems: List[ScoreItem]): List[(String, Item)] = {
    scoreItems
      .map(completeFilter(item, minimumScore, _))
      .filter(_.isDefined)
      .map(scoreItem => scoreItem.get.notificationEmails.getOrElse("") -> item)
  }
}
