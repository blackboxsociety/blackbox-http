package com.blackboxsociety.http.parsers

import com.blackboxsociety.http._
import com.blackboxsociety.json._
import scalaz._
import scalaz.concurrent._

object JsonBodyParser extends BodyParser[JsValue] {

  def fromBody(request: HttpRequest): Task[JsValue] = {
    request.getBody().flatMap { s =>
      JsonParser.parse(s) match {
        case Failure(e) => Task.fail(new Exception(e))
        case Success(j) => Task.now(j)
      }
    }
  }

}
