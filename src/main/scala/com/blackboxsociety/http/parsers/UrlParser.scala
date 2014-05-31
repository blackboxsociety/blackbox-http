package com.blackboxsociety.http.parsers

import scala.util.parsing.combinator._
import scalaz.Validation

case class Url(protocol: String, host: String, port: Option[Int], path: String, query: Option[String]) {
  override def toString: String = s"$protocol://$host${port.map(":" + _.toString)}$toRequestPath"
  def toRequestPath: String = s"$path${query.getOrElse("")}"
}

object Url {
  implicit def implicitToString(url: Url): String = url.toString
}

object UrlParser extends RegexParsers {

  val protocol: Parser[String] = opt("[^:]+".r <~ "://") ^^ { _.getOrElse("http").toLowerCase }

  val host: Parser[String] = "[^:\\/]+".r

  val port: Parser[Option[Int]] = opt(":" ~> "[0-9]+".r) ^^ { _.map(_.toInt) }

  val path: Parser[String] = opt("/[^?]+".r) ^^ { _.getOrElse("/") }

  val query: Parser[Option[String]] = opt("?" ~> ".+".r)

  val urlParser: Parser[Url] = protocol ~ host ~ port ~ path ~ query ^^ {
    case pr ~ ho ~ po ~ pa ~ qu => Url(pr, ho, po, pa, qu)
  }

  def apply(url: String): Validation[String, Url] = {
    parseAll(urlParser, url) match {
      case NoSuccess(e) => scalaz.Failure(e)
      case Success(u)   => scalaz.Success(u)
    }
  }

}
