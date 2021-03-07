package com.lomicron.oikoumene.parsers.politics

import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.repository.api.resources.GameFile

case class TagConf
(
  tag: String,
  country: Option[GameFile] = None,
  history: Option[GameFile] = None,
  localisation: Option[Localisation] = None
)
