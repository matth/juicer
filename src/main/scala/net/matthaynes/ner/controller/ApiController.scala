package net.matthaynes.ner.controller

import net.matthaynes.ner.service._
import org.scalatra._

class ApiController extends ScalatraServlet {

  val service       = new NamedEntityService

  before()          { contentType = "application/json" }

  get("/ping")      { "{\"message\":\"I'm alive!\"}" }

  post("/entities") { service.classify(params("text")).toJson }

}
