package catalog.setups

import java.util.Properties

import org.apache.spark.sql.SparkSession

trait Setup {
  val spark: SparkSession = SparkSession
    .builder()
    .master("local")
    .getOrCreate()

  val dbUrl: String = "jdbc:" + sys.env("DATABASE_URL")

  val connectionProperties = new Properties()
  connectionProperties.setProperty("Driver", "org.postgresql.Driver")
}
