package net.matthaynes.juicer.web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import java.util.TimeZone
import util.Properties

object WebMain extends App {
  System.setProperty("user.timezone", "GMT")
  TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

  val port = Properties.envOrElse("PORT", "8080").toInt
  val server  : Server = new Server(port)
  var context : ServletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)

  context.setContextPath("/")
  context.setInitParameter("org.eclipse.jetty.servlet.Default.resourceBase", "./juicer-web/src/main/resources/webapp")
  server.setHandler(context)

  context.addServlet(new ServletHolder(new ApiServlet()), "/api/*")
  context.addServlet(new ServletHolder(new DefaultServlet()), "/*")

  server.start()
  server.join()

}