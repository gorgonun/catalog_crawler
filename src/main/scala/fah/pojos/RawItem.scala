package fah.pojos

case class RawItem(id: String,
                   postDate: String,
                   title: String,
                   link: String,
                   entity: String,
                   originalSource: String,
                   category: Option[String] = None,
                   images: List[String] = List.empty,
                   description: Option[String] = None, // TODO: Spark NLP?
                   expirationDate: Option[String] = None,
                   price: Option[String] = None,
                   street: Option[String] = None,
                   neighborhood: Option[String] = None,
                   city: Option[String] = None,
                   gender: Option[String] = None,
                   contract: Option[String] = None,
                   waterIncluded: Option[String] = None,
                   lightIncluded: Option[String] = None,
                   internetIncluded: Option[String] = None,
                   laundry: Option[String] = None,
                   washingMachine: Option[String] = None,
                   animalsAllowed: Option[String] = None,
                   rentPrice: Option[String] = None,
                   iptuPrice: Option[String] = None,
                   managerFee: Option[String] = None,
                   stove: Option[String] = None,
                   fridge: Option[String] = None,
                   furnished: Option[String] = None,
                   habitationType: Option[String] = None,
                   negotiatorType: Option[String] = None,
                   contractType: Option[String] = None,
                   active: Option[String] = None,
                   sellerName: Option[String] = None,
                   sellerEmail: Option[String] = None)
