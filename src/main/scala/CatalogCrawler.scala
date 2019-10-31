import java.time.LocalDate

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object CatalogCrawler {

  val url = "https://classificados.inf.ufsc.br/latestads.php?offset="

  case class CompleteInfo(description: String, seller: String, email: Option[String], expiration: LocalDate, postDate: LocalDate, price: Option[Int] = None, street: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, gender: Option[String] = None, contract: Option[Boolean] = None, basicExpenses: Option[Boolean] = None, laundry: Option[Boolean] = None, internet: Option[Boolean] = None, animals: Option[Boolean] = None)

  case class Item(category: String, date: LocalDate, title: String, link: String, image: String) {

    def completeUrl: String = "https://classificados.inf.ufsc.br/" + link

    def completeInfo: CompleteInfo = {
      val doc = page(completeUrl)
      val data = doc
        .selectFirst("table tr td form table tbody")
        .select("tbody tr")

      val description = data.select("td").get(1).text
      val mp = data.iterator.asScala.map(x => x.select("td")).map(x => x.iterator.asScala.toSeq).filter(x => x.length == 2).map(x => (x.head.text.replace(" ", "_").replace(":", "").toLowerCase, x(1).text)).toMap

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
    val l = date.split("/")
    LocalDate.of(l(2).split(" ").head.toInt, l(1).toInt, l.head.toInt)
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
          if (pageNumber > (limit - 1) * 5) throw new VerifyError("Page number was greater than the limit")
          page(url + pageNumber.toString, sleep)
            .selectFirst("table[class=box]")
            .select("tr")
      }
      .takeWhile{
        doc =>
          parseDate(doc.get(1).select("td").get(2).text) isEqual date
    }
  }

  def main(args: Array[String]): Unit = {
    require(args.length == 1, "Usage: CatalogCrawler category1,category2...,categoryN")
    val sleep = 2000
    val today = LocalDate.now()
    val pages = pagesToParse(url, today, sleep)

    val rows = pages
        .flatMap{
          page =>
            page
              .iterator
              .asScala
              .map(rows => rows.select("td"))
        }

    val items = rows
      .map {
        items => Try(
          Item(
            category = items.get(0).text,
            title = items.get(1).text,
            link = items.get(1).select("a").first.attr("href"),
            date = parseDate(items.get(2).text),
            image = items.get(3).select("img").first.attr("src"))
        )
      }
      .flatMap{
        case Success(r) => Some(r)
        case Failure(_) => None
      }
      .toList

    println(items)

//    items.foreach(x => println(x.completeInfo))
//    println(items.head.completeInfo)
  }

}
