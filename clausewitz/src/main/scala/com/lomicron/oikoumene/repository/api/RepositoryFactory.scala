package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.diplomacy.{CasusBelliRepository, DiplomacyRepository, WarGoalTypeRepository, WarHistoryRepository}
import com.lomicron.oikoumene.repository.api.gfx.GFXRepository
import com.lomicron.oikoumene.repository.api.government.IdeaGroupRepository
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics._
import com.lomicron.oikoumene.repository.api.trade.{TradeGoodRepository, TradeNodeRepository}

trait RepositoryFactory {

  def resources: ResourceRepository
  def localisations: LocalisationRepository

  def tags: TagRepository
  def cultures: CultureRepository
  def religions: ReligionRepository

  def ideas: IdeaGroupRepository

  def diplomacy: DiplomacyRepository
  def warHistory: WarHistoryRepository
  def casusBelli: CasusBelliRepository
  def warGoalTypes: WarGoalTypeRepository

  def provinces: ProvinceRepository
  def buildings: BuildingRepository
  def geography: GeographicRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository

  def tradeGoods: TradeGoodRepository
  def tradeNodes: TradeNodeRepository

  def gfx: GFXRepository

}
