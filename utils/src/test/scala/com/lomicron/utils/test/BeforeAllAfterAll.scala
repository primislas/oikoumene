package com.lomicron.utils.test

import org.specs2.Specification
import org.specs2.specification.core.Fragments

trait BeforeAllAfterAll extends Specification {
  // see http://bit.ly/11I9kFM (specs2 User Guide)
  override def map(fragments: => Fragments): Fragments =
    step(beforeAll()) ^ fragments ^ step(afterAll())

  protected def beforeAll()
  protected def afterAll()
}