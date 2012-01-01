package net.matthaynes.juicer.web

import net.matthaynes.juicer.service._
import org.scalatra._
import net.liftweb.json._
import net.liftweb.json.Serialization.{write}
import net.liftweb.json.Xml.{toJson, toXml}

trait JsonServlet extends ScalatraServlet {

  implicit val formats = new Formats {
    val dateFormat = DefaultFormats.lossless.dateFormat
    override val typeHints = ShortTypeHints(List(classOf[NamedEntity]))
    override val typeHintFieldName = "type"
  }

  override protected def renderResponseBody(actionResult: Any) {
    super.renderResponseBody(write(actionResult.asInstanceOf[AnyRef]))
  }

  def renderError(code : Int, message : String, headers : Map[String, String] = Map()) = {
    status(code)
    headers foreach { case (k, v) => { response.setHeader(k, v) } }
    Map("error" -> Map("status" -> code, "message" -> message))
  }

  before() {
    contentType = "application/json"
  }

  notFound {
    renderError(404, "Not Found")
  }

  methodNotAllowed { allow =>
    renderError(405, "Method Not Allowed", Map("Allow" -> allow.mkString(", ")))
  }

  error {
    case e : java.util.NoSuchElementException => renderError(400, "Bad Request " + e.getMessage)
    case e : Exception => renderError(500, "Internal Server Error " + e.getMessage)
  }

}
