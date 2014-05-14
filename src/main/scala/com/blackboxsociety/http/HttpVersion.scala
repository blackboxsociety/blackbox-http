package com.blackboxsociety.http

sealed trait HttpVersion {

  override def toString = this match {
    case HttpVersionOneDotZero => "HTTP/1.0"
    case HttpVersionOneDotOne  => "HTTP/1.1"
  }

}

case object HttpVersionOneDotZero extends HttpVersion
case object HttpVersionOneDotOne  extends HttpVersion