package com.lomicron.oikoumene.repository.api.diplomacy

import com.lomicron.oikoumene.model.diplomacy.War
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait WarHistoryRepository extends AbstractRepository[Int, War]
