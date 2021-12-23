package com.lomicron.eu4.writers

case class ModSettings
(
  name: Option[String] = None,
  version: Option[String] = None,
  modDir: Option[String] = None,
  eu4ModDir: Option[String] = None,
)

object ModSettings {
  def empty: ModSettings = ModSettings()
}
