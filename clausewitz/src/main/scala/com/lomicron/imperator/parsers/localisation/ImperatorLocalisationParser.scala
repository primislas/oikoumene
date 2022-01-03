package com.lomicron.imperator.parsers.localisation

import com.lomicron.imperator.repository.api.RepositoryFactory
import com.lomicron.oikoumene.model.localisation.LocalisationEntry

object ImperatorLocalisationParser {

  def apply(repos: RepositoryFactory)
  : Seq[LocalisationEntry] = {
    val resources = repos.resources
    val localisationEntries = resources
      .getLocalisation(resources.SupportedLanguages.english)
      .reverse
    repos.localisation.upsertEntries(localisationEntries)
    localisationEntries
  }

}
