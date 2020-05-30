package com.lomicron.oikoumene.writers

trait ModWriter[S, T] { self =>
  val settings: ModSettings
  def serialize(sourceEntity: S): T
  def serialize(sourceEntities: Seq[S]): Seq[T] = sourceEntities.map(serialize)
  def storeEntity(sourceEntity: S): ModWriter[S, T] = storeSerialized(serialize(sourceEntity))
  def storeEntities(es: Seq[S]): ModWriter[S, T] = {
    es.map(storeEntity)
    self
  }
  def storeSerialized(serializedEntity: T): ModWriter[S, T]
  def clear: ModWriter[S, T]
}
