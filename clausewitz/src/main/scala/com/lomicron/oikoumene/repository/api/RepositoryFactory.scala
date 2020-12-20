package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.diplomacy.{CasusBelliRepository, DiplomacyRepository, WarGoalTypeRepository, WarHistoryRepository}
import com.lomicron.oikoumene.repository.api.gfx.GFXRepository
import com.lomicron.oikoumene.repository.api.government._
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.modifiers.ModifierRepository
import com.lomicron.oikoumene.repository.api.politics._
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.oikoumene.repository.api.trade.{CenterOfTradeRepository, TradeGoodRepository, TradeNodeRepository}
import com.lomicron.oikoumene.writers.WriterFactory

trait RepositoryFactory {

  def settings: GameFilesSettings

  def resources: ResourceRepository
  def localisations: LocalisationRepository

  def tags: TagRepository
  def cultures: CultureRepository
  def religions: ReligionRepository
  def rulerPersonalities: RulerPersonalityRepository

  def governments: GovernmentRepository
  def governmentReforms: GovernmentReformRepository
  def technology: TechnologyRepository
  def ideas: IdeaGroupRepository
  def policies: PolicyRepository
  def stateEdicts: StateEdictRepository

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
  def centersOfTrade: CenterOfTradeRepository

  def modifiers: ModifierRepository

  def gfx: GFXRepository

  def modWriters(mod: String): WriterFactory
  def storeToCache: RepositoryFactory
  def loadFromCache: Option[RepositoryFactory]

}
