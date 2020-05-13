package fah

import fah.datasource.Datasource
import fah.parsers.{ItemParser, RawItemQueryParser}
import fah.utils.Common
import fah.utils.Queries._

object CompleteItemSetup extends Common {
  val completeItemsTable: Table = Table("completeitems")
  val completeItemsTempTable: Table = Table("temp_completeitems")
  val rawItemsTable: Table = Table("rawitems")

  val rawItemsDb: DB = DB("rawitems")
  val completeItemsDB: DB = DB("completeitems")

  def main(args: Array[String]): Unit = {
    Datasource.start()

    val rawitems = readOnlyQuery(rawItemsDb, select(rawItemsTable).map(RawItemQueryParser.parse).list)
    val completeItems = rawitems.map(ItemParser.parse)

    autoCommitQuery(completeItemsDB, createTable(completeItemsTempTable))
    completeItems.map(i => updateQuery(completeItemsDB, insert(i)(completeItemsTempTable)))
    autoCommitQuery(completeItemsDB, drop(completeItemsTable))
    autoCommitQuery(completeItemsDB, rename(completeItemsTempTable)(completeItemsTable))
  }
}
