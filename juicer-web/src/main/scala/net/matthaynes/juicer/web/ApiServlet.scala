package net.matthaynes.juicer.web

import net.matthaynes.juicer.entities._

class ApiServlet extends JsonServlet {

  val service = new NamedEntityService

  get("/ping")        { Map("message"  -> "I'm alive!") }

  post("/entities")   { Map("entities" -> service.classify(params("text"))) }

}
