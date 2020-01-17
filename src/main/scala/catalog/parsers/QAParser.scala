package catalog.parsers

import catalog.pojos.RawQA
import org.apache.spark.sql.{Dataset, SparkSession}
import org.json4s.{DefaultFormats, JArray, JValue}

object QAParser {
  def parse(json: JValue)(implicit spark: SparkSession): Dataset[RawQA] = {
    import spark.implicits._
    implicit val formats: DefaultFormats.type = DefaultFormats

    val parsedJson = (json \ "hits")
      .findField(_._1 == "hit")
      .map {_._2.extract[JArray].arr
        .flatMap(_.findField(_._1 == "fields")
          .map(_._2.extract[RawQA]))}
      .getOrElse(List.empty)

    spark.createDataset(parsedJson)
  }
}
