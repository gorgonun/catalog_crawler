package catalog.parsers

import java.time.LocalDate
import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object UFSCParser {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val dbUrl = "jdbc:" + sys.env("DATABASE_URI")
    val connectionProperties = new Properties()
    connectionProperties.setProperty("Driver", "org.postgresql.Driver")

    val table = spark.read.jdbc(dbUrl, "rawitems", connectionProperties)

    val emailUDF = udf(parseEmail _)
    val
    table.withColumn("email", emailUDF(table("email")))

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

  def parseDate(df: DataFrame): DataFrame = {
    df.withColumn("date", col("date").cast("timestamp"))
  }

  def parseGender(gender: Option[String]): Option[String] = {
    gender match {
      case Some(r) if r == "Feminino" => Some("F")
      case Some(r) if r == "Masculino" => Some("M")
      case _ => None
    }
  }


//  def optionToWord(option: Option[Boolean]): String =
//    option match {
//      case Some(r) if r => "Sim"
//      case Some(r) if !r => "Não"
//      case _ => "Não informado"
//    }




//  def sendNotification(filteredItemsWithEmail: Map[String, List[RawItem]], date: LocalDate): Unit = {
//    if (filteredItemsWithEmail.nonEmpty) {
//      val subject = s"Novos achados ${date.toString}"
//      filteredItemsWithEmail.foreach{itemWithEmail =>
//        val message = itemWithEmail._2.map{ item =>
//          s"""
//             |Categoria: ${item.category}
//             |Link: ${item.completeUrl}
//             |Informações:
//             |
//             |Descrição: ${item.completeInfo.get.description}
//             |
//             |Vendedor: ${item.completeInfo.get.seller}
//             |Email: ${item.completeInfo.get.email}
//             |Expiração: ${item.completeInfo.get.expiration}
//             |Cidade: ${item.completeInfo.get.city.getOrElse("Não informado")}
//             |Bairro: ${item.completeInfo.get.neighborhood.getOrElse("Não informado")}
//             |Rua: ${item.completeInfo.get.street.getOrElse("Não informado")}
//             |Preço: ${item.completeInfo.get.price.getOrElse("Não informado")}
//             |Contrato? ${optionToWord(item.completeInfo.get.contract)}
//             |Lavanderia? ${optionToWord(item.completeInfo.get.laundry)}
//             |Internet? ${optionToWord(item.completeInfo.get.internet)}
//             |IPTU, Água, Luz incluso? ${optionToWord(item.completeInfo.get.basicExpenses)}
//             |""".stripMargin
//        }.mkString("\n\n\n")
//        logger.info(s"Sending email with ${itemWithEmail._2.length} finds")
//        sendEmail(subject, message, itemWithEmail._1.split(",").toList)
//      }
//    }
//  }
}
