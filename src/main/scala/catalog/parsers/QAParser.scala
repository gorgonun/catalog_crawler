package catalog.parsers

import catalog.pojos.RawQA
import org.apache.spark.sql.{Dataset, SparkSession}
import org.json4s.{DefaultFormats, JArray, JValue}
import org.json4s.jackson.Serialization.write

object QAParser {
  def parse(json: JValue)(implicit spark: SparkSession): Dataset[RawQA] = {
    import spark.implicits._

    val parsedJson = parseJson(json)
    spark.createDataset(parsedJson)
  }

  def parseJson(json: JValue): List[RawQA] = {
    implicit val formats: DefaultFormats.type = DefaultFormats

    (json \ "hits").findField(_._1 == "hit")
      .map {_._2.extract[JArray].arr
        .flatMap(_.findField(_._1 == "fields")
          .map(_._2.extract[RawQA])
          .map(_.copy(originalSource = Some(write(json))
          )))}
      .getOrElse(List.empty)
  }
}
