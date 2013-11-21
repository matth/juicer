package net.matthaynes.juicer.web

import org.eclipse.jetty.server.NCSARequestLog
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.handler.RequestLogHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import util.Properties

object WebMain extends App {

  val handlers = new HandlerCollection
  val contexts = new ContextHandlerCollection

  // Handle servlets
  val servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
  servletHandler.setContextPath("/")
  servletHandler.setInitParameter("org.eclipse.jetty.servlet.Default.resourceBase", "./juicer-web/src/main/resources/webapp")
  servletHandler.addServlet(new ServletHolder(new ApiServlet()), "/api/*")
  servletHandler.addServlet(new ServletHolder(new DefaultServlet()), "/*")

  // Handle Logging
  val requestLogHandler = new RequestLogHandler

  val requestLog = new NCSARequestLog

  requestLog.setAppend(true)
  requestLog.setExtended(true)
  requestLog.setLogTimeZone("GMT")
  requestLogHandler.setRequestLog(requestLog)

  // Set handlers
  handlers.addHandler(servletHandler)
  handlers.addHandler(requestLogHandler)

  // Start server
  val port   = Properties.envOrElse("PORT", "8080").toInt

  val server = new Server(port)

  server.setHandler(handlers)

  server.start()
  server.join()

}
