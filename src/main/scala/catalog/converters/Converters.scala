package catalog.converters

import catalog.pojos.{RawItem, RawQA, RawZI}
import catalog.utils.Utils.{normalize, parseInt}

trait Converters {
  def convert(rawQA: RawQA): RawItem = {
    RawItem(
      id = parseInt(rawQA.id.get).get,
      category = normalize(rawQA.tipo.getOrElse("") + " para alugar direto com proprietario"), // FIXME: handle camelCase as separated strings
      date = rawQA.first_publication.get,
      title = normalize(rawQA.tipo.getOrElse("") + " " + rawQA.endereco.getOrElse("")),
      image = "yes",
      link = "https://www.quintoandar.com.br/imovel/" + rawQA.id.get,
      description = Some("descrição no link"), // TODO: Crawl the comment link
      seller = None,
      expiration = None,
      postDate = rawQA.first_publication,
      email = None,
      price = Some((parseInt(rawQA.aluguel_condominio.getOrElse("0")).getOrElse(0) + parseInt(rawQA.home_insurance.getOrElse("0")).getOrElse(0)).toString),
      street = rawQA.endereco,
      neighborhood = rawQA.bairro,
      city = rawQA.cidade,
      gender = None,
      contract = Some("sim"),
      basicExpenses = None,
      laundry = rawQA.amenidades.flatMap(_.find(_ == "MAQUINA_DE_LAVAR").map(_ => "sim")),
      internet = None,
      animals = rawQA.amenidades.flatMap(_.find(_ == "PODE_TER_ANIMAIS_DE_ESTIMACAO").map(_ => "sim")),
      rent = rawQA.aluguel,
      stove = rawQA.amenidades.flatMap(_.find(_ == "FOGAO_INCLUSO").map(_ => "sim")),
      fridge = rawQA.amenidades.flatMap(_.find(_ == "GELADEIRA_INCLUSO").map(_ => "sim"))
    )
  }

  def convert(rawZI: RawZI): RawItem = {
    RawItem(
      id = parseInt(rawZI.id).get,
      category = s"${rawZI.pricinginfos.head.businessType}_${rawZI.contractType}_${rawZI.unitTypes.head}",
      date = rawZI.createdAt,
      title = rawZI.title,
      image = "yes",
      link = rawZI.link.get.href,
      description = Some(rawZI.description),
      seller = Some(rawZI.account.get.name),
      expiration = None,
      postDate = Some(rawZI.createdAt),
      email = rawZI.account.get.emails.get("primary"),
      price = Some(rawZI.pricinginfos.head.price),
      street = Some(rawZI.address.street),
      neighborhood = Some(rawZI.address.neighborhood),
      city = Some(rawZI.address.city),
      gender = None,
      contract = Some("sim"),
      basicExpenses = None,
      laundry = None, // FIXME: find key to laundry confirmation
      internet = Some("nao"),
      animals = None, // FIXME: find key to animals confirmation
      rent = Some(rawZI.pricinginfos.head.price),
      stove = None, // FIXME: find key to animals confirmation
      fridge = None, // FIXME: find key to animals confirmation
      habitation = Some(rawZI.unitTypes.head),
      negotiator = Some(rawZI.contractType),
      contractType = Some(rawZI.pricinginfos.head.businessType),
      active = Some(rawZI.status),
      furnished = rawZI.amenities.find(_ == "FURNISHED")
    )
  }
}
