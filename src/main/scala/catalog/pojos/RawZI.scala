package catalog.pojos

case class RawZI(createdAt: String,
                 id: String,
                 contractType: String,
                 unitTypes: Array[String],
                 suites: Array[String],
                 bathrooms: Array[String],
                 bedrooms: Array[String],
                 pricinginfos: Array[PricingInfoZI],
                 status: String,
                 description: String,
                 title: String,
                 amenities: Array[String],
                 address: AddressZI,
                 account: Option[AccountZI],
                 medias: Option[Array[MediaZI]],
                 link: Option[LinkZI])
