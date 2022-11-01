package com.lomicron.vicky.repository.api

import com.lomicron.oikoumene.repository.api.AbstractRepository
import com.lomicron.vicky.model.technology.Invention

trait TechnologyRepository extends AbstractRepository[String, Invention]
