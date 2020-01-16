package catalog.parsers

import java.sql.Timestamp
import java.time.LocalDate

import catalog.pojos.{CompleteItem, RawItem}
import org.scalatest.{FunSpec, Matchers}

import scala.util.Success

class ItemParserSpec extends FunSpec with Matchers {

  it("should convert text to valid boolean") {
    val texts = Seq("sim", "Sim", "SIm", "SIM", "hjjk")

    texts.map(ItemParser.textToBoolean) shouldBe Seq(Some(true), Some(true), Some(true), Some(true), Some(false))
  }

  it("should parse emails") {
    val validEmail = "jose@maria.com"
    val invalidEmail1 = "jose@maria"
    val invalidEmail2 = "jose.com"

    ItemParser.parseEmail(validEmail) shouldBe Some("jose@maria.com")
    ItemParser.parseEmail(invalidEmail1) shouldBe None
    ItemParser.parseEmail(invalidEmail2) shouldBe None
  }

  it("should parse gender") {
    val validMascGender = Seq("masculino", "Masculino", "MaSculino", "MASCULINO")
    val validFemGender = Seq("feminino", "Feminino", "FemininO", "FEMININO")
    val invalidGender = "j"

    validMascGender.map(ItemParser.parseGender) shouldBe Seq(Some("M"), Some("M"), Some("M"), Some("M"))
    validFemGender.map(ItemParser.parseGender) shouldBe Seq(Some("F"), Some("F"), Some("F"), Some("F"))
    ItemParser.parseGender(invalidGender) shouldBe None
  }

  it("should parse date") {
    val validDates = Seq("23/01/2020 (em 13 dias)", "22/01/2020")
    val invalidDate = "22/"

    validDates.map(ItemParser.parseDate) shouldBe Seq(Success(LocalDate.of(2020, 1, 23)), Success(LocalDate.of(2020, 1, 22)))
    ItemParser.parseDate(invalidDate).isFailure shouldBe true
  }

  it("should convert rawitems in completeitems") {
    val rawItem = RawItem(
      184761,
      "ofertas_de_quartos_vagas_centro",
      "08/01/2020",
      "Alugo quarto em apartamento no Centro, com óti...",
      "layout_images/new/noimg.gif",
      "https://classificados.inf.ufsc.br/detail.php?id=184761",
      Some("Procuramos uma menina tranquila para convivência, que trabalhe/estude, sem vícios, responsável financeiramente e com as tarefas domésticas. O apartamento é todo mobiliado, o quarto não. O apartamento é compartilhado com mais 2 pessoas e possui vaga de garagem aberta. Valor em torno de R$790,00 com aluguel, luz, água, condomínio e internet. Contato falar com Adriana Telefone (48) 9 9991- 3136"),
      Some("Isabela Amorim de Oliveira"),
      Some("23/01/2020 (em 13 dias)"),
      Some("08/01/2020"),
      Some("Contatar Vendedor"),
      Some("790"),
      Some("Rua Maestro Tullo Cavalazzi nº 80, apto 203 - Centro"),
      Some("Centro"),
      Some("Florianópolis"),
      Some("Feminino"))

    val completeItem = CompleteItem(
      184761,
      "ofertas_de_quartos_vagas_centro",
      Timestamp.valueOf("2020-01-08 00:00:00.0"),
      "Alugo quarto em apartamento no Centro, com óti...",
      "https://classificados.inf.ufsc.br/detail.php?id=184761",
      "layout_images/new/noimg.gif",
      Some("Procuramos uma menina tranquila para convivência, que trabalhe/estude, sem vícios, responsável financeiramente e com as tarefas domésticas. O apartamento é todo mobiliado, o quarto não. O apartamento é compartilhado com mais 2 pessoas e possui vaga de garagem aberta. Valor em torno de R$790,00 com aluguel, luz, água, condomínio e internet. Contato falar com Adriana Telefone (48) 9 9991- 3136"),
      Some("Isabela Amorim de Oliveira"),
      Some(Timestamp.valueOf("2020-01-23 00:00:00.0")),
      Some(Timestamp.valueOf("2020-01-08 00:00:00.0")),
      None,
      Some(790),
      Some("Rua Maestro Tullo Cavalazzi nº 80, apto 203 - Centro"),
      Some("Centro"),
      Some("Florianópolis"),
      Some("F"))

    ItemParser.parse(rawItem) shouldBe completeItem
  }
}
