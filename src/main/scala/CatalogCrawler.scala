import org.jsoup.Jsoup

import collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object CatalogCrawler {

  val url = "https://classificados.inf.ufsc.br/latestads.php"

  case class Item(category: String, title: String, link: String, image: String) {
    def completeUrl: String = "https://classificados.inf.ufsc.br/" + link
  }

  def main(args: Array[String]): Unit = {

    val doc = Jsoup.connect(url).get()
    val data = doc
      .select("table[class=box]")
      .get(0)
      .select("tr")
      .iterator
      .asScala
      .map(rows => rows.select("td"))

    val items = data
      .map {
        items => Try(
          Item(items.get(0).text, items.get(1).text, items.get(1).select("a").first.attr("href"), items.get(3).select("img").first.attr("src"))
        )
      }
      .flatMap{
        case Success(r) => Some(r)
        case Failure(_) => None
      }
  }

}
