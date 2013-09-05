package net.matthaynes.juicer.web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import java.util.TimeZone
import util.Properties
import com.cybozu.labs.langdetect.DetectorFactory

object WebMain extends App {
  System.setProperty("user.timezone", "GMT")
  TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

  val port = Properties.envOrElse("PORT", "8080").toInt
  val server  : Server = new Server(port)
  server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", -1);

  var context : ServletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)

  context.setContextPath("/")
  context.setInitParameter("org.eclipse.jetty.servlet.Default.resourceBase", "./juicer-web/src/main/resources/webapp")
  server.setHandler(context)

  DetectorFactory.loadProfile("./juicer-web/src/main/resources/langdetect/profiles") // this should be fixed to load the files from the jar, like http://stackoverflow.com/questions/12007603/java-language-detection-with-langdetect-how-to-load-profiles 
  // can also use profiles.sm for short messages (like twitter)

  context.addServlet(new ServletHolder(new ApiServlet()), "/api/*")
  context.addServlet(new ServletHolder(new DefaultServlet()), "/*")

  server.start()
  server.join()

}