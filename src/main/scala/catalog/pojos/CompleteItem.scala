package catalog.pojos

import java.sql.Timestamp

import catalog.pojos.ContractEnum.ContractEnum
import catalog.pojos.HabitationEnum.HabitationEnum
import catalog.pojos.NegotiatorEnum.NegotiatorEnum

case class CompleteItem(id: Int,
                        category: String, // TODO: Remove category and replace with habitation + negotiator + contractType
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
                        basicExpenses: Option[Boolean] = None, // TODO: separate between water, light, gas, etc
                        laundry: Option[Boolean] = None,
                        internet: Option[Boolean] = None,
                        animals: Option[Boolean] = None,
                        rent: Option[Int] = None,
                        stove: Option[Boolean] = None,
                        fridge: Option[Boolean] = None,
                        habitation: Option[HabitationEnum.Value] = None,
                        negotiator: Option[NegotiatorEnum] = None,
                        contractType: Option[ContractEnum] = None,
                        active: Boolean = true,
                        furnished: Option[Boolean] = None)

object NegotiatorEnum extends Enumeration {
  type NegotiatorEnum = Value

  val Owner: Value = Value("dono")
  val RealState: Value = Value("imobiliária")
}

object ContractEnum extends Enumeration {
  type ContractEnum = Value

  val Rent: Value = Value("aluguel")
}
