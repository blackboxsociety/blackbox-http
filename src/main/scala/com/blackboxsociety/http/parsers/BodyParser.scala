package com.blackboxsociety.http.parsers

import com.blackboxsociety.http._
import scalaz.concurrent._

trait BodyParser[A] {

  def fromBody(request: HttpRequest): Task[A]

}




