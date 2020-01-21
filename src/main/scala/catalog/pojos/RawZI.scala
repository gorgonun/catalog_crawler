package catalog.pojos

case class RawZI(createdAt: String,
                 id: String,
                 suites: Array[String],
                 bathrooms: Array[String],
                 bedrooms: Array[String],
                 pricinginfos: Array[PricingInfoZI],
                 status: String,
                 description: String,
                 title: String,
                 address: AddressZI,
                 account: Option[AccountZI] = None,
                 medias: Option[Array[MediaZI]] = None,
                 link: Option[LinkZI] = None)
