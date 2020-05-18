package fah.utils

import org.scalatest.{FunSpec, Matchers}

class UtilsSpec extends FunSpec with Matchers {
  it("should normalize strings") {
    val text = "A DòNÃ                   aranha: - , subiu.{}ç"

    Utils.normalize(text) shouldBe "a_dona_aranha_subiuc"
  }

  it("should parse decimal to int") {
    val invalidPrice = "j"
    val validPrices = Seq("110.0", "111", "555.55555", "R$1.000.000,00", "R$ 1000", "+-8", "8+")
    val expected = Seq(Some(1100), Some(111), Some(55555555), Some(1000000), Some(1000), Some(8), Some(8))

    validPrices.map(Utils.parseInt) shouldBe expected
    Utils.parseInt(invalidPrice) shouldBe None
  }

  it("parses str option to int option") {
    Utils.parseInt(Some("300")) shouldBe Some(300)
    Utils.parseInt(None) shouldBe None
  }

  it("parses string by its primitive map") {
    val primitives = Map(
      "ca" -> "caca",
      "ar" -> "arvore"
    )
    val values = Seq("cac", "arvi")
    values.flatMap(Utils.parseStringByPrimitive(_, primitives)) shouldBe Seq("caca", "arvore")
  }

  it("compress text with b64 encoding") {
    val texts = Seq("test", "bla bla!")
    val expected = Seq("H4sIAAAAAAAAACtJLS4BAAx+f9gEAAAA", "H4sIAAAAAAAAAEvKSVRIyklUBACx6v5ZCAAAAA==")
    texts.map(Utils.toB64Compressed) shouldBe expected
  }

  it("decompress b64 text") {
    val texts = Seq("H4sIAAAAAAAAACtJLS4BAAx+f9gEAAAA", "H4sIAAAAAAAAAEvKSVRIyklUBACx6v5ZCAAAAA==")
    val expected = Seq("test", "bla bla!")
    texts.map(Utils.decodeString) shouldBe expected
  }
}
