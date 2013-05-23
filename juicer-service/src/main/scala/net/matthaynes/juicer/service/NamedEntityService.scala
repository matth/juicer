package net.matthaynes.juicer.service

import edu.stanford.nlp.ie.crf._
import edu.stanford.nlp.ie.AbstractSequenceClassifier
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation

import scala.collection.BufferedIterator
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._

trait NamedEntity {
  val text      : String
  var frequency : Int
}

class Organization(val text : String, var frequency : Int = 0) extends NamedEntity
class Person(val text : String, var frequency : Int = 0) extends NamedEntity
class Location(val text : String, var frequency : Int = 0) extends NamedEntity

object NamedEntityService {
  val classifier = {
    val model = getClass.getResourceAsStream("/english.all.3class.distsim.crf.ser.gz")
    val is = new java.io.BufferedInputStream(model)
    val gzipped = new java.util.zip.GZIPInputStream(is)
    val c = CRFClassifier.getClassifier(gzipped)
    model.close
    is.close
    gzipped.close
    c
  }
}

class NamedEntityService {

  def classify(text : String) : List[NamedEntity] = {

    val results   = NamedEntityService.classifier.classify(text)

    val entities  = asScalaBuffer(results).foldLeft(new HashMap[String, NamedEntity]) {

      (entities, sentence) => {

        var tokens = asScalaBuffer(sentence).map(_.asInstanceOf[CoreLabel]).iterator.buffered

        while (tokens.hasNext) {

          getAnnotationType(tokens.head) match {
            case "ORGANIZATION" => addNamedEntity(entities, new Organization(getNamedEntityForAnnotation(tokens, "ORGANIZATION")))
            case "LOCATION"     => addNamedEntity(entities, new Location(getNamedEntityForAnnotation(tokens, "LOCATION")))
            case "PERSON"       => addNamedEntity(entities, new Person(getNamedEntityForAnnotation(tokens, "PERSON")))
            case _              => tokens.next
          }

        }

        entities

      }
    }

    entities.values.toList

  }

  private def addNamedEntity(entities : HashMap[String, NamedEntity], entity : NamedEntity) {
    entities.getOrElseUpdate(entity.text, entity).frequency += 1
  }

  private def getNamedEntityForAnnotation(tokens : BufferedIterator[CoreLabel], annotation : String) : String = {
    tokens.takeWhile { s => getAnnotationType(s) == annotation }.map(_.word).mkString(" ")
  }

  private def getAnnotationType(token : CoreLabel) : String = {
    token.get[String, AnswerAnnotation](classOf[AnswerAnnotation])
  }

}
