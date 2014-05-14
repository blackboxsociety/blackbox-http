package com.blackboxsociety.http

import com.blackboxsociety.mvc._
import scalaz.concurrent._
import scalaz.concurrent.Task._

case class MissingRouteException(r: HttpRequest) extends Throwable

case class HttpRouter(controllers: Controller*) {

  def route(request: HttpRequest): Task[HttpResponse] = {
    controllers.map(h => (h, h.route.route(request))).find(_._2.isDefined) match {
      case Some((c: Controller, Some(h: HttpRequest))) => c.run(h)
      case _ => fail(MissingRouteException(request))
    }
  }

}