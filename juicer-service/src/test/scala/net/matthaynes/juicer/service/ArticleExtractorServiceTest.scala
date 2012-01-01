package net.matthaynes.juicer.service

import org.scalatest.FunSuite

class ArticleExtractorServiceTest extends FunSuite {

  val service = new ArticleExtractorService

  test("should extract article text") {
    service.extract("http://www.bbc.co.uk/news/world-europe-16377010")
  }

}