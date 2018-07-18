package com.lomicron.utils.parsing.scopes

case class ParsingException(private val message: String = "",
                            private val cause: Throwable = None.orNull)
  extends RuntimeException(message, cause)