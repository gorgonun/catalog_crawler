package fah.utils

import org.scalatest.{FunSpec, Matchers}

class UtilsSpec extends FunSpec with Matchers {
  it("should normalize strings") {
    val text = "A DòNÃ                   aranha: - , subiu.{}ç"

    Utils.normalize(text) shouldBe "a_dona_aranha_subiuc"
  }

  it("should parse decimal to int") {
    val validPrice1 = "110.0"
    val validPrice2 = "111"
    val validPrice3 = "555.55555"
    val invalidPrice = "j"

    Utils.parseInt(validPrice1) shouldBe Some(110)
    Utils.parseInt(validPrice2) shouldBe Some(111)
    Utils.parseInt(validPrice3) shouldBe Some(555)
    Utils.parseInt(invalidPrice) shouldBe None
  }

  it("parses str option to int option") {
    Utils.parseInt(Some("300")) shouldBe Some(300)
    Utils.parseInt(None) shouldBe None
  }
}
