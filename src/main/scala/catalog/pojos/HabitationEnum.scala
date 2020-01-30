package catalog.pojos

object HabitationEnum extends Enumeration {
  type HabitationEnum = Value

  val Home: Value = Value("home")
  val Apartment: Value = Value("apartment")
  val Kitnet: Value = Value("kitnet")
}
