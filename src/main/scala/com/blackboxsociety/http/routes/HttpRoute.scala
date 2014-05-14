package com.blackboxsociety.http.routes

import com.blackboxsociety.http._

case class HttpRoute(rules: HttpRouteRule*) extends HttpRouteRule {

  def route(request: HttpRequest): Option[HttpRequest] = {
    rules.foldLeft(Option(request))((a, b) => a.flatMap { b.route(_) })
  }

}