package net.matthaynes.ner.controller

import net.matthaynes.ner.service._
import org.scalatra._
import net.liftweb.json._
import net.liftweb.json.Serialization.{write}

class ApiController extends ScalatraServlet {

  implicit val formats = new Formats {
    val dateFormat = DefaultFormats.lossless.dateFormat
    override val typeHints = ShortTypeHints(List(classOf[NamedEntity]))
    override val typeHintFieldName = "type"
  }

  val service          = new NamedEntityService

  before()            { contentType = "application/json" }

  get("/ping")        { write(Map[String, String]("message" -> "I'm alive!")) }

  post("/entities")   { write(Map[String, List[NamedEntity]]( "entities" -> service.classify(params("text")))) }

}
