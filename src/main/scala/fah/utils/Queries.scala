package fah.utils

import fah.pojos.CompleteItem
import scalikejdbc._

import scala.collection.immutable

object Queries {

  case class Table(sName: String) extends AnyVal {
    def name: SQLSyntax = SQLSyntax.createUnsafely(s"${sName.replace("`", "")}")
  }

  case class DB(name: String) extends AnyVal

  val createTable: Table => SQL[Nothing, NoExtractor] = (table: Table) =>  sql"""
           CREATE TABLE IF NOT EXISTS ${table.name} (
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
           """

  val select: Table => SQL[Nothing, NoExtractor] = (table: Table) =>  sql"SELECT * FROM ${table.name}"

  val rename: Table => Table => SQL[Nothing, NoExtractor] = (original: Table) => (to: Table) =>  sql"ALTER TABLE ${original.name} RENAME TO ${to.name}"

  val insert: CompleteItem => Table => SQL[Nothing, NoExtractor] = (i: CompleteItem) => (table: Table) => sql"""
             INSERT INTO ${table.name} VALUES (
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
             """

  val drop: Table => SQL[Nothing, NoExtractor] = (table: Table) => sql"DROP TABLE ${table.name}"

  def readOnlyQuery[T](db: DB, query: SQLToList[T, HasExtractor]): immutable.Seq[T] = {
    NamedDB(Symbol(db.name)) readOnly {implicit session =>
      query.apply()
    }
  }

  def autoCommitQuery(db: DB, query: SQL[Nothing, NoExtractor]): Boolean = {
    NamedDB(Symbol(db.name)) autoCommit { implicit session =>
      query.execute.apply()
    }
  }

  def updateQuery(db: DB, query: SQL[Nothing, NoExtractor]): Int = {
    NamedDB(Symbol(db.name)) localTx { implicit session =>
      query.update.apply()
    }
  }
}
