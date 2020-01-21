package catalog.crawlers

import catalog.pojos.{AccountZI, LinkZI, MediaZI, RawZI}
import org.json4s.{DefaultFormats, JArray, JValue}
import org.json4s.native.JsonMethods.parse
import scalaj.http.Http

object ZICrawler {
  def main(args: Array[String]): Unit = {
    val url = "https://glue-api.zapimoveis.com.br/v2/listings?0=U&1=n&2=i&3=t&4=T&5=y&6=p&7=e&8=_&9=N&10=O&11=N&12=E&addressCountry=&addressState=Santa+Catarina&addressCity=Florian%C3%B3polis&addressZone=&addressNeighborhood=&addressStreet=&addressLocationId=BR%3ESanta+Catarina%3ENULL%3EFlorianopolis&addressPointLat=-27.598639&addressPointLon=-48.518722&business=RENTAL&listingType=USED&portal=ZAP&categoryPage=RESULT&includeFields=search,page,fullUriFragments,developments,superPremium&developmentsSize=3&superPremiumSize=3&__zt="
    val resp =
      """
        |{
        |  "search": {
        |    "time": 59,
        |    "totalCount": 5330,
        |    "result": {
        |      "listings": [
        |        {
        |          "listing": {
        |            "displayAddressType": "STREET",
        |            "latitudeEncoded": 0.3466874888888889,
        |            "salesPriceEncoded": 0,
        |            "contractType": "REAL_ESTATE",
        |            "usableAreas": [
        |              "229"
        |            ],
        |            "videoTourLink": "",
        |            "listingType": "USED",
        |            "createdAt": "2019-01-24T01:15:39.430Z",
        |            "unitTypes": [
        |              "HOME"
        |            ],
        |            "hasStreetNumberEncoded": 1,
        |            "unitsOnTheFloor": 0,
        |            "id": "2430126331",
        |            "portal": "ZAP",
        |            "unitFloor": 0,
        |            "parkingSpaces": [
        |              0
        |            ],
        |            "updatedAt": "2019-12-19T02:30:32.340Z",
        |            "suites": [
        |              0
        |            ],
        |            "unitTypesEncoded": 12,
        |            "publicationType": "PREMIUM",
        |            "bathrooms": [
        |              5
        |            ],
        |            "usageTypes": [
        |              "COMMERCIAL"
        |            ],
        |            "deliveredAt": "1976-01-01T00:00:00Z",
        |            "bedrooms": [
        |              0
        |            ],
        |            "pricingInfos": [
        |              {
        |                "rentalInfo": {
        |                  "period": "MONTHLY",
        |                  "warranties": [],
        |                  "monthlyRentalTotalPrice": "6400"
        |                },
        |                "yearlyIptu": "4651",
        |                "price": "6400",
        |                "businessType": "RENTAL",
        |                "monthlyCondoFee": "0"
        |              }
        |            ],
        |            "resale": false,
        |            "buildings": 0,
        |            "stateEncoded": 24,
        |            "status": "ACTIVE",
        |            "amenities": [],
        |            "feedsId": "4078",
        |            "constructionStatus": "ConstructionStatus_NONE",
        |            "description": "São três pavimentos, 228 m² de área útil, 79 m² de terreno, 5 banheiros, em local de fácil acesso, há 5 minutos do TICEN (Terminal de Integração do Centro).",
        |            "title": "Comercial/Industrial de 229 metros quadrados no bairro Centro",
        |            "portalEncoded": 2,
        |            "hasZipCodeEncoded": 1,
        |            "floors": [],
        |            "nonActivationReason": "NonActivationReason_NONE",
        |            "hasStreetEncoded": 1,
        |            "providerId": "zap-71d1e707-f34c-d9f9-7724-95c298179849",
        |            "propertyType": "UNIT",
        |            "unitSubTypes": [],
        |            "ceilingHeight": [],
        |            "legacyId": "21294101",
        |            "rentalPriceEncoded": "6400",
        |            "address": {
        |              "country": "BR",
        |              "zipCode": "88010080",
        |              "geoJson": "",
        |              "city": "Florianópolis",
        |              "level": "STREET",
        |              "precision": "RANGE_INTERPOLATED",
        |              "confidence": "VALID_STREET",
        |              "geopoint": "-27.596252,-48.556613",
        |              "source": "GOOGLE",
        |              "point": {
        |                "lon": -48.556613,
        |                "source": "GOOGLE",
        |                "lat": -27.596252
        |              },
        |              "zone": "",
        |              "street": "Rua Bento Gonçalves",
        |              "locationId": "BR>Santa Catarina>NULL>Florianopolis>Barrios>Centro",
        |              "district": "",
        |              "name": "",
        |              "state": "Santa Catarina",
        |              "neighborhood": "Centro",
        |              "poisList": [
        |                "BS:Rua Pedro Ivo",
        |                "BS:Rua Conselheiro Mafra",
        |                "BS:Rua Tenente Silveira (02)",
        |                "BS:Avenida Rio Branco",
        |                "BS:Alameda Adolfo Konder",
        |                "TS:Rua Pedro Ivo",
        |                "TS:Rua Tenente Silveira (02)",
        |                "TS:Avenida Rio Branco",
        |                "TS:Alameda Adolfo Konder",
        |                "TS:Rua Jerônimo Coelho (01)",
        |                "CS:Padaria Buenos",
        |                "CS:Padaria Sabor e Pão",
        |                "CS:Foguinho",
        |                "CS:Casa dos Pães",
        |                "CS:Kretzen",
        |                "PH:FarmaMellitus",
        |                "PH:Preço Popular1",
        |                "PH:Bio Idêntica",
        |                "PH:Phytomaster",
        |                "PH:Master Farma"
        |              ],
        |              "complement": "",
        |              "pois": [],
        |              "valuableZones": []
        |            },
        |            "longitudeEncoded": 0.36512051944444446,
        |            "periodEncoded": 1,
        |            "externalId": "4078",
        |            "pubTypeEncoded": 1,
        |            "totalAreas": [
        |              "229"
        |            ],
        |            "advertiserId": "71d1e707-f34c-d9f9-7724-95c298179849",
        |            "daysSinceCreationEncoded": 349,
        |            "acceptExchange": true,
        |            "showPrice": true,
        |            "businessTypeEncoded": 0,
        |            "displayAddressEncoded": 3,
        |            "capacityLimit": [
        |              0
        |            ],
        |            "advertiserContact": {
        |              "phones": [
        |                "4833641364",
        |                "48991021364"
        |              ]
        |            }
        |          },
        |          "account": {
        |            "id": "71d1e707-f34c-d9f9-7724-95c298179849",
        |            "name": "F1 Cia Imobiliária",
        |            "emails": {
        |              "primary": "f1@f1ciaimobiliaria.com.br"
        |            },
        |            "phones": {
        |              "primary": "4833641364",
        |              "mobile": "48991021364"
        |            },
        |            "addresses": {
        |              "shipping": {
        |                "country": "Brasil",
        |                "zipCode": "88035400",
        |                "geoJson": "",
        |                "city": "Florianopolis",
        |                "streetNumber": "399",
        |                "level": "Level_NONE",
        |                "precision": "Precision_NONE",
        |                "confidence": "ConfidenceLevel_NONE",
        |                "source": "AddressSource_NONE",
        |                "ibgeCityId": "",
        |                "zone": "",
        |                "street": "Av. Madre Benvenuta, 399",
        |                "locationId": "",
        |                "district": "",
        |                "name": "",
        |                "state": "SC",
        |                "neighborhood": "Santa Mônica",
        |                "poisList": [],
        |                "complement": "",
        |                "pois": [],
        |                "valuableZones": []
        |              },
        |              "billing": {
        |                "country": "Brasil",
        |                "zipCode": "88035400",
        |                "geoJson": "",
        |                "city": "Florianópolis",
        |                "streetNumber": "399",
        |                "level": "Level_NONE",
        |                "precision": "Precision_NONE",
        |                "confidence": "ConfidenceLevel_NONE",
        |                "source": "AddressSource_NONE",
        |                "ibgeCityId": "",
        |                "zone": "",
        |                "street": "Rua Eurico Hosterno, 38",
        |                "locationId": "",
        |                "district": "",
        |                "name": "",
        |                "state": "SC",
        |                "neighborhood": "Santa Mônica",
        |                "poisList": [],
        |                "complement": "",
        |                "pois": [],
        |                "valuableZones": []
        |              }
        |            },
        |            "logoUrl": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/c5ffea2c5f9b8520f224dcad04f492f3.jpg",
        |            "licenseNumber": "04110-J-SC",
        |            "websiteUrl": "http://www.f1ciaimobiliaria.com.br",
        |            "createdDate": "2018-03-27T19:25:12Z",
        |            "showAddress": true,
        |            "legacyVivarealId": 76826,
        |            "legacyZapId": 3058399
        |          },
        |          "medias": [
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/1c7b8092e04ae27e6069fe72a3207387.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://www.youtube.com/v/WdFk0qMwTic",
        |              "type": "VIDEO",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/66d9334e558ef61c9eb5e13e60a3d56a.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/b0e32b3112cd19f6541f1f2163a25db0.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/6eaac5ecb42e309983587e410a118007.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/8647bc40123838e5389a215ee3909944.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/cbffb7cbc7b1677f336fb5119ba7189a.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/c62c70f56966d1974c8e34b3a8a5b966.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/0174fe57a951671d492b88bc6c16d10a.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/d8a565e5d51ffad41eb48b776b73b44a.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/98c909ce48c4da650d7dd1fda6268ca1.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/79500627bba4d2b92ce555d34fca3f12.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/2a9a5c3f564365854c710001df6a5529.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/ec2e4aa80232a4f44943791a0cce0bc8.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/4f1460a7c6dec96fd1513f721bd821d8.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/d283caa06a04cdf9bf6d69021fb0cfc5.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/9cc4b9cff6367208ddbfece6ee283f61.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/d9829127d877e0570e32c2c89db1c2f4.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/d6cbaa68ac892329429fb8abef3611c1.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/00e75039c51fd70d87e370a0fcade03b.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            },
        |            {
        |              "url": "https://resizedimgs.zapimoveis.com.br/{action}/{width}x{height}/vr.images.sp/35ba88eaa38570d6fac64f39eced0181.jpg",
        |              "type": "IMAGE",
        |              "status": "SUCCESS"
        |            }
        |          ],
        |          "accountLink": {
        |            "data": {},
        |            "name": "F1 Cia Imobiliária",
        |            "href": "/imobiliaria/76826/",
        |            "rel": ""
        |          },
        |          "link": {
        |            "data": {},
        |            "name": "Centro",
        |            "href": "/imovel/aluguel-casa-centro-florianopolis-sc-229m2-id-2430126331/",
        |            "rel": ""
        |          }
        |        }
        |      ]
        |    }
        |  }
        |}
        |""".stripMargin
//    val a = Http(url).headers(Seq(("X-Domain", "www.zapimoveis.com.br"))).asString.body
    implicit val formats: DefaultFormats.type = DefaultFormats

    val a = (parse(resp) \ "search" \ "result")
      .findField(_._1 == "listings")
      .map(_._2.extract[JArray].arr
        .map{l => (l \ "listing").extract[RawZI]
          .copy(
            account = (l \ "account").extract[Option[AccountZI]],
            medias = (l \ "medias").extract[Option[Array[MediaZI]]],
            link = (l \ "link").extract[Option[LinkZI]])
        })
      .getOrElse(List.empty)

    println(a)
  }
}
