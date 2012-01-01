package net.matthaynes.juicer.service

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

  test("should extract named entities from text with correct frequencies") {
     val entities      = service.classify(text)
     val locations     = entities.filter(_.isInstanceOf[Location])
     val organizations = entities.filter(_.isInstanceOf[Organization])
     val people        = entities.filter(_.isInstanceOf[Person])
     assert(organizations.size == 5)
     assert(people.size == 3)
     assert(locations.size == 3)
     assert(entities.filter(_.text == "United Kingdom").head.frequency == 2)
     assert(entities.filter(_.text == "Lehman Brothers").head.frequency == 1)
  }

}
