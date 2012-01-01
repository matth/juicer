package net.matthaynes.juicer.service

import com.gravity.goose._
import java.util.Date

case class ExtractedArticle(val url:String, val domain:String, val hash:String,
  val title:String, val description:String, val body:String, val entities: List[NamedEntity])

class ArticleExtractorService {

  val config   = new Configuration
  config.setLocalStoragePath("/tmp/goose")
  config.setEnableImageFetching(false)

  val goose    = new Goose(config)

  val entities = new NamedEntityService

  def extract(url : String) : ExtractedArticle = {

    val article  = goose.extractContent(url)
    var text     = List(article.title, article.cleanedArticleText).filter(_ != null).mkString(" ")

    new ExtractedArticle(article.canonicalLink, article.domain, article.linkhash,
      article.title, article.metaDescription, article.cleanedArticleText, entities.classify(text))

  }

}