package catalog.crawlers

import java.time.LocalDate

import catalog.parsers.UFSCParser
import catalog.pojos.RawItem
import org.jsoup.Jsoup
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source
import scala.util.Success

class UFSCCrawlerSpec extends FunSpec with Matchers {

  it("should retrieve a stream of numbers respecting the step") {
    val result = UFSCCrawler.getPagesNumber(15)
    result.take(5).sum shouldBe 150
  }

  it("should throw error if the limit is reached") {
    val result = UFSCCrawler.getPagesNumber(15)
    an[VerifyError] should be thrownBy result.take(6).sum
  }

  it("should get the relevant table rows") {
    val expectedFile = Source.fromResource("UFSCCrawlerSpec/table_rows.html")
    val expected = Jsoup.parse(expectedFile.getLines.mkString).selectFirst("table").select("tr")
    val resultFile = Source.fromResource("UFSCCrawlerSpec/complete_page.html")
    val result = UFSCCrawler.getBodyElements(Jsoup.parse(resultFile.getLines.mkString))
    expectedFile.close()
    resultFile.close()

    result.size shouldBe expected.size
  }

  it("should return true when date is greater or equals today") {
    val pageFile = Source.fromResource("UFSCCrawlerSpec/complete_page.html")
    val page = UFSCCrawler.getBodyElements(Jsoup.parse(pageFile.getLines.mkString))
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    page.get(1).select("td").get(2).text(s"${today.getDayOfMonth}/${today.getMonthValue}/${today.getYear}")
    val todayResult = UFSCCrawler.pageDateIsBetween(page, today, tomorrow)
    page.get(1).select("td").get(2).text(s"${tomorrow.getDayOfMonth}/${tomorrow.getMonthValue}/${tomorrow.getYear}")
    val tomorrowResult = UFSCCrawler.pageDateIsBetween(page, today, tomorrow)

    todayResult shouldBe true
    tomorrowResult shouldBe true
  }

  it("should return false when date is lower than today") {
    val pageFile = Source.fromResource("UFSCCrawlerSpec/complete_page.html")
    val page = UFSCCrawler.getBodyElements(Jsoup.parse(pageFile.getLines.mkString))
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    page.get(1).select("td").get(2).text(s"${yesterday.getDayOfMonth}/${yesterday.getMonthValue}/${yesterday.getYear}")
    val yesterdayResult = UFSCCrawler.pageDateIsBetween(page, today, today.plusDays(1))

    yesterdayResult shouldBe false
  }

  it("should parse a valid raw item") {
    val rawFile = Source.fromResource("UFSCCrawlerSpec/raw_item.html")
    val rawItem = Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr td")

    val expected = Success(RawItem(
      184761,
      "08/01/2020",
      "alugo_quarto_em_apartamento_no_centro_com_otima_localizacao_ao_lado_do_shopping_beira_mar",
      "https://classificados.inf.ufsc.br/detail.php?id=184761",
      Some("ofertas_de_quartos_vagas_centro")))

    UFSCParser.parse(rawItem) shouldBe expected
  }

  it("should fail if a necessary field is not found") {
    val rawFile = Source.fromResource("UFSCCrawlerSpec/raw_item.html")
    val rawItem = Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr td")
    rawItem.get(2).text("")

    UFSCParser.parse(rawItem).isFailure shouldBe true
  }

  it("should complete rawitem with the complete info") {
    val infoFile = Source.fromResource("UFSCCrawlerSpec/complete_info_page.html")
    val infoPage = Jsoup.parse(infoFile.getLines.mkString)
    val rawFile = Source.fromResource("UFSCCrawlerSpec/raw_item.html")
    val rawItem = UFSCParser.parse(Jsoup.parse(rawFile.getLines.mkString).selectFirst("table").select("tr td"))

    infoFile.close()
    rawFile.close()

    val expected = RawItem(
      id = 184761,
      postDate = "08/01/2020",
      title = "alugo_quarto_em_apartamento_no_centro_com_otima_localizacao_ao_lado_do_shopping_beira_mar",
      link = "https://classificados.inf.ufsc.br/detail.php?id=184761",
      category = Some("ofertas_de_quartos_vagas_centro"),
      description = Some("Procuramos uma menina tranquila para convivência, que trabalhe/estude, sem vícios, responsável financeiramente e com as tarefas domésticas. O apartamento é todo mobiliado, o quarto não. O apartamento é compartilhado com mais 2 pessoas e possui vaga de garagem aberta. Valor em torno de R$790,00 com aluguel, luz, água, condomínio e internet. Contato falar com Adriana Telefone (48) 9 9991- 3136"),
      expirationDate = Some("23/01/2020 (em 13 dias)"),
      price = Some("790"),
      street = Some("Rua Maestro Tullo Cavalazzi nº 80, apto 203 - Centro"),
      neighborhood = Some("Centro"),
      city = Some("Florianópolis"),
      gender = Some("Feminino"),
      sellerName = Some("Isabela Amorim de Oliveira"),
      sellerEmail = Some("Contatar Vendedor"))

    UFSCParser.getCompleteInfo(infoPage, rawItem.get) shouldBe expected
  }
}
