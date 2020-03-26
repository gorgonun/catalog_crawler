package catalog.parsers

import catalog.pojos.{AccountZI, LinkZI, MediaZI, RawZI}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.json4s.{DefaultFormats, JArray, JValue}

object ZIParser {
  def parse(json: JValue)(implicit spark: SparkSession): Dataset[RawZI] = {
    import spark.implicits._

    val parsedJson = parseJson(json)
    spark.createDataset(parsedJson)
  }

  def parseJson(json: JValue): List[RawZI] = {
    implicit val formats: DefaultFormats = DefaultFormats

    (json \ "search" \ "result")
      .findField(_._1 == "listings")
      .map(_._2.extract[JArray].arr
        .map{l => (l \ "listing").extract[RawZI]
          .copy(
            account = (l \ "account").extract[Option[AccountZI]],
            medias = (l \ "medias").extract[Option[List[MediaZI]]],
            link = (l \ "link").extract[Option[LinkZI]])
        })
      .getOrElse(List.empty)
  }
}
