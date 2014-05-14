package com.blackboxsociety.http.routes

import com.blackboxsociety.http._

trait HttpRouteRule {

  def route(request: HttpRequest): Option[HttpRequest]

}