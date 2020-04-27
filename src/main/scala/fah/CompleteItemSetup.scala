package fah

import fah.datasource.Datasource
import fah.parsers.{CompleteItemQueryParser, ItemParser, RawItemQueryParser}
import fah.utils.Common
import scalikejdbc._

// separate specific things in case class extends AnyVal

object CompleteItemSetup extends Common {
  def main(args: Array[String]): Unit = {

    Datasource.start()

    val rawitems = DB readOnly { implicit session =>
      sql"SELECT * FROM rawitems"
        .map(RawItemQueryParser.parse)
        .list
        .apply()
    }

    val completeItems = rawitems.map(ItemParser.parse)

    NamedDB(Symbol("completeitems")) autoCommit { implicit session =>
      sql"""
           CREATE TABLE IF NOT EXISTS completeitems_temp (
            id TEXT NOT NULL,
            categories TEXT[],
            postDate TIMESTAMP,
            title TEXT NOT NULL,
            link TEXT NOT NULL,
            images TEXT[],
            description TEXT,
            sellerName TEXT,
            expiration TIMESTAMP,
            email TEXT,
            price DECIMAL,
            street TEXT,
            neighborhood TEXT,
            city TEXT,
            gender TEXT,
            bathroom SMALLINT,
            rooms SMALLINT,
            iptu DECIMAL,
            floor SMALLINT,
            area SMALLINT,
            contract BOOLEAN,
            basicExpenses BOOLEAN,
            laundry BOOLEAN,
            internet BOOLEAN,
            animals BOOLEAN,
            rent DECIMAL,
            stove BOOLEAN,
            fridge BOOLEAN,
            habitation TEXT,
            negotiator TEXT,
            contractType TEXT,
            active BOOLEAN NOT NULL DEFAULT TRUE,
            furnished BOOLEAN
            )
           """.execute.apply()
    }

    NamedDB(Symbol("completeitems")) localTx { implicit s =>
      completeItems.foreach(i =>
      sql"""
           INSERT INTO completeitems_temp VALUES (
              ${i.id},
              ${i.categories},
              ${i.postDate},
              ${i.title},
              ${i.link},
              ${i.images},
              ${i.description},
              ${i.sellerName},
              ${i.expiration},
              ${i.email},
              ${i.price},
              ${i.street},
              ${i.neighborhood},
              ${i.city},
              ${i.gender},
              ${i.bathroom},
              ${i.rooms},
              ${i.iptu},
              ${i.floor},
              ${i.area},
              ${i.contract},
              ${i.basicExpenses},
              ${i.laundry},
              ${i.internet},
              ${i.animals},
              ${i.rent},
              ${i.stove},
              ${i.fridge},
              ${i.habitation},
              ${i.negotiator},
              ${i.contractType},
              ${i.active},
              ${i.furnished}
           )
           """.update.apply()
      )
    }

    NamedDB(Symbol("completeitems")) autoCommit { implicit session =>
      sql"""
           DROP TABLE IF EXISTS completeitems
           """.execute().apply()
    }

    NamedDB(Symbol("completeitems")) autoCommit { implicit session =>
      sql"""
           ALTER TABLE completeitems_temp RENAME TO completeitems
           """.execute().apply()
    }
  }
}
