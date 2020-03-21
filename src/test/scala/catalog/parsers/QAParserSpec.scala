package catalog.parsers

import catalog.pojos.RawQA
import org.scalatest.{FunSpec, Matchers}
import org.json4s.native.JsonMethods._

import scala.io.Source

class QAParserSpec extends FunSpec with Matchers {
  it("should parse one api item"){
    val rawFile = Source.fromResource("QASpec/api_item.json")
    val jsonFile = parse(rawFile.getLines.mkString)
    QAParser.parseJson(jsonFile).head shouldBe RawQA(bairro = Some("test_neighborhood"), cidade = Some("gotham"))
  }

  it("should parse multiple api items"){
    val rawFile = Source.fromResource("QASpec/api_items.json")
    val jsonFile = parse(rawFile.getLines.mkString)
    QAParser.parseJson(jsonFile) shouldBe List(
      RawQA(banheiros = Some("5"), quartos = Some("4"), iptu = Some("77"), andar = Some("5")),
      RawQA(for_rent = Some("sim"), local = Some("4"), suites = Some("77"))
    )
  }
}
