package catalog.pojos

case class AddressZI(zipCode: String,
                     city: String,
                     street: Option[String],
                     state: String,
                     neighborhood: String)
