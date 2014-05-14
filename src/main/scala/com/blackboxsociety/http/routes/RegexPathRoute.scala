package com.blackboxsociety.http.routes

import com.blackboxsociety.http._
import scala.util.matching.Regex

case class RegexPathRoute(path: String, args: String*) extends HttpRouteRule {

  val regPath = new Regex(path, args: _*)

  def route(request: HttpRequest): Option[HttpRequest] = {
    val r = regPath findFirstMatchIn request.resource.path
    val n = r.map { m =>
      m.groupNames.map(g => (g, m.group(g)))
    } map { m =>
      Map(m: _*)
    }

    n.map(r => request.copy(pathVars = r))
  }

}
