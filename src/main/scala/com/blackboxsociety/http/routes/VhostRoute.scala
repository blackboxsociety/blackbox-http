package com.blackboxsociety.http.routes

import com.blackboxsociety.http._

case class VhostRoute(host: String) extends HttpRouteRule {

  def route(request: HttpRequest): Option[HttpRequest] = {
    val header = request.headers.find(_.key == "Host")
    val value  = header map { _.value }
    if(value.filter(_ == host).nonEmpty)
      Some(request)
    else
      None
  }

}