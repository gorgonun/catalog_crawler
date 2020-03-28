package catalog.parsers

import org.json4s.{DefaultFormats, JValue}
import org.json4s.native.JsonMethods.parse
import org.json4s.jackson.Serialization.write
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class ZIParserSpec extends FunSpec with Matchers {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def parseJsonFileFromResource(filename: String): JValue =
    parse(Source.fromResource(filename).getLines.mkString)

  it("parses one api item"){
    val jsonFile = parseJsonFileFromResource("ZIParserSpec/api_item.json")
    val jsonResult = parseJsonFileFromResource("ZIParserSpec/api_result.json")
    write(ZIParser.parseJson(jsonFile).head) shouldBe write(jsonResult)
  }

  it("parses multiple api items"){
    val jsonFile = parseJsonFileFromResource("ZIParserSpec/api_items.json")
    val jsonResult = parseJsonFileFromResource("ZIParserSpec/api_results.json")
    write(ZIParser.parseJson(jsonFile)) shouldBe write(jsonResult)
  }

  it("ignores invalid zi items"){
    val json = parse("{}")
    ZIParser.parseJson(json) shouldBe empty
  }
}
