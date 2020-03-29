package catalog.crawlers

import org.json4s.{DefaultFormats, JArray, JValue}
import org.json4s.native.JsonMethods.parse
import scalaj.http.Http

object ZICrawler {
  def crawl(): JValue = {
    implicit val formats: DefaultFormats.type = DefaultFormats

    val url = "https://glue-api.zapimoveis.com.br/v2/listings?0=U&1=n&2=i&3=t&4=T&5=y&6=p&7=e&8=_&9=N&10=O&11=N&12=E&addressCountry=&addressState=Santa+Catarina&addressCity=Florian%C3%B3polis&addressZone=&addressNeighborhood=&addressStreet=&addressLocationId=BR%3ESanta+Catarina%3ENULL%3EFlorianopolis&addressPointLat=-27.598639&addressPointLon=-48.518722&business=RENTAL&listingType=USED&portal=ZAP&categoryPage=RESULT&includeFields=search,page,fullUriFragments,developments,superPremium&developmentsSize=3&superPremiumSize=3&__zt="
    val resp = Http(url).headers(Seq(("X-Domain", "www.zapimoveis.com.br"))).asString.body
    parse(resp)
  }
}
