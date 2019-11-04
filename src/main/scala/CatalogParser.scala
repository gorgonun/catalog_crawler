import java.time.LocalDate

import Email.sendEmail
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import collection.JavaConverters._
import scala.util.Try

object CatalogParser {
  case class CompleteInfo(description: String, seller: String, email: Option[String], expiration: LocalDate, postDate: LocalDate, price: Option[Int] = None, street: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, gender: Option[String] = None, contract: Option[Boolean] = None, basicExpenses: Option[Boolean] = None, laundry: Option[Boolean] = None, internet: Option[Boolean] = None, animals: Option[Boolean] = None)

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
        .map(x => (x.head.text.replace(" ", "_").replace(":", "")
          .toLowerCase, x(1).text))
        .toMap

      CompleteInfo(
        description = description,
        seller = mp("vendido_por"),
        email = parseEmail(("email")),
        expiration = parseDate(mp("anúncio_expira")),
        postDate = parseDate(mp("adicionado")),
        city = mp.get("cidade"),
        neighborhood = mp.get("bairro"),
        street = mp.get("logradouro,_nº"),
        price = parsePrice(mp.get("preço")),
        contract = textToBoolean(mp.get("necessita_contrato?").orElse(None)),
        laundry = textToBoolean(mp.get("lavanderia_disponível?").orElse(None)),
        internet = textToBoolean(mp.get("conexão_c/internet?").orElse(None)),
        basicExpenses = textToBoolean(mp.get("água,_cond._e_iptu_inclusos?").orElse(None)),
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

  def parseDate(date: String): LocalDate = {
    val dateAsText = date.split("/")
    LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)
  }

  def page(url: String, sleep: Int = 2000): Document = {
    Thread.sleep(sleep)
    println(s"Getting page $url")
    Jsoup.connect(url).get()
  }

  def pagesToParse(url: String, date: LocalDate, sleep: Int, limit: Int = 5): Stream[Elements] = {
    Stream.iterate(0)(_ + 15)
      .map{
        pageNumber =>
          if (pageNumber > (limit - 1) * 15) throw new VerifyError("Page number was greater than the limit")
          page(url + pageNumber.toString, sleep)
            .selectFirst("table[class=box]")
            .select("tr")
      }
      .takeWhile{
        doc =>
          parseDate(doc.get(1).select("td").get(2).text) isEqual date
      }
  }

  def optionToWord(option: Option[Boolean]): String =
    option match {
      case Some(r) if r => "Sim"
      case Some(r) if !r => "Não"
      case _ => "Não informado"
    }

  def parse(items: Elements): Try[Item] = {
    Try(
      Item(
        category = items.get(0).text.replace(" ", "_").replace("/", "_").toLowerCase,
        title = items.get(1).text,
        link = items.get(1).selectFirst("a").attr("href"),
        date = parseDate(items.get(2).text),
        image = items.get(3).selectFirst("img").attr("src"))
    )
  }

  def sendNotification(items: List[Item], date: LocalDate): Unit = {
    if (items.nonEmpty) {
      val subject = s"Novos achados ${date.toString}"
      val message = items.map{item =>
        s"""
           |Categoria: ${item.category}
           |Link: ${item.completeUrl}
           |Informações:
           |
           |Descrição: ${item.completeInfo.get.description}
           |
           |Vendedor: ${item.completeInfo.get.seller}
           |Email: ${item.completeInfo.get.email}
           |Expiração: ${item.completeInfo.get.expiration}
           |Cidade: ${item.completeInfo.get.city.getOrElse("Não informado")}
           |Bairro: ${item.completeInfo.get.neighborhood.getOrElse("Não informado")}
           |Rua: ${item.completeInfo.get.street.getOrElse("Não informado")}
           |Preço: ${item.completeInfo.get.price.getOrElse("Não informado")}
           |Contrato? ${optionToWord(item.completeInfo.get.contract)}
           |Lavanderia? ${optionToWord(item.completeInfo.get.laundry)}
           |Internet? ${optionToWord(item.completeInfo.get.internet)}
           |IPTU, Água, Luz incluso? ${optionToWord(item.completeInfo.get.basicExpenses)}
           |""".stripMargin
      }.mkString("\n\n\n")
      println(s"Sending email with ${items.length} finds")
      sendEmail(subject, message)
    }
  }
}
