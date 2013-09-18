package net.matthaynes.juicer.service

import com.gravity.goose.{ Article => GooseArticle, _ }
import com.gravity.goose.extractors.AdditionalDataExtractor
import com.gravity.goose.images.{ Image => GooseImage }
import org.jsoup.nodes.Element
import scala.util.matching.Regex

case class Image(src: String, width: Int, height: Int)

case class Article(url: String, domain: String, hash: String, title: String, description: String,
  body: String, image: Option[Image], additionalData: Option[scala.collection.Map[String, String]], entities: List[NamedEntity])

class ArticleExtractorService {

  val config   = new Configuration {
    setLocalStoragePath("/tmp/goose")
    setAdditionalDataExtractor(new BbcNewsDataExtractor())

    import scala.sys.process._

    if ("which convert".! == 0 && "which identify".! == 0) {
      setEnableImageFetching(true)
      setImagemagickConvertPath("which convert".!!.trim)
      setImagemagickIdentifyPath("which identify".!!.trim)
    } else {
      setEnableImageFetching(false)
    }

    // Don't want this disabled thanks
    com.gravity.goose.network.HtmlFetcher.getHttpClient.getParams.setParameter("http.connection.stalecheck", true)

  }

  val goose    = new Goose(config)

  val entities = new NamedEntityService

  def extract(url : String) : Article = {

    val article  = goose.extractContent(url)

    var text     = List(article.title, article.cleanedArticleText).filter(_ != null).mkString(" ")

    val image    = article.topImage match {
      case i:GooseImage =>
        if (i.imageSrc.isEmpty) None else Option(new Image(i.imageSrc, i.width, i.height))
      case _ => None
    }

    val additionalData = article.additionalData.isEmpty match {
      case false => Option(article.additionalData)
      case true  => None
    }

    new Article(article.canonicalLink, article.domain, article.linkhash, article.title,
      article.metaDescription, article.cleanedArticleText, image, additionalData, entities.classify(text))

  }

}

class BbcNewsDataExtractor extends AdditionalDataExtractor {

  override def extract(rootElement: Element): Map[String, String] = {

    var attributes = Map[String, String]()

    getMetaTagValue(rootElement, "Section") match {
      case Some(section) => attributes = attributes ++ Map("bbc.news.section" -> section)
      case None          =>
    }

    getMetaTagValue(rootElement, "CPS_ASSET_TYPE") match {
      case Some(asset_type) => attributes = attributes ++ Map("bbc.news.asset_type" -> asset_type)
      case None          =>
    }

    getMetaTagValue(rootElement, "CPS_ID") match {
      case Some(id) => attributes = attributes ++ Map("bbc.news.id" -> id)
      case None     =>
    }

    getMetaTagValue(rootElement, "OriginalPublicationDate") match {
      case Some(published) => {
        val pattern = new Regex("""(\d{4})\/(\d{2})\/(\d{2}) (\d{2}):(\d{2}):(\d{2})""", "Y", "m", "d", "H", "M", "S")
        val result  = pattern.findFirstMatchIn(published).get
        val date    = result.group("Y") + "-" +
                      result.group("m") + "-" +
                      result.group("d") + "T" +
                      result.group("H") + ":" +
                      result.group("M") + ":" +
                      result.group("S")
        attributes   = attributes ++ Map("bbc.news.published" -> date)
      }
      case None            =>
    }

    return attributes
  }

  def getMetaTagValue(rootElement: Element, key: String): Option[String] = {

    var metaTags = rootElement.getElementsByAttributeValue("name", key)

    if (metaTags.isEmpty()) {
      return None
    }

    val metaTag  = metaTags.get(0)

    if (metaTag.tagName() != "meta") {
      return None
    }

    return Some(metaTag.attr("content"))

  }

}
