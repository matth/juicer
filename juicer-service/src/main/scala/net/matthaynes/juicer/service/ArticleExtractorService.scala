package net.matthaynes.juicer.service

import com.gravity.goose._
import images.Image

import scala.collection.JavaConversions._

import extractors.PublishDateExtractor
import extractors.AdditionalDataExtractor

import org.apache.commons.lang.time._
import java.text.ParseException
import java.net.URL
import java.util.Date
import org.jsoup.Jsoup
import org.jsoup.select.Selector
import org.jsoup.nodes.{Document, Element}
import net.liftweb.json._

import de.jetwick.snacktory._
import com.cybozu.labs.langdetect._
import org.slf4j.{Logger, LoggerFactory}

case class ExtractedArticle(
  val url:String, 
  val domain:String, 
  val hash:String, 
  val publishDate:Date,
  val title:String, 
  val description:String, 
  val body:String, 
  val entities:Option[List[NamedEntity]], 
  val links:List[Map[String, String]], 
  val topImage:String, 
  val additionalData:Map[String, String]
)

class ArticleExtractorService {
  val logger =  LoggerFactory.getLogger(getClass)

  val config = new Configuration
  config.setLocalStoragePath("/tmp/goose")
  config.setEnableImageFetching(true)

  config.setAdditionalDataExtractor(new AdditionalDataExtractor() {
    @Override
    override def extract(rootElement: Element): Map[String, String] = {
      return Map("author" -> extract_author(rootElement), "siteName" -> extract_sitename(rootElement))
    }

    def extract_author(rootElement: Element): String = {
      // opengraph
      var elements = Selector.select("meta[property=article:author]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          return el.attr("content");
        }
      }

      // schema.org creativework
      elements = Selector.select("meta[itemprop=author], span[itemprop=author]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          return el.attr("content");
        } else {
          return el.text();
        }
      } 

      // parsely page
      elements = Selector.select("meta[name=parsely-page]", rootElement)
      if (elements.size() > 0) {
        implicit val formats = net.liftweb.json.DefaultFormats

        val el = elements.get(0);
        if(el.hasAttr("content")) {
          val json = parse(el.attr("content"))
          return (json \ "author").extract[String]
        }
      } 

      // html5 author
      elements = Selector.select("a[rel=author], span[class=author]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          return el.attr("content");
        } else {
          return el.text();
        }
      } 

      return null
    }

    def extract_sitename(rootElement: Element): String = {
      // opengraph site name 
      var elements = Selector.select("meta[property=og:site_name]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          return el.attr("content");
        }
      }

      // schema.org
      elements = Selector.select("*[itemprop=copyrightHolder] > *[itemprop=name]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          return el.attr("content");
        }
      }

      return null
    }
  })

  config.setPublishDateExtractor(new PublishDateExtractor() {
    @Override
    def extract(rootElement: Element): Date = {
      // opengraph 
      var elements = Selector.select("meta[property=article:published_time]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          var dateStr = el.attr("content");

          if (dateStr.endsWith("Z")) {
            dateStr = dateStr.substring(0, dateStr.size - 1) + "GMT-00:00"
          }
          else {
            dateStr = "%sGMT%s".format(dateStr.substring(0, dateStr.size - 6), dateStr.substring(dateStr.size - 6, dateStr.size))
          }

          return parseDate(dateStr)
        }
      }

      // rnews 
      elements = Selector.select("meta[property=dateCreated], span[property=dateCreated]", rootElement)

      if (elements.size() > 0) {
        
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          val dateStr = el.attr("content");

          return parseDate(dateStr)
        } else {
          return parseDate(el.text())
        }
      }

      // schema.org creativework
      elements = Selector.select("meta[itemprop=datePublished], span[itemprop=datePublished]", rootElement)

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          val dateStr = el.attr("content");

          return parseDate(dateStr)
        } else if (el.hasAttr("value")) {
          val dateStr = el.attr("value");

          return parseDate(dateStr)
        } else {
          return parseDate(el.text())
        }
      } 

      // parsely page
      elements = Selector.select("meta[name=parsely-page]", rootElement)
      if (elements.size() > 0) {
        implicit val formats = net.liftweb.json.DefaultFormats

        val el = elements.get(0);
        if(el.hasAttr("content")) {
          val json = parse(el.attr("content"))

          return DateUtils.parseDateStrictly((json \ "pub_date").extract[String], Array("yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZZ", "yyyy-MM-dd'T'HH:mm:ssz"))
        }
      } 
      
      // BBC
      elements = Selector.select("meta[name=OriginalPublicationDate]", rootElement);

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          val dateStr = el.attr("content");

          return parseDate(dateStr)
        }
      }

      // wired
      elements = Selector.select("meta[name=DisplayDate]", rootElement);

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          val dateStr = el.attr("content");

          return parseDate(dateStr)
        }
      }

      // wildcard
      elements = Selector.select("meta[name*=date]", rootElement);

      if (elements.size() > 0) {
        val el = elements.get(0);
        if (el.hasAttr("content")) {
          val dateStr = el.attr("content");

          return parseDate(dateStr)
        }
      }

      return null
    }

    def parseDate(dateStr: String): Date = {
      val parsePatterns = Array("yyyy-MM-dd'T'HH:mm:ssz", 
        "yyyy-MM-dd HH:mm:ss", 
        "yyyy/MM/dd HH:mm:ss", 
        "yyyy-MM-dd HH:mm",
        "yyyy/MM/dd HH:mm",
        "yyyy-MM-dd", 
        "yyyy/MM/dd",
        "MM/dd/yyyy HH:mm:ss",
        "MM-dd-yyyy HH:mm:ss",
        "MM/dd/yyyy HH:mm",
        "MM-dd-yyyy HH:mm",
        "MM/dd/yyyy",
        "MM-dd-yyyy",
        "EEE, MMM dd, yyyy",
        "MM/dd/yyyy hh:mm:ss a",
        "MM-dd-yyyy hh:mm:ss a",
        "MM/dd/yyyy hh:mm a",
        "MM-dd-yyyy hh:mm a",
        "yyyy-MM-dd hh:mm:ss a", 
        "yyyy/MM/dd hh:mm:ss a ", 
        "yyyy-MM-dd hh:mm a",
        "yyyy/MM/dd hh:mm ",
        "dd MMM yyyy",
        "dd MMMM yyyy",
        "yyyyMMddHHmm",
        "yyyyMMdd HHmm",
        "dd-MM-yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm:ss",
        "dd MMM yyyy HH:mm:ss",
        "dd MMMM yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "dd/MM/yyyy HH:mm",
        "dd MMM yyyy HH:mm",
        "dd MMMM yyyy HH:mm",
        "yyyyMMddHHmmss",
        "yyyyMMdd HHmmss",
        "yyyyMMdd"
      )

      try {
        return DateUtils.parseDateStrictly(dateStr, parsePatterns)
      } catch {
        case e: ParseException => return null
      }
    }
  })

  val snacktory = new HtmlFetcher
  snacktory.setReferrer("https://www.google.com")
  snacktory.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36")
  val snacktoryExtractor = new ArticleTextExtractor
  val snacktoryFormatter = new OutputFormatter

  val ajax_fragment = "#!"
  val escaped_fragment_replacement = "?_escaped_fragment_="
  
  val goose = new Goose(config)

  val entities = new NamedEntityService

  def extract(url : String, force_goose : Boolean = false, extract_entities : Boolean = true) : ExtractedArticle = {
    if (!force_goose) {
      val fixed_url = get_ajax_ugly_url(url)
      val article = snacktory.fetchAndExtract(fixed_url, 20000, true)
      var text = List(article.getTitle, article.getDescription, article.getText).filter(_ != null).mkString(" ")

      val lang = get_language(text)

      return new ExtractedArticle(
        get_ajax_pretty_url(article.getUrl),    // should be handling this + canonicalization inside snacktory
        get_domain(article.getUrl),             //
        "", 
        article.getDate,
        article.getTitle, 
        article.getDescription, 
        article.getText, 
        Option(extract_entities).filter(_ == true).map(_ => entities.classify(article.getText)), 
        Option(article.getLinks.toList.map(_.toMap)).getOrElse(null), 
        article.getImageUrl, 
        Map("author" -> article.getAuthorName, "authorDescription" ->  article.getAuthorDescription, "language" -> lang, "extractor" -> "snacktory")
      )
    } else {
      val article  = goose.extractContent(url)
      var text     = List(article.title, article.cleanedArticleText).filter(_ != null).mkString(" ")
      val lang = get_language(text)

      return new ExtractedArticle(
        article.canonicalLink, 
        article.domain, 
        article.linkhash, 
        article.publishDate,
        article.title, 
        article.metaDescription, 
        article.cleanedArticleText, 
        Option(extract_entities).filter(_ == true).map(_ => entities.classify(text)), 
        Option(article.links.map(_.toMap)).getOrElse(null), 
        Option(article.topImage).map(_.imageSrc).getOrElse(null), 
        Option(article.additionalData ++ Map("language" -> lang, "extractor" -> "goose")).map(_.toMap).getOrElse(null)
      )
    }
  }

  def extract_src(url : String, src : String, force_goose : Boolean = false, extract_entities : Boolean = true) : ExtractedArticle = {
    if (!force_goose) {
      val fixed_url = get_ajax_ugly_url(url)
      val document = Jsoup.parse(src, fixed_url)
      val article = snacktoryExtractor.extractContent(new JResult, document, snacktoryFormatter)
      var text = List(article.getTitle, article.getDescription, article.getText).filter(_ != null).mkString(" ")
      val lang = get_language(text)
      val canonical_url = get_canonical_url(document, fixed_url)

      return new ExtractedArticle(
        canonical_url, 
        get_domain(canonical_url), 
        "", 
        article.getDate,
        article.getTitle, 
        article.getDescription, 
        article.getText, 
        Option(extract_entities).filter(_ == true).map(_ => entities.classify(article.getText)), 
        Option(article.getLinks.toList.map(_.toMap)).getOrElse(null), 
        article.getImageUrl, 
        Map("author" -> article.getAuthorName, "authorDescription" ->  article.getAuthorDescription, "language" -> lang, "extractor" -> "snacktory")
      )
    } else {
      val article  = goose.extractContent(url, src)
      var text = List(article.title, article.cleanedArticleText).filter(_ != null).mkString(" ")
      val lang = get_language(text)

      return new ExtractedArticle(
        article.canonicalLink, 
        article.domain, 
        article.linkhash, 
        article.publishDate,
        article.title, 
        article.metaDescription, 
        article.cleanedArticleText, 
        Option(extract_entities).filter(_ == true).map(_ => entities.classify(text)), 
        Option(article.links.map(_.toMap)).getOrElse(null), 
        Option(article.topImage).map(_.imageSrc).getOrElse(null), 
        Option(article.additionalData ++ Map("language" -> lang, "extractor" -> "goose")).map(_.toMap).getOrElse(null)
      )
    }
  }

  def get_language(text: String): String = {
    try {
      val language_detector = DetectorFactory.create

      language_detector.append(text)
      return language_detector.detect
    } catch {
      case e: LangDetectException => return ""
    } 
  }

  /**
   * Get the absolute canonical url from the document, or the default url if none is present
   */
  def get_canonical_url(doc: Document, default_url : String): String = {
    var url = doc.select("link[rel=canonical]").attr("abs:href")
    logger.trace("base uri: " + doc.baseUri)
    logger.trace("canonical link: " + url)

    if (url.isEmpty) {
      url = doc.select("meta[property=og:url]").attr("abs:content")

      logger.trace("canonical link meta og: " + url)
      if (url.isEmpty) {
        url = doc.select("meta[name=twitter:url]").attr("abs:content")

        logger.trace("canonical link meta twitter: " + url)
      }
    }
    if (url.nonEmpty) {
      val href = url.trim
      if (href.nonEmpty) href else default_url
    } else {
      default_url
    }
  }

  def get_ajax_ugly_url(url: String): String = {
    if (url.contains(ajax_fragment)) {
      return url.replaceAllLiterally(ajax_fragment, escaped_fragment_replacement)
    }
    return url
  }

  def get_ajax_pretty_url(url: String): String = {
    if (url.contains(escaped_fragment_replacement)) {
      return url.replaceAllLiterally(escaped_fragment_replacement, ajax_fragment)
    }
    return url
  }
  def get_domain(url: String): String = {
    new URL(url).getHost
  }
}