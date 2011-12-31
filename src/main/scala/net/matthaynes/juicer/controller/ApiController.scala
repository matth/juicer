package net.matthaynes.juicer.controller

import net.matthaynes.juicer.service._

class ApiController extends ApiServlet {

  val service = new NamedEntityService

  get("/ping")        { respond(Map[String, String]("message" -> "I'm alive!")) }

  post("/entities")   { respond(Map[String, List[NamedEntity]]( "entities" -> service.classify(params("text")))) }

}
