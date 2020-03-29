package catalog.parsers

import org.json4s.{DefaultFormats, JValue}
import org.jsoup.Jsoup
import org.scalatest.{FunSpec, Matchers}
import org.json4s.jackson.Serialization.write
import org.json4s.native.JsonMethods.parse

import scala.io.Source

class UFSCParserSpec extends FunSpec with Matchers {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def parseJsonFileFromResource(filename: String): JValue =
    parse(Source.fromResource(filename).getLines.mkString)

  it("should parse a valid raw item") {
    val rawFile = Source.fromResource("UFSCCrawlerSpec/raw_item.html")
    val rawItem = Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr td")
    val jsonResult = parseJsonFileFromResource("UFSCCrawlerSpec/parse_result.json")

    write(UFSCParser.parse(rawItem).get) shouldBe write(jsonResult)
  }

  it("should fail if a necessary field is not found") {
    val rawFile = Source.fromResource("UFSCCrawlerSpec/raw_item.html")
    val rawItem = Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr td")
    rawItem.get(2).text("")

    UFSCParser.parse(rawItem).isFailure shouldBe true
  }

  it("should get id from link") {
    val link = "https://classificados.inf.ufsc.br/detail.php?id=8184761"
    UFSCParser.getIdFromLink(link) shouldBe "8184761"
  }

  it("should complete rawitem with the complete info") {
    val infoFile = Source.fromResource("UFSCCrawlerSpec/complete_info_page.html")
    val infoPage = Jsoup.parse(infoFile.getLines.mkString)
    val rawFile = Source.fromResource("UFSCCrawlerSpec/raw_item.html")
    val rawItem = UFSCParser.parse(Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr td"))
    val jsonResult = parseJsonFileFromResource("UFSCCrawlerSpec/parse_complete_info_result.json")

    infoFile.close()
    rawFile.close()

    write(UFSCParser.getCompleteInfo(infoPage, rawItem.get)) shouldBe write(jsonResult)
  }
}
