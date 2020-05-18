package fah.datasource

import java.net.URI

import scalikejdbc._

case class DbData(url: String, username: String, password: String)

object Datasource {
  def start(): Unit = {
    val rawDbUri = new URI(System.getenv("RAW_ITEMS_DB"))
    val completeDbUri = new URI(System.getenv("COMPLETE_ITEMS_DB"))

    val rawData: DbData = parseDbData(rawDbUri)
    val completeData: DbData = parseDbData(completeDbUri)

    ConnectionPool.singleton(rawData.url, rawData.username, rawData.password)
    ConnectionPool.add(Symbol("rawitems"), rawData.url, rawData.username, rawData.password)
    ConnectionPool.add(Symbol("completeitems"), completeData.url, completeData.username, completeData.password)
  }

  def parseDbData(uri: URI): DbData = {
    val dbUrl = s"jdbc:postgresql://${uri.getHost}:${uri.getPort}${uri.getPath}" + "?sslmode=require"
    val dbUsername: String = uri.getUserInfo.split(":")(0)
    val dbPassword: String = uri.getUserInfo.split(":")(1)
    DbData(url = dbUrl, username = dbUsername, password = dbPassword)
  }
}
