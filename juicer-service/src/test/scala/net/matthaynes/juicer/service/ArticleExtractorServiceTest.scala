package net.matthaynes.juicer.service

import com.gravity.goose.{ Article => GooseArticle, _ }
import org.scalatest.FunSuite
import org.mockito.Mockito._

class ArticleExtractorServiceTest extends FunSuite {

  val url = "http://www.bbc.co.uk/news/world-africa-16377824"

  val article = new GooseArticle
  article.canonicalLink       = "http://www.bbc.co.uk/news/world-africa-16377824"
  article.domain              = "www.bbc.co.uk"
  article.linkhash            = "ac2f2e739421184f01c942b057f8449d"
  article.title               = "South Sudan 'sends more troops' to strife-torn town Pibor"
  article.metaDescription     = "South Sudan's government says it is sending more troops and police to the town ..."
  article.cleanedArticleText  = "South Sudan's government says it is sending more troops and police to the town ..."

  val service = new ArticleExtractorService {
    override val goose    = mock(classOf[Goose])
    override val entities = mock(classOf[NamedEntityService])
  }

  test("should extract article data") {
    when(service.goose.extractContent(url)).thenReturn(article)
    val extracted = service.extract(url)
    assert(extracted.title == article.title)
    assert(extracted.description == article.metaDescription)
    assert(extracted.body == article.cleanedArticleText)
    assert(extracted.hash == article.linkhash)
    assert(extracted.url == article.canonicalLink)
    assert(extracted.domain == article.domain)
    verify(service.entities).classify(article.title + " " + article.cleanedArticleText)
  }

}
