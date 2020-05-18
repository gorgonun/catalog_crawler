package fah.parsers

import java.time.LocalDate

import fah.pojos._
import fah.utils.Utils.{LocalDateSerializer, decodeString}
import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, Formats, JValue}
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source
import scala.util.Success

class ItemParserSpec extends FunSpec with Matchers {

  implicit val formats: DefaultFormats.type = DefaultFormats

  def parseJsonFileFromB64EncodedResource(filename: String): JValue =
    parse(decodeString(Source.fromResource(filename).getLines.mkString))

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
    val validDates = Seq("23/01/2020 ()", "22/01/2020", "2020-01-01T23:59:29Z")
    val invalidDate = "22/"

    validDates.map(ItemParser.parseDate) shouldBe Seq(Success(LocalDate.of(2020, 1, 23)), Success(LocalDate.of(2020, 1, 22)), Success(LocalDate.of(2020, 1, 1)))
    ItemParser.parseDate(invalidDate).isFailure shouldBe true
  }

  it("gets the correct habitation type from text") {
    val habTypes = Seq("casa", "casebre", "apt", "apto", "kitnet", "ponte")
    val expected = Seq(HabitationEnum.Home, HabitationEnum.Home, HabitationEnum.Apartment, HabitationEnum.Apartment, HabitationEnum.Kitnet)
    habTypes.flatMap(ItemParser.inferHabitationTypeFromRawNormalizedText) shouldBe expected
  }

  it("gets the correct negotiator type from text") {
    val negotTypes = Seq("dono", "proprietario", "imobiliaria", "agiota")
    val expected = Seq(NegotiatorEnum.Owner, NegotiatorEnum.Owner, NegotiatorEnum.RealState)
    negotTypes.flatMap(ItemParser.inferNegotiatorTypeFromRawNormalizedText) shouldBe expected
  }

  it("gets the correct contract type from text") {
    val contTypes = Seq("aluguel", "noturno")
    val expected = Seq(ContractEnum.Rent)
    contTypes.flatMap(ItemParser.inferContractTypeFromRawNormalizedText) shouldBe expected
  }

  it("gets the element with a custom function") {
    val testStrs = Seq("some", "nop")
    ItemParser.inferFromList(testStrs, s => Option(s)) shouldBe Some("some")
  }

  it("should convert rawitems to completeitems") {
    implicit val formats: Formats = DefaultFormats ++ List(LocalDateSerializer)

    val jsonRawItem = parseJsonFileFromB64EncodedResource("ItemParser/raw_item")
    val jsonParsed = parseJsonFileFromB64EncodedResource("ItemParser/parser_result")

    ItemParser.parse(jsonRawItem.extract[RawItem]) shouldBe jsonParsed.extract[CompleteItem]
  }
}
