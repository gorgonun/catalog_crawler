package catalog.pojos

case class PricingInfoZI(rentalInfo: RentalInfoZI,
                         yearlyIptu: Option[String],
                         price: String,
                         businessType: String,
                         monthlyCondoFee: Option[String])
