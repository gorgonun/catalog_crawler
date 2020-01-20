package catalog.pojos

case class RawZI(createdAt: String,
                 id: String,
                 suites: Array[String],
                 bathrooms: Array[String],
                 bedrooms: Array[String],
                 pricinginfos: Array[PricingInfo],
                 status: String,
                 description: String,
                 title: String,
                 address: Address,
                 account: Account,
                 medias: Array[Map[String, String]],
                 link: Map[String, String])

case class PricingInfo(yearlyIptu: String,
                       price: String,
                       businessType: String,
                       monthlyCondoFee: String)

case class Address(zipCode: String,
                   city: String,
                   geopoint: String,
                   street: String,
                   state: String,
                   neighborhood: String)

case class Account(name: String,
                   emails: Map[String, String],
                   phones: Map[String, String])
