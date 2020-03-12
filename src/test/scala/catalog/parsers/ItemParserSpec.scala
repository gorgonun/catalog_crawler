package catalog.parsers

import java.sql.Timestamp
import java.time.LocalDate

import catalog.pojos.{CompleteItem, ContractEnum, HabitationEnum, RawItem}
import org.scalatest.{FunSpec, Matchers}

import scala.util.Success

class ItemParserSpec extends FunSpec with Matchers {

  it("should infer habitation type from category") {
    val categories = Seq("aluga_se_casa", "casa_para_", "tenho_uma_casinha_na", "um_apartamento", "novo_apart_", "kitnet_na", "", "jose")
    val expected = Seq(HabitationEnum.Home, HabitationEnum.Home, HabitationEnum.Home, HabitationEnum.Apartment, HabitationEnum.Apartment, HabitationEnum.Kitnet)
    categories.flatMap(c => ItemParser.inferHabitationTypeFromRawNormalizedText(c)) shouldBe expected
  }

  it("should convert text to valid boolean") {
    val texts = Seq("sim", " Sim", "__SIm", "SIM", "sI", "nao", "NAo", "na", "jri", "oi")

    texts.flatMap(ItemParser.textToBoolean) shouldBe Seq(true, true, true, true, true, false ,false, false)
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
    val validMascGender = Seq("masculino", "masc", "homem", "MaSculino", "MASCULINO", "homen")
    val validFemGender = Seq("feminino", "fem", "FemininO", "FEMININO", "mulher", "mulhe")
    val invalidGender = "j"

    validMascGender.flatMap(ItemParser.parseGender) shouldBe Seq("M", "M", "M", "M", "M", "M")
    validFemGender.flatMap(ItemParser.parseGender) shouldBe Seq("F", "F", "F", "F", "F", "F")
    ItemParser.parseGender(invalidGender) shouldBe None
  }

  it("should parse date") {
    val validDates = Seq("23/01/2020 (em 13 dias)", "22/01/2020", "2020-01-01T23:59:29Z")
    val invalidDate = "22/"

    validDates.map(ItemParser.parseDate) shouldBe Seq(Success(LocalDate.of(2020, 1, 23)), Success(LocalDate.of(2020, 1, 22)), Success(LocalDate.of(2020, 1, 1)))
    ItemParser.parseDate(invalidDate).isFailure shouldBe true
  }

  it("should convert rawitems in completeitems") {
    val rawItem = RawItem(
      id = 184761,
      postDate = "08/01/2020",
      title = "Alugo quarto em apartamento no Centro, com óti...",
      link = "https://classificados.inf.ufsc.br/detail.php?id=184761",
      category = Some("ofertas_de_quartos_vagas_centro"),
      images = None,
      description = Some("Procuramos uma menina tranquila para convivência, que trabalhe/estude, sem vícios, responsável financeiramente e com as tarefas domésticas. O apartamento é todo mobiliado, o quarto não. O apartamento é compartilhado com mais 2 pessoas e possui vaga de garagem aberta. Valor em torno de R$790,00 com aluguel, luz, água, condomínio e internet. Contato falar com Adriana Telefone (48) 9 9991- 3136"),
      sellerName = Some("Isabela Amorim de Oliveira"),
      expirationDate = Some("23/01/2020 (em 13 dias)"),
      sellerEmail = Some("Contatar Vendedor"),
      price = Some("790"),
      street = Some("Rua Maestro Tullo Cavalazzi nº 80, apto 203 - Centro"),
      neighborhood = Some("Centro"),
      city = Some("Florianópolis"),
      gender = Some("Feminino"))

    val completeItem = CompleteItem(
      id = 184761,
      categories = List(Some(HabitationEnum.Apartment.toString), None, Some(ContractEnum.Rent.toString)),
      postDate = Timestamp.valueOf("2020-01-08 00:00:00.0"),
      title = "Alugo quarto em apartamento no Centro, com óti...",
      link = "https://classificados.inf.ufsc.br/detail.php?id=184761",
      description = Some("Procuramos uma menina tranquila para convivência, que trabalhe/estude, sem vícios, responsável financeiramente e com as tarefas domésticas. O apartamento é todo mobiliado, o quarto não. O apartamento é compartilhado com mais 2 pessoas e possui vaga de garagem aberta. Valor em torno de R$790,00 com aluguel, luz, água, condomínio e internet. Contato falar com Adriana Telefone (48) 9 9991- 3136"),
      sellerName = Some("Isabela Amorim de Oliveira"),
      expiration = Some(Timestamp.valueOf("2020-01-23 00:00:00.0")),
      price = Some(790),
      street = Some("Rua Maestro Tullo Cavalazzi nº 80, apto 203 - Centro"),
      neighborhood = Some("Centro"),
      city = Some("Florianópolis"),
      habitation = Some("apartamento"),
      contractType = Some("aluguel"),
      gender = Some("F"))

    ItemParser.parse(rawItem) shouldBe completeItem
  }
}
