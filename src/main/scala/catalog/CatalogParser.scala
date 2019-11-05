package catalog

import java.time.LocalDate

import catalog.Email.sendEmail
import catalog.pojo._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object CatalogParser {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def parseDate(date: String): LocalDate = {
    val dateAsText = date.split("/")
    LocalDate.of(dateAsText(2).split(" ").head.toInt, dateAsText(1).toInt, dateAsText.head.toInt)
  }

  def parseGender(gender: Option[String]): Option[Int] = {
    gender match {
      case Some(r) if (r == "Feminino") => Some(0)
      case Some(r) if (r == "Masculino") => Some(1)
      case _ => None
    }
  }

  def page(url: String, sleep: Int = 2000): Document = {
    Thread.sleep(sleep)
    logger.info(s"Getting page $url")
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
          (parseDate(doc.get(1).select("td").get(2).text) isAfter date) ||
            (parseDate(doc.get(1).select("td").get(2).text) isEqual date)
      }
  }

  def optionToWord(option: Option[Boolean]): String =
    option match {
      case Some(r) if r => "Sim"
      case Some(r) if !r => "Não"
      case _ => "Não informado"
    }

  def normalize(text: String): String = {
    text
      .replace(" ", "_")
      .replace("/", "_")
      .replace("_-_", "_")
      .replace(",", "")
      .replace(".", "")
      .replace(":", "")
      .toLowerCase
  }

  def parse(items: Elements): Try[Item] = {
    Try(
      Item(
        category = normalize(items.get(0).text),
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
      logger.info(s"Sending email with ${items.length} finds")
      sendEmail(subject, message)
    }
  }
}
