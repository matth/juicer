package net.matthaynes.ner.controller

import org.scalatra.test.scalatest.ScalatraFunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.{JUnitRunner, JUnitSuite}
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.eclipse.jetty.servlet.ServletHolder

class ApiControllerTest extends ScalatraFunSuite  {

  val servletHolder = addServlet(classOf[ApiController], "/*")

  def jsonResponse : Boolean = {
    header("Content-Type") == "application/json;charset=UTF-8"
  }

  def assertError(responseStatus : Int, responseMessage : String) {
    assert(jsonResponse)
    assert(status == responseStatus)
    assert(body   == "{\"error\":{\"status\":"+responseStatus.toString+",\"message\":\""+responseMessage+"\"}}")
  }

  test("/entities returns nice message for Method Not Allowed error") {
    get("/entities")  { assertError(405, "Method Not Allowed") }
  }

  test("/entities returns nice message for missing param") {
    post("/entities") { assertError(400, "Bad Request key not found: text") }
  }

  test("/foobar returns nice message for Not Found error") {
    get("/foobar")    { assertError(404, "Not Found") }
  }

}


