package fah.parsers

import fah.pojos.RawItem
import scalikejdbc.WrappedResultSet

object RawItemQueryParser {
  def parse(rs: WrappedResultSet): RawItem = {
    RawItem(
      id = rs.string("id"),
      postDate = rs.string("postDate"),
      title = rs.string("title"),
      link = rs.string("link"),
      entity = rs.string("entity"),
      originalSource = rs.string("originalSource"),
      category = rs.stringOpt("category"),
      images = rs.array("images").getArray.asInstanceOf[Array[String]],
      description = rs.stringOpt("description"),
      expirationDate = rs.stringOpt("expirationDate"),
      price = rs.stringOpt("price"),
      street = rs.stringOpt("street"),
      neighborhood = rs.stringOpt("neighborhood"),
      city = rs.stringOpt("city"),
      gender = rs.stringOpt("gender"),
      contract = rs.stringOpt("contract"),
      waterIncluded = rs.stringOpt("waterIncluded"),
      lightIncluded = rs.stringOpt("lightIncluded"),
      internetIncluded = rs.stringOpt("internetIncluded"),
      laundry = rs.stringOpt("laundry"),
      washingMachine = rs.stringOpt("washingMachine"),
      animalsAllowed = rs.stringOpt("animalsAllowed"),
      rentPrice = rs.stringOpt("rentPrice"),
      IPTUPrice = rs.stringOpt("IPTUPrice"),
      managerFee = rs.stringOpt("managerFee"),
      stove = rs.stringOpt("stove"),
      fridge = rs.stringOpt("fridge"),
      furnished = rs.stringOpt("furnished"),
      habitationType = rs.stringOpt("habitationType"),
      negotiatorType = rs.stringOpt("negotiatorType"),
      contractType = rs.stringOpt("contractType"),
      active = rs.stringOpt("active"),
      sellerName = rs.stringOpt("sellerName"),
      sellerEmail = rs.stringOpt("sellerEmail")
    )
  }

}
