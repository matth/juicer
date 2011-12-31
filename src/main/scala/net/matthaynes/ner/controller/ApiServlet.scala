package net.matthaynes.juicer.controller

import net.matthaynes.juicer.service._
import org.scalatra._
import net.liftweb.json._
import net.liftweb.json.Serialization.{write}
import net.liftweb.json.Xml.{toJson, toXml}

class ApiServlet extends ScalatraServlet {

  implicit val formats = new Formats {
    val dateFormat = DefaultFormats.lossless.dateFormat
    override val typeHints = ShortTypeHints(List(classOf[NamedEntity]))
    override val typeHintFieldName = "type"
  }

  def respond(responseBody : Map[String, Any], responseStatus : Int = 200) : String = {
    status(responseStatus)
    contentType = "application/json"
    write(responseBody)
  }

  def respondWithError(message : String, responseStatus : Int = 500) = {
    val responseBody = Map[String, Map[String, Any]]("error" -> Map[String, Any]("status" -> responseStatus, "message" -> message))
    respond(responseBody, responseStatus)
  }

  notFound {
    respondWithError("Not Found", 404)
  }

  methodNotAllowed { allow =>
    response.setHeader("Allow", allow.mkString(", "))
    respondWithError("Method Not Allowed", 405)
  }

  error {
    case e : java.util.NoSuchElementException => respondWithError("Bad Request " + e.getMessage, 400)
    case e : Exception => respondWithError("Internal Server Error " + e.getMessage, 500)
  }

}
