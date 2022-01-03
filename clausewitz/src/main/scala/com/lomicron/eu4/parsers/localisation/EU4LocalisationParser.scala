package com.lomicron.eu4.parsers.localisation

import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.oikoumene.model.localisation.LocalisationEntry

object EU4LocalisationParser {

  def apply(repos: RepositoryFactory)
  : Seq[LocalisationEntry] = {
    val resources = repos.resources
    val localisationEntries = resources
      .getLocalisation(resources.SupportedLanguages.english)
      .reverse
    repos.localisations.upsertEntries(localisationEntries)
    localisationEntries
  }

}
