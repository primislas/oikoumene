package com.lomicron.oikoumene.repository.api

case class ResourceNameAndEntity[T](name: String = "UNDEFINED.txt", entity: T)
