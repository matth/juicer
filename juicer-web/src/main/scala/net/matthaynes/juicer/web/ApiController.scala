package net.matthaynes.juicer.web

import net.matthaynes.juicer.entities._

class ApiServlet extends JsonServlet {

  val service = new NamedEntityService

  get("/ping")        { respond(Map[String, String]("message" -> "I'm alive!")) }

  post("/entities")   { respond(Map[String, List[NamedEntity]]( "entities" -> service.classify(params("text")))) }

}
