package net.matthaynes.juicer.web

import net.matthaynes.juicer.entities._

class ApiServlet extends JsonServlet {

  val service = new NamedEntityService

  get("/ping")        { respond(Map("message"  -> "I'm alive!")) }

  post("/entities")   { respond(Map("entities" -> service.classify(params("text")))) }

}
