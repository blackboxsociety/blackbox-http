package com.blackboxsociety.http

sealed trait HttpMethod {

  override def toString = this match {
    case HttpGet     => "GET"
    case HttpHead    => "HEAD"
    case HttpPost    => "POST"
    case HttpPut     => "PUT"
    case HttpDelete  => "DELETE"
    case HttpTrace   => "TRACE"
    case HttpOptions => "OPTIONS"
    case HttpConnect => "CONNECT"
    case HttpPatch   => "PATCH"
  }

}

case object HttpGet     extends HttpMethod
case object HttpHead    extends HttpMethod
case object HttpPost    extends HttpMethod
case object HttpPut     extends HttpMethod
case object HttpDelete  extends HttpMethod
case object HttpTrace   extends HttpMethod
case object HttpOptions extends HttpMethod
case object HttpConnect extends HttpMethod
case object HttpPatch   extends HttpMethod