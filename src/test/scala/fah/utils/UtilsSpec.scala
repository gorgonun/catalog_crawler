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
}
