package com.lomicron.eu4.parsers.politics

import com.lomicron.eu4.repository.api.resources.GameFile
import com.lomicron.oikoumene.model.localisation.Localisation

case class TagConf
(
  tag: String,
  country: Option[GameFile] = None,
  history: Option[GameFile] = None,
  localisation: Option[Localisation] = None
)
