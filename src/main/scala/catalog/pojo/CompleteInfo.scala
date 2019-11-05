package catalog.pojo

import java.time.LocalDate

case class CompleteInfo(
                         description: String,
                         seller: String,
                         email: Option[String],
                         expiration: LocalDate,
                         postDate: LocalDate,
                         price: Option[Int] = None,
                         street: Option[String] = None,
                         neighborhood: Option[String] = None,
                         city: Option[String] = None,
                         gender: Option[Int] = None,
                         contract: Option[Boolean] = None,
                         basicExpenses: Option[Boolean] = None,
                         laundry: Option[Boolean] = None,
                         internet: Option[Boolean] = None,
                         animals: Option[Boolean] = None
                       )

