package fah.parsers

import fah.pojos.CompleteItem
import scalikejdbc.WrappedResultSet

object CompleteItemQueryParser {
  def parse(rs: WrappedResultSet): CompleteItem = {
    CompleteItem(
      id = rs.string("id"),
      categories = rs.array("categories").asInstanceOf[List[String]],
      postDate = rs.timestamp("postdate").toLocalDateTime.toLocalDate,
      title = rs.string("title"),
      link = rs.string("link"),
      images = rs.array("images").asInstanceOf[List[String]],
      description = rs.stringOpt("description"),
      sellerName = rs.stringOpt("sellerName"),
      expiration = rs.timestampOpt("expiration").map(_.toLocalDateTime.toLocalDate),
      email = rs.stringOpt("email"),
      price = rs.intOpt("price"),
      street = rs.stringOpt("street"),
      neighborhood = rs.stringOpt("neighborhood"),
      city = rs.stringOpt("city"),
      gender = rs.stringOpt("gender"),
      bathroom = rs.intOpt("bathroom"),
      rooms = rs.intOpt("rooms"),
      iptu = rs.intOpt("iptu"),
      floor = rs.intOpt("floor"),
      area = rs.intOpt("area"),
      contract = rs.booleanOpt("contract"),
      basicExpenses = rs.booleanOpt("basicExpenses"),
      laundry = rs.booleanOpt("laundry"),
      internet = rs.booleanOpt("internet"),
      animals = rs.booleanOpt("animals"),
      rent = rs.intOpt("rent"),
      stove = rs.booleanOpt("stove"),
      fridge = rs.booleanOpt("fridge"),
      habitation = rs.stringOpt("habitationrs"),
      negotiator = rs.stringOpt("negotiatorrs"),
      contractType = rs.stringOpt("contractTypers"),
      active = rs.boolean("active"),
      furnished = rs.booleanOpt("furnished")
    )
  }

}
