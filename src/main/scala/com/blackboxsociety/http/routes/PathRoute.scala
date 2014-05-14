package com.blackboxsociety.http.routes

import com.blackboxsociety.http._

case class PathRoute(path: String) extends HttpRouteRule {

  def route(request: HttpRequest): Option[HttpRequest] = {
    if(request.resource.path == path)
      Some(request)
    else
      None
  }

}