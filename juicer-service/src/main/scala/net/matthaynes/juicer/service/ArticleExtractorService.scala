package net.matthaynes.juicer.service

import com.gravity.goose._
import java.util.Date

case class ExtractedArticleMetaData(val description: String, val keywords:String, val canonicalLink: String,
                                    val hash: String, val publishDate: Date = null)

case class ExtractedArticleData(val title: String, val text: String, val meta: ExtractedArticleMetaData, val entities: List[NamedEntity])

class ArticleExtractorService {

  val config   = new Configuration
  config.setLocalStoragePath("/tmp/goose")
  config.setEnableImageFetching(false)

  val goose    = new Goose(config)

  val entities = new NamedEntityService

  def extract(url : String) : ExtractedArticleData = {

    val article  = goose.extractContent(url)
    var text     = ""

    if (article.title != null) {
      text += article.title
    }

    if (article.cleanedArticleText != null) {
      text += article.cleanedArticleText
    }

    val meta = new ExtractedArticleMetaData(article.metaDescription, article.metaKeywords, article.canonicalLink, article.linkhash, article.publishDate)

    new ExtractedArticleData(article.title, article.cleanedArticleText, meta, entities.classify(text))

  }

}