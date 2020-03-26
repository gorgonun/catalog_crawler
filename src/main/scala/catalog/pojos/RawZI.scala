package catalog.pojos

case class RawZI(createdAt: String,
                 id: String,
                 contractType: String,
                 unitTypes: List[String],
                 suites: List[String],
                 bathrooms: List[String],
                 bedrooms: List[String],
                 pricingInfos: List[PricingInfoZI],
                 status: String,
                 description: String,
                 title: String,
                 amenities: List[String],
                 address: AddressZI,
                 account: Option[AccountZI],
                 medias: Option[List[MediaZI]],
                 link: Option[LinkZI])
