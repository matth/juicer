package net.matthaynes.juicer.web

import net.matthaynes.juicer.service._

class ApiServlet extends JsonServlet {

  val service = new ArticleExtractorService

  get("/ping")        { Map("message"  -> "pong") }

  get("/article")     { Map("article"  -> service.extract(params("url"))) }

  post("/entities")   { Map("entities" -> service.entities.classify(params("text"))) }

}
