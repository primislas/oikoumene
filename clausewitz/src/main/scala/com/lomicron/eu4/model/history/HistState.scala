package com.lomicron.eu4.model.history

import com.lomicron.eu4.model.EntityState

trait HistState[S <: HistState[S, E], E <: HistEvent] extends EntityState[S, E] {}
