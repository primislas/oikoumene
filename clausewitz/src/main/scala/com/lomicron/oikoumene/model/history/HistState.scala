package com.lomicron.oikoumene.model.history

import com.lomicron.oikoumene.model.EntityState

trait HistState[S <: HistState[S, E], E <: HistEvent] extends EntityState[S, E] {}
