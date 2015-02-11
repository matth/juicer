package net.matthaynes.juicer.service

import com.gravity.goose._
import org.scalatest.FunSuite
import org.mockito.Mockito._
import de.jetwick.snacktory._

class ArticleExtractorServiceTest extends FunSuite {

  val url = "http://www.bbc.co.uk/news/world-africa-16377824"

  val article = new Article
  article.canonicalLink       = "http://www.bbc.co.uk/news/world-africa-16377824"
  article.domain              = "www.bbc.co.uk"
  article.linkhash            = "ac2f2e739421184f01c942b057f8449d"
  article.title               = "South Sudan 'sends more troops' to strife-torn town Pibor"
  article.metaDescription     = "South Sudan's government says it is sending more troops and police to the town ..."
  article.cleanedArticleText  = "South Sudan's government says it is sending more troops and police to the town ..."

  val service = new ArticleExtractorService {
    override val goose    = mock(classOf[Goose])
  }

  test("should extract article data") {
    when(service.goose.extractContent(url)).thenReturn(article)
    val extracted = service.extract(url, force_goose=true)
    assert(extracted.title == article.title)
    assert(extracted.description == article.metaDescription)
    assert(extracted.body == article.cleanedArticleText)
    assert(extracted.hash == article.linkhash)
    assert(extracted.url == article.canonicalLink)
    assert(extracted.domain == article.domain)
  }

  test("snacktory - author extract from named entities") {
    val c = new Converter
    val url = "http://women2.com/2014/06/18/woman-entrepreneur-misnomer/"
    val html = c.streamToString(getClass().getResourceAsStream("women2.html"))
    val extracted = service.extract_src(url, src=html)
    assert(extracted.title == "Why 'Woman Entrepreneur' Is a Misnomer")
    assert(extracted.additionalData.get("author") == Some("Rebekah Iliff"))
  }

  test("snacktory - author extraction - espn") {
    val c = new Converter
    val url = "http://sports.espn.go.com/espn/commentary/news/story?id=5461430"
    val html = c.streamToString(getClass().getResourceAsStream("espn.html"))
    val extracted = service.extract_src(url, src=html)
    assert(extracted.title == "Florida, Alabama grandstanding? Urban Meyer, Nick Saban take stands on agents - ESPN")
    assert(extracted.additionalData.get("author") == Some("Jemele Hill"))
  }

  test("snacktory - author extraction - gizmodo") {
    val c = new Converter
    val url = "http://www.gizmodo.com.au/2010/08/xbox-kinect-gets-its-fight-club/"
    val html = c.streamToString(getClass().getResourceAsStream("gizmodo.html"))
    val extracted = service.extract_src(url, src=html)
    assert(extracted.title == "Xbox Kinect Gets Its Fight Club")
    assert(extracted.additionalData.get("author") == Some("Stephen Totilo"))
  }

  test("snacktory - author extraction - usatoday") {
    val c = new Converter
    val url = "http://content.usatoday.com/communities/thehuddle/post/2010/08/brett-favre-practices-set-to-speak-about-return-to-minnesota-vikings/1"
    val html = c.streamToString(getClass().getResourceAsStream("usatoday.html"))
    val extracted = service.extract_src(url, src=html)
    // TODO: Find a way to clean up long titles.
    assert(extracted.title == "Brett Favre says he couldn't give up on one more chance to win the Super Bowl with Vikings - The Huddle: Football News from the NFL - USATODAY.com")
    assert(extracted.additionalData.get("author") == Some("Sean Leahy"))
  }

}