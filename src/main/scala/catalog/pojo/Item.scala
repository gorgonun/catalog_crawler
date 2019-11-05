package catalog.pojo

import java.time.LocalDate

import catalog.CatalogParser.{normalize, page, parseDate, parseGender}

import scala.collection.JavaConverters._

case class Item(category: String, date: LocalDate, title: String, link: String, image: String, completeInfo: Option[CompleteInfo] = None) {

  def completeUrl: String = "https://classificados.inf.ufsc.br/" + link

  def getCompleteInfo: CompleteInfo = {
    val doc = page(completeUrl)
    val data = doc
      .selectFirst("table tr td form table tbody")
      .select("tbody tr")

    val description = data.select("td").get(1).text
    val mp = data.iterator.asScala
      .map(_.select("td"))
      .map(_.iterator.asScala.toSeq)
      .filter(_.length == 2)
      .map(x => (normalize(x.head.text), x(1).text))
      .toMap

    CompleteInfo(
      description = description,
      seller = mp("vendido_por"),
      email = parseEmail(("email")),
      expiration = parseDate(mp("anúncio_expira")),
      postDate = parseDate(mp("adicionado")),
      city = mp.get("cidade"),
      neighborhood = mp.get("bairro"),
      street = mp.get("logradouro_nº"),
      price = parsePrice(mp.get("preço")),
      gender = parseGender(mp.get("gênero")),
      animals = textToBoolean(mp.get("permitido_animais?")),
      contract = textToBoolean(mp.get("necessita_contrato?")),
      laundry = textToBoolean(mp.get("lavanderia_disponível?")),
      internet = textToBoolean(mp.get("conexão_c_internet?")),
      basicExpenses = textToBoolean(mp.get("água_cond_e_iptu_inclusos?")),
    )
  }
  def textToBoolean(text: Option[String]): Option[Boolean] =
    text match {
      case Some(text) if (text.toLowerCase == "sim") => Some(true)
      case _ => Some(false)
    }

  def parsePrice(price: Option[String]): Option[Int] = {
    val noDecimal = price.getOrElse("").split(",").head
    "[\\d.]+".r findFirstMatchIn noDecimal match {
      case Some(r) => Some(r.toString.replace(".", "").toInt)
      case _ => None
    }
  }

  def parseEmail(email: String): Option[String] = {
    "\\w\\S+[@]\\w+[.]\\w+".r findFirstMatchIn email match {
      case Some(r) => Some(r.toString)
      case _ => None
    }
  }
}
