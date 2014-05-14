package com.blackboxsociety.mvc

import com.blackboxsociety.http._
import scalaz.concurrent._
import com.blackboxsociety.http.routes._

trait Controller {

  val number = """(\d+)"""

  val route: HttpRouteRule

  def middleware: List[(HttpRequest => Task[HttpResponse]) => (HttpRequest => Task[HttpResponse])] = List()

  def standardMiddleware = mark _ :: middleware

  val run = standardMiddleware.reverse.foldLeft[HttpRequest => Task[HttpResponse]](action) { (m, n) =>
    n(m)
  }

  def mark(next: HttpRequest => Task[HttpResponse]): HttpRequest => Task[HttpResponse] = { (request) =>
    next(request) map { response => response.markFromController() }
  }

  def action(request: HttpRequest): Task[HttpResponse]

}