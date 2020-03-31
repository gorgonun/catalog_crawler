package fah.pojos

object HabitationEnum extends Enumeration {
  type Value = String

  val Home: Value = "casa"
  val Apartment: Value = "apartamento"
  val Kitnet: Value = "kitnet"
}
