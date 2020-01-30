package catalog.pojos

object HabitationEnum extends Enumeration {
  type HabitationEnum = Value

  val Home: Value = Value("casa")
  val Apartment: Value = Value("apartamento")
  val Kitnet: Value = Value("kitnet")
}
