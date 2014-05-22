package com.blackboxsociety.http.client

import scala.util.parsing.combinator._
import scalaz.concurrent._
import scalaz.concurrent.Task._
import com.blackboxsociety.util.parser._
import com.blackboxsociety.util._
import com.blackboxsociety.http._

case class ParsedHttpResponse(version: HttpVersion,
                              responseValue: Int,
                              responseCode: String,
                              headers: List[HttpHeader])

object HttpResponseParser extends RegexParsers {

  override def skipWhitespace = false

  def spaces = """[ ]+""".r

  def lineEnd = "\r\n"

  def nonEndings = "[^\r\n]+".r

  def httpVersionLegacy: Parser[HttpVersion] = "HTTP/1.0" ^^ { _ =>
    HttpVersionOneDotZero
  }

  def httpVersionModern: Parser[HttpVersion] = "HTTP/1.1" ^^ { _ =>
    HttpVersionOneDotOne
  }

  def httpVersion: Parser[HttpVersion] = httpVersionLegacy | httpVersionModern

  def responseValue: Parser[Int] = "\\d+".r ^^ {
    _.toInt
  }

  def responseCode: Parser[String] = "([^\\s]+)".r ^^ {
    _.toString
  }

  def httpHeader: Parser[HttpHeader] = "[^:\r\n]+".r ~ ":" ~ spaces ~ nonEndings ~ lineEnd ^^ {
    case (key ~ _ ~ _ ~ value ~ _) => HttpGenericHeader(key, value)
  }

  def httpBody: Parser[String] = ".+".r ^^ {
    x => x
  }

  def httpParser: Parser[ParsedHttpResponse] = httpVersion     ~
                                               " "             ~
                                               responseValue   ~
                                               " "             ~
                                               responseCode    ~
                                               lineEnd         ~
                                               rep(httpHeader) ~
                                               lineEnd         ^^
    {
      case(version ~ _ ~ rVal ~ _ ~ rCode ~ _ ~ headers ~ _) => ParsedHttpResponse(
        version,
        rVal,
        rCode,
        headers
      )
    }

  def apply(stream: ParserStream): Task[ClientHttpResponse] = {
    stream.latest flatMap { f =>
      f.current match {
        case More(s) => parse(httpParser, s) match {
          case Success(response, next) =>
            finishWithSuccess(stream, response, next, Finishable.more)
          case NoSuccess(error, _)    => "source found$".r findFirstIn error match {
            case None => fail(HttpParserException(error))
            case _    => apply(f)
          }
        }
        case Done(s) => parse(httpParser, s) match {
          case Success(response, next) =>
            finishWithSuccess(stream, response, next, Finishable.done)
          case NoSuccess(error, _)    => "source found$".r findFirstIn error match {
            case None => fail(HttpParserException("wat"/*error*/))
            case _    => fail(HttpParserException("Received an incomplete HTTP response."))
          }
        }
      }
    }
  }

  private def finishWithSuccess(stream: ParserStream,
                                req: ParsedHttpResponse,
                                next: HttpResponseParser.Input,
                                f: String => Finishable[String]): Task[ClientHttpResponse] = now {
    ClientHttpResponse(
      version       = req.version,
      responseValue = req.responseValue,
      responseCode  = req.responseCode,
      headers       = req.headers,
      body          = stream.withText(f(next.source.toString.substring(next.offset)))
    )
  }

}

