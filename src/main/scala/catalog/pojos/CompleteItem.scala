package catalog.pojos

import java.sql.Timestamp

case class CompleteItem(category: String,
                        date: Timestamp,
                        title: String,
                        link: String,
                        image: String,
                        description: Option[String] = None,
                        seller: Option[String] = None,
                        expiration: Option[Timestamp] = None,
                        postDate: Option[Timestamp] = None,
                        email: Option[String] = None,
                        price: Option[Int] = None,
                        street: Option[String] = None,
                        neighborhood: Option[String] = None,
                        city: Option[String] = None,
                        gender: Option[String] = None,
                        bathroom: Option[Int] = None,
                        rooms: Option[Int] = None,
                        iptu: Option[Int] = None,
                        floor: Option[Int] = None,
                        area: Option[Int] = None,
                        contract: Option[Boolean] = None,
                        basicExpenses: Option[Boolean] = None,
                        laundry: Option[Boolean] = None,
                        internet: Option[Boolean] = None,
                        animals: Option[Boolean] = None)
