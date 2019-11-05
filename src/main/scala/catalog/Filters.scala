package catalog

import catalog.pojo.ScoreItem

object Filters {
  val filters = List(
    ScoreItem(
      categories = List(
        "ofertas_de_quartos_vagas_pantanal",
        "kitnets_ofertadas_pelo_proprietário_para_aluguel",
        "aptos_ofertados_pelo_proprietário_para_aluguel",
        "ofertas_de_quartos_vagas_trindade"
      ),
      price = Some(700),
      gender = Some(1),
      basicExpenses = Some(true),
      laundry = Some(true),
      internet = Some(true)
    )
  )
}
