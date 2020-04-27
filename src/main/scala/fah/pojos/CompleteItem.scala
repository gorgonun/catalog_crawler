package fah.pojos

import java.sql.Timestamp

case class CompleteItem(id: String,
                        categories: Array[String], // TODO: Remove category and replace with habitation + negotiator + contractType
                        postDate: Timestamp,
                        title: String,
                        link: String,
                        images: Array[String] = Array.empty,
                        description: Option[String] = None,
                        sellerName: Option[String] = None,
                        expiration: Option[Timestamp] = None,
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
                        basicExpenses: Option[Boolean] = None, // TODO: separate between water, light, gas, etc
                        laundry: Option[Boolean] = None,
                        internet: Option[Boolean] = None,
                        animals: Option[Boolean] = None,
                        rent: Option[Int] = None,
                        stove: Option[Boolean] = None,
                        fridge: Option[Boolean] = None,
                        habitation: Option[HabitationEnum.Value] = None,
                        negotiator: Option[NegotiatorEnum.Value] = None,
                        contractType: Option[ContractEnum.Value] = None,
                        active: Boolean = true,
                        furnished: Option[Boolean] = None)
