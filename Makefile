SHELL=/bin/bash

.PHONY: run, deploy

run:
	- sbt "run ofertas_de_quartos_vagas_-_pantanal,kitnets_ofertadas_pelo_proprietário_para_aluguel,aptos_ofertados_pelo_proprietário_para_aluguel,ofertas_de_quartos_vagas_-_trindade"

deploy:
