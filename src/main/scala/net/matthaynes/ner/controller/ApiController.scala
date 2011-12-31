package net.matthaynes.ner.controller

import net.matthaynes.ner.service._

class ApiController extends ApiServlet {

  val service = new NamedEntityService

  get("/ping")        { respond(Map[String, String]("message" -> "I'm alive!")) }

  post("/entities")   { respond(Map[String, List[NamedEntity]]( "entities" -> service.classify(params("text")))) }

}
