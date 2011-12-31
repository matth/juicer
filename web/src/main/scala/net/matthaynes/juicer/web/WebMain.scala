package net.matthaynes.juicer.web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import util.Properties

object WebMain extends App {

  val port = Properties.envOrElse("PORT", "8080").toInt
  val server  : Server = new Server(port)
  var context : ServletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)

  context.setContextPath("/")
  context.setInitParameter("org.eclipse.jetty.servlet.Default.resourceBase", "./web/target/scala-2.9.0-1/classes/webapp")
  server.setHandler(context)

  context.addServlet(new ServletHolder(new ApiServlet()), "/api/*")
  context.addServlet(new ServletHolder(new DefaultServlet()), "/*")

  server.start()
  server.join()

}