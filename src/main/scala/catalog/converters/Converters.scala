package catalog.converters

import catalog.pojos.{RawItem, RawQA, RawZI}
import catalog.utils.Utils.{normalize, parseInt, toB64Compressed}

trait Converters {
  def convert(rawQA: RawQA): RawItem = {
    RawItem(
      id = rawQA.id.get,
      category = Some(normalize(rawQA.tipo.getOrElse("") + " para alugar direto com proprietario")), // FIXME: handle camelCase as separated strings
      title = normalize(rawQA.tipo.getOrElse("") + " " + rawQA.endereco.getOrElse("")),
      link = "https://www.quintoandar.com.br/imovel/" + rawQA.id.get,
      entity = "qa",
      originalSource = toB64Compressed(rawQA.originalSource.get),
      description = Some("descrição no link"), // TODO: Crawl the comment link
      postDate = rawQA.first_publication.get,
      price = (parseInt(rawQA.aluguel_condominio) ++ parseInt(rawQA.home_insurance)).reduceOption(_ + _).map(_.toString),
      street = rawQA.endereco,
      neighborhood = rawQA.bairro,
      city = rawQA.cidade,
      contract = Some("sim"),
      laundry = rawQA.amenidades.flatMap(_.find(_ == "MAQUINA_DE_LAVAR").map(_ => "sim")),
      animalsAllowed = rawQA.amenidades.flatMap(_.find(_ == "PODE_TER_ANIMAIS_DE_ESTIMACAO").map(_ => "sim")),
      rentPrice = rawQA.aluguel,
      stove = rawQA.amenidades.flatMap(_.find(_ == "FOGAO_INCLUSO").map(_ => "sim")),
      fridge = rawQA.amenidades.flatMap(_.find(_ == "GELADEIRA_INCLUSO").map(_ => "sim"))
    )
  }

  def convert(rawZI: RawZI): RawItem = {
    val temp = RawItem(
      id = rawZI.id,
      category = Some(normalize(s"${rawZI.pricingInfos.head.businessType}_${rawZI.contractType}_${rawZI.unitTypes.head}")),
      title = rawZI.title,
      link = rawZI.link.get.href,
      entity = "zi",
      originalSource = toB64Compressed(rawZI.originalSource.get),
      description = Some(rawZI.description),
      sellerName = Some(rawZI.account.get.name),
      postDate = rawZI.createdAt,
      sellerEmail = rawZI.account.get.emails.get("primary"),
      price = Some(rawZI.pricingInfos.head.price),
      street = rawZI.address.street,
      neighborhood = Some(rawZI.address.neighborhood),
      city = Some(rawZI.address.city),
      contract = Some("sim"),
      laundry = None, // FIXME: find key to laundry confirmation
      internetIncluded = Some("nao"),
      animalsAllowed = None, // FIXME: find key to animals confirmation
      rentPrice = Some(rawZI.pricingInfos.head.price),
      IPTUPrice = rawZI.pricingInfos.head.yearlyIptu,
      managerFee = rawZI.pricingInfos.head.monthlyCondoFee,
      stove = None, // FIXME: find key to animals confirmation
      fridge = None, // FIXME: find key to animals confirmation
      habitationType = Some(rawZI.unitTypes.head),
      negotiatorType = Some(rawZI.contractType),
      contractType = Some(rawZI.pricingInfos.head.businessType),
      active = Some(rawZI.status),
      furnished = rawZI.amenities.find(_ == "FURNISHED")
    )
    temp.copy(rentPrice =
      temp.rentPrice.flatMap(i =>
        (parseInt(i) ++ parseInt(temp.managerFee)).reduceOption(_ + _).map(_.toString)
      )
    )
  }
}
