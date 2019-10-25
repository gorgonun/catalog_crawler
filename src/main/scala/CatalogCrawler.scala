import org.jsoup.Jsoup

import collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object CatalogCrawler {

  val url = "https://classificados.inf.ufsc.br/latestads.php"

  case class CompleteInfo(description: String, seller: String, email: String, street: String, neighborhood: String, city: String, gender: String, price: String, contract: Boolean, basicsExpenses: Boolean, laundry: Boolean, internet: Boolean, animals: Boolean, expiration: String, postDate: String)

  case class Item(category: String, title: String, link: String, image: String, completeInfo: Option[CompleteInfo] = None) {

    def completeUrl: String = "https://classificados.inf.ufsc.br/" + link

//    def getCompleteInfo: CompleteInfo = {
//      completeInfo match {
//        case Some(r) => r
//        case _ => catchCompleteInfo
//      }
//    }


    def catchCompleteInfo = {
      val doc = Jsoup.connect(completeUrl).get()
      val data = doc
        .selectFirst("table tr td form table tbody")
        .select("tbody")
        .select("tr")

      val attributeList = data.iterator.asScala.map(row => row.select("td")).map(x => x.text).toList//.filter(str => str.size != 2).toMap
      attributeList.foreach(x => println(x))
      //        .select("tr")

//
//      val attributes = data
//          .map(infos => )

//      data
//        .map(infos => Try(CompleteInfo(
//          description = infos.get(17).text,
//          seller = infos.get(20).text,
//          email = infos.get(22).text,
//          price = infos.get(24).text,
//          expiration = infos.get(30).text,
//          postDate = infos.get(5).text,
//          infos.get(6).text,
//          infos.get(7).text,
//          infos.get(8).text,
//          infos.get(1).text,
//          infos.get(1).text,
//          infos.get(1).text,
//          infos.get(1).text,
//          infos.get(1).text,
//
//        )))
//      data.foreach(d => println(d))
    }
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
      .toList

    items.head.catchCompleteInfo
  }

}
