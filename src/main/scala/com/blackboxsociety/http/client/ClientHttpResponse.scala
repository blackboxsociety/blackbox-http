package com.blackboxsociety.http.client

import com.blackboxsociety.http.{HttpHeader, HttpVersion}
import com.blackboxsociety.util.parser.ParserStream
import com.blackboxsociety.util.{Done, More}
import scalaz.concurrent.Task

case class ClientHttpResponse(version: HttpVersion,
                              responseValue: Int,
                              responseCode: String,
                              headers: List[HttpHeader],
                              body: ParserStream) {

  def getHeader(key: String): Option[String] = {
    headers.find(_.key == key).map(_.value)
  }

  def getBody(ps: ParserStream = body): Task[String] = {
    ps.latest.flatMap { p => {
      p.current match {
      case Done(d: String) =>
        Task.now{d}
      case More(m: String) =>
        getBody(p)
    }}}
  }

}
