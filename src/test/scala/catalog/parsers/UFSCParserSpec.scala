package catalog.parsers

import catalog.pojos.RawItem
import org.jsoup.Jsoup
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source
import scala.util.Success

class UFSCParserSpec extends FunSpec with Matchers {
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

  it("should get id from link") {
    val link = "https://classificados.inf.ufsc.br/detail.php?id=8184761"
    UFSCParser.getIdFromLink(link) shouldBe Some(8184761)
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
