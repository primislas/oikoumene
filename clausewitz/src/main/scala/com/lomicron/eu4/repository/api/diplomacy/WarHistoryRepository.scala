package com.lomicron.eu4.repository.api.diplomacy

import com.lomicron.eu4.model.diplomacy.War
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait WarHistoryRepository extends AbstractRepository[Int, War]
