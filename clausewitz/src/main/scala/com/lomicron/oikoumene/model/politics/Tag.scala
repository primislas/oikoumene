package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.{ActiveModifier, Modifier}
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.parsers.ClausewitzParser.startDate
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date
import com.softwaremill.quicklens.ModifyPimp

import scala.collection.immutable.ListMap

case class Tag
(
  // hits = 793, isOptional = false, sample = "GLE"
  id: String = Entity.UNDEFINED,
  // hits = 793, isOptional = false, sample = {"r":38,"g":160,"b":67}
  color: Color = Color.black,
  // hits = 793, isOptional = false, sample = "muslimgfx"
  graphicalCulture: String = Entity.UNDEFINED,
  // hits = 793, isOptional = false
  history: TagHistory = TagHistory.empty,
  // hits = 793, isOptional = false, sample = ["Abdallah","Abdikarim","Abubakar","Ahmad","Ayub","Dalmar","Ghedi","Hassan","Khalid","Liban","Mahad","Mosa","Muhammad","Omar","Rahim","Saad","Sadiq","Sharif"]
  leaderNames: Seq[String] = Seq.empty,
  // hits = 793, isOptional = false, sample = {"name":"Geledi","nameAdj":"Geledi"}
  localisation: Localisation = Localisation.empty,
  // hits = 793, isOptional = false, sample = {"Arliqo #1":60,"Sarjelle #0":60,"Fadumo #0":40,"Umar #0":40,"Muhammad #0":20,"Zaynab #0":-1,"Sadaf #0":-1,"Amira #0":-1}
  monarchNames: ListMap[String, Int] = ListMap.empty,
  // hits = 792, isOptional = true, sample = ["religious_ideas","offensive_ideas","economic_ideas","diplomatic_ideas","trade_ideas","maritime_ideas","defensive_ideas","administrative_ideas"]
  historicalIdeaGroups: Seq[String] = Seq.empty,
  // hits = 792, isOptional = true, sample = ["Afgooye","Awash","Baraawe","Berahle","Boocame","Dallol","Dooloow","Dubti","Dulecha","Elidar","Ewa","Geedo","Gewane","Gobolka","Hawo","Jubbada","Kalabaydh","Marka","Semera","Simurobi","Yalo"]
  shipNames: Seq[String] = Seq.empty,
  // hits = 789, isOptional = true, sample = ["african_spearmen","bantu_tribal_warfare","african_hill_warfare","adal_gunpowder_warfare","westernized_adal","adal_guerilla_warfare","african_somali_cavalry","african_abyssinian_cavalry","african_abyssinian_light_cavalry","african_hussar","african_dragoon"]
  historicalUnits: Seq[String] = Seq.empty,
  // hits = 184, isOptional = true, sample = ["Army of $PROVINCE$"]
  armyNames: Seq[String] = Seq.empty,
  // hits = 89, isOptional = true, sample = ["Fleet of $PROVINCE$"]
  fleetNames: Seq[String] = Seq.empty,
  // hits = 62, isOptional = true, sample = [5,8,1]
  revolutionaryColors: Option[Color] = None,
  // hits = 48, isOptional = true, sample = 1300
  historicalScore: Option[Int] = None,
  // hits = 34, isOptional = true, sample = "south_east_asian_special"
  specialUnitCulture: Option[String] = None,
  // hits = 26, isOptional = true, sample = 0
  randomNationChance: Option[Int] = None,
  // hits = 21, isOptional = true, sample = "protestant"
  preferredReligion: Option[String] = None,
  // hits = 16, isOptional = true, sample = "GBR"
  colonialParent: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  allYourCoreAreBelongToUs: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  rightToBearArms: Boolean = false,
) extends Entity { self =>

  @JsonCreator def this() = this(Entity.UNDEFINED)

  def state: TagState = history.state
  def withState(ts: TagState): Tag = self.modify(_.history.state).setTo(ts)

  def addModifier(m: Modifier): Tag = addModifier(ActiveModifier.of(m))
  def addModifier(am: ActiveModifier): Tag = {
    val s = state.addModifier(am)
    self.modify(_.history.state).setTo(s)
  }

  def atStart: Tag = at(startDate)

  def atTheEnd: Tag = copy(history = history.atTheEnd())

  def at(year: Int, month: Int, day: Int): Tag = at(Date(year, month, day))

  def at(date: Date): Tag = copy(history = history.at(date))

}

object Tag extends FromJson[Tag] {
  val undefined = "---"
}
