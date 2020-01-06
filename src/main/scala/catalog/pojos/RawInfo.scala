package catalog.pojos

case class RawInfo(link: String,
                   description: String,
                   seller: String,
                   expiration: String,
                   postDate: String,
                   email: Option[String] = None,
                   price: Option[String] = None,
                   street: Option[String] = None,
                   neighborhood: Option[String] = None,
                   city: Option[String] = None,
                   gender: Option[String] = None,
                   contract: Option[String] = None,
                   basicExpenses: Option[String] = None,
                   laundry: Option[String] = None,
                   internet: Option[String] = None,
                   animals: Option[String] = None)
