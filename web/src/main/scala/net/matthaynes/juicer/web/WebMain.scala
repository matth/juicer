package net.matthaynes.juicer.web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler

object WebMain extends App {

  val server  : Server = new Server(8080)
  var context : ServletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)

  context.setContextPath("/")
  context.setInitParameter("org.eclipse.jetty.servlet.Default.resourceBase", "./web/target/scala-2.9.0-1/classes/webapp")
  server.setHandler(context)

  context.addServlet(new ServletHolder(new ApiServlet()), "/api/*")
  context.addServlet(new ServletHolder(new DefaultServlet()), "/*")

  server.start()
  server.join()

}