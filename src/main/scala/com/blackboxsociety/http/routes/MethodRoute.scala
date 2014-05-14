package com.blackboxsociety.http.routes

import com.blackboxsociety.http._

case class MethodRoute(method: HttpMethod) extends HttpRouteRule {

  def route(request: HttpRequest): Option[HttpRequest] = {
    if(request.method == method)
      Some(request)
    else
      None
  }

}