package net.matthaynes.juicer.service

import com.gravity.goose._

class ArticleExtractorService {

  val config = new Configuration
  config.setLocalStoragePath("/tmp/goose")
  config.setEnableImageFetching(false)

  val goose  = new Goose(config)

  def extract(url : String) {
    val article = goose.extractContent(url)
    println(article.cleanedArticleText)
  }

}