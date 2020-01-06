package catalog.pojos

import java.time.LocalDate

case class CompleteItem(category: String,
                        date: LocalDate,
                        title: String,
                        link: String,
                        image: String,
                        description: String,
                        seller: String,
                        expiration: LocalDate,
                        postDate: LocalDate,
                        email: Option[String] = None,
                        price: Option[Int] = None,
                        street: Option[String] = None,
                        neighborhood: Option[String] = None,
                        city: Option[String] = None,
                        gender: Option[String] = None,
                        contract: Option[Boolean] = None,
                        basicExpenses: Option[Boolean] = None,
                        laundry: Option[Boolean] = None,
                        internet: Option[Boolean] = None,
                        animals: Option[Boolean] = None)
