package catalog.utils

import org.scalatest.{FunSpec, Matchers}

class UtilsSpec extends FunSpec with Matchers {
  it("should normalize strings") {
    val text = "A DòNÃ                   aranha: - , subiu.{}ç"

    Utils.normalize(text) shouldBe "a_dona_aranha_subiuc"
  }
}
