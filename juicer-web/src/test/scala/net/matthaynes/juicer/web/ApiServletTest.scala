package net.matthaynes.juicer.web

import org.scalatra.test.scalatest.ScalatraFunSuite
import org.eclipse.jetty.servlet.ServletHolder

class ApiServletTest extends ScalatraFunSuite  {

  val servletHolder = addServlet(classOf[ApiServlet], "/*")

  def jsonResponse : Boolean = {
    header("Content-Type") == "application/json;charset=UTF-8"
  }

  def assertError(responseStatus : Int, responseMessage : String) {
    assert(jsonResponse)
    assert(status == responseStatus)
    assert(body   == "{\"error\":{\"status\":"+responseStatus.toString+",\"message\":\""+responseMessage+"\"}}")
  }

  test("/entities returns correct data") {
    post("/entities", List(("text","I went to London")))  {
      assert(jsonResponse)
      assert(status == 200)
      assert(body == "{\"entities\":[{\"type\":\"Location\",\"text\":\"London\",\"frequency\":1}]}")
    }
  }

  test("/entities returns nice message for Method Not Allowed error") {
    get("/entities")  {
      assertError(405, "Method Not Allowed")
      assert(header("Allow") == "POST")
    }
  }

  test("/entities returns nice message for missing param") {
    post("/entities") { assertError(400, "Bad Request key not found: text") }
  }

  test("/foobar returns nice message for Not Found error") {
    get("/foobar")    { assertError(404, "Not Found") }
  }

}


