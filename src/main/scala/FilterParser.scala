//object FilterParser {
//
//  case class Expression(column: Int, operator: String, value: String)
//
//  case class R(expressions: String, columns: Map[String, Int]) {
//
//    def separateExpressions = {
//      val separatedExpressions = expressions.split("and").map{expression => expression.split(" ") match{
//        case (column, operator, value) => Expression(columns(column), operator, value)
//      }}
//      srg(separatedExpressions.toList)
//    }
//
//    def srg(l: List[Expression]): Boolean = {
//      if (l.length == 0) l.head
//      srg(List(parse(l(0)) && parse(l(1))) :: l.takeRight(l.size - 2))
//    }
//  }
//
//  def parse(separatedExpression: Expression) = {
//    stringToOperator(separatedExpression.operator)(separatedExpression.column.toInt, separatedExpression.value.toInt)
//  }
//
//  def parse(b: Boolean) = b
//
//  def stringToOperator(operator: String): (Int, Int) => Boolean = {
//    operator match {
//      case ">" => _ > _
//      case ">=" => _ >= _
//      case "<" => _ < _
//      case "<=" => _ <= _
//      case "=" => _ == _
//      case _ => throw new VerifyError("Invalid operator")
//    }
//  }
//
//}
