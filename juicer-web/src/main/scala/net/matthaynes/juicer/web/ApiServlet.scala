package net.matthaynes.juicer.web

import net.matthaynes.juicer.service._

class ApiServlet extends JsonServlet {

  val service = new ArticleExtractorService

  get("/ping")        { Map("message"  -> "pong") }

  get("/article")     { Map("article"  -> service.extract(params("url"), params.getOrElse("force_snacktory", "false").toBoolean, params.getOrElse("extract_entities", "true").toBoolean )) }

  post("/article")    { Map("article"  -> service.extract_src(params("url"), params("src"), params.getOrElse("force_snacktory", "false").toBoolean, params.getOrElse("extract_entities", "true").toBoolean )) }

  post("/entities")   { Map("entities" -> service.entities.classify(params("text"))) }

}
