package catalog.pojo

case class ScoreItem(
                      categories: List[String],
                      email: Option[Boolean] = None,
                      price: Option[Int] = None,
                      neighborhood: Option[String] = None,
                      city: Option[String] = None,
                      gender: Option[Int] = None,
                      contract: Option[Boolean] = None,
                      basicExpenses: Option[Boolean] = None,
                      laundry: Option[Boolean] = None,
                      internet: Option[Boolean] = None,
                      animals: Option[Boolean] = None
                    )
