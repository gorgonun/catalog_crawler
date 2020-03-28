package catalog.parsers

import org.json4s.{DefaultFormats, JValue}
import org.scalatest.{FunSpec, Matchers}
import org.json4s.native.JsonMethods._
import org.json4s.jackson.Serialization.write

import scala.io.Source

class QAParserSpec extends FunSpec with Matchers {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def parseJsonFileFromResource(filename: String): JValue =
    parse(Source.fromResource(filename).getLines.mkString)

  it("should parse one api item"){
    val jsonFile = parseJsonFileFromResource("QAParserSpec/api_item.json")
    val jsonResult = parseJsonFileFromResource("QAParserSpec/api_result.json")

    write(QAParser.parseJson(jsonFile).head) shouldBe write(jsonResult)
  }

  it("should parse multiple api items"){
    val jsonFile = parseJsonFileFromResource("QAParserSpec/api_items.json")
    val jsonResult = parseJsonFileFromResource("QAParserSpec/api_results.json")

    write(QAParser.parseJson(jsonFile)) shouldBe write(jsonResult)
  }

  it("ignores invalid qa items"){
    val json = parse("{}")
    QAParser.parseJson(json) shouldBe empty
  }
}
