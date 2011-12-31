package net.matthaynes.ner.service

import org.scalatest.FunSuite

class NamedEntityServiceTest extends FunSuite {

  val service = new NamedEntityService

  val text    = """The fate of Lehman Brothers, the beleaguered investment bank, hung in the balance on Sunday
    as Federal Reserve officials and the leaders of major financial institutions continued to gather in emergency
    meetings trying to complete a plan to rescue the stricken bank. Several possible plans emerged from the talks,
    held at the Federal Reserve Bank of New York and led by Timothy R. Geithner, the president of the New York Fed,
    and Treasury Secretary Henry M. Paulson Jr.

    Meanwhile, in London, the capital city of the United Kingdom there was a strike on the London Underground, this
    was not an issue for Mr Paulson though, as he live in the US not the United Kingdom"""

  test("should extract named entities from text") {
     val container     = service.classify(text)
     val locations     = container.entities.filter(_._2.isInstanceOf[Location])
     val organizations = container.entities.filter(_._2.isInstanceOf[Organization])
     val people        = container.entities.filter(_._2.isInstanceOf[Person])
     assert(organizations.size == 5)
     assert(people.size == 3)
     assert(locations.size == 3)
     assert(container.entities.getOrElse("United Kingdom",  null).frequency == 2)
     assert(container.entities.getOrElse("Lehman Brothers", null).frequency == 1)
  }

}

class NamedEntityContainerTest extends FunSuite {
  val container = new NamedEntityContainer

  test("add() increments and entity's frequency and adds to internal map") {
    val entity = new Location("London")
    (1 to 3) foreach { _ => container.add(entity) }
    assert(entity.frequency == 3)
    assert(container.entities.size == 1)
  }

}