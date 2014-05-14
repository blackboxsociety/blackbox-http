package com.blackboxsociety.http

trait HttpHeader {
  val key:   String
  val value: String

  override def toString: String = s"$key: $value"
}

case class HttpGenericHeader(key: String, value: String) extends HttpHeader

abstract class HttpBaseHeader(val key: String) extends HttpHeader

case class AcceptRangesHeader(value: String) extends HttpBaseHeader("Accept-Ranges")
case class AgeHeader(value: String) extends HttpBaseHeader("Age")
case class AllowHeader(value: String) extends HttpBaseHeader("Allow")
case class AuthorizationHeader(value: String) extends HttpBaseHeader("Authorization")
case class CacheControlHeader(value: String) extends HttpBaseHeader("Cache-Control")
case class ConnectionHeader(value: String) extends HttpBaseHeader("Connection")
case class ContentEncodingHeader(value: String) extends HttpBaseHeader("Content-Encoding")
case class ContentLanguageHeader(value: String) extends HttpBaseHeader("Content-Language")
case class ContentLengthHeader(value: String) extends HttpBaseHeader("Content-Length")
case class ContentLocationHeader(value: String) extends HttpBaseHeader("Content-Location")
case class ContentMD5Header(value: String) extends HttpBaseHeader("Content-MD5")
case class ContentRangeHeader(value: String) extends HttpBaseHeader("Content-Range")
case class ContentTypeHeader(value: String) extends HttpBaseHeader("Content-Type")
case class DateHeader(value: String) extends HttpBaseHeader("Date")
case class EtagHeader(value: String) extends HttpBaseHeader("Etag")
case class ExpiresHeader(value: String) extends HttpBaseHeader("Expires")
case class LastModifiedHeader(value: String) extends HttpBaseHeader("Last-Modified")
case class LocationHeader(value: String) extends HttpBaseHeader("Location")
case class PragmaHeader(value: String) extends HttpBaseHeader("Pragma")
case class ProxyAuthenticationHeader(value: String) extends HttpBaseHeader("Proxy-Authentication")
case class RetryAfterHeader(value: String) extends HttpBaseHeader("Retry-After")
case class ServerHeader(value: String) extends HttpBaseHeader("Server")
case class SetCookieHeader(value: String) extends HttpBaseHeader("Set-Cookie")
case class TransferEncodingHeader(value: String) extends HttpBaseHeader("Transfer-Encoding")
case class VaryHeader(value: String) extends HttpBaseHeader("Vary")
case class WarningHeader(value: String) extends HttpBaseHeader("Warning")
case class WWWAuthenticateHeader(value: String) extends HttpBaseHeader("WWW-Authenticate")