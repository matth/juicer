package net.matthaynes.juicer.web

import net.matthaynes.juicer.entities._
import org.scalatra._
import net.liftweb.json._
import net.liftweb.json.Serialization.{write}
import net.liftweb.json.Xml.{toJson, toXml}

class JsonServlet extends ScalatraServlet {

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

  def respondWithError(message : String, responseStatus : Int = 500) : String = {
    val responseBody = Map("error" -> Map("status" -> responseStatus, "message" -> message))
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
