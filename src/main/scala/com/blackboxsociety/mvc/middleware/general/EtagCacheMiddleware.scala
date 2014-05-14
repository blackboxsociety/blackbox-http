package com.blackboxsociety.mvc.middleware.general

import com.blackboxsociety.http._
import scalaz.concurrent.Task
import com.blackboxsociety.waterhouse.Hash

object EtagCacheMiddleware {

  def apply()(next: HttpRequest => Task[HttpResponse])(request: HttpRequest): Task[HttpResponse] = {
    request.headers.find(_.key == "If-None-Match") match {
      case None    => next(request).map(addHashToResponse)
      case Some(h) => next(request) map { response =>
        hashFromBody(response.body) match {
          case None       => response
          case Some(hash) =>
            if (hash == h.value)
              NotModified("").withHeaders(response.headers)
            else
              response.withHeader(EtagHeader(hash))
        }
      }
    }
  }

  private def addHashToResponse(response: HttpResponse): HttpResponse = {
    hashFromBody(response.body)
      .map({ hash => response.withHeader(EtagHeader(hash))})
      .getOrElse(response)
  }

  private def hashFromBody(body: HttpResponseBody): Option[String]= body match {
    case HttpStringResponseBody(s)      => Some(Hash.stringDigest[Hash.Sha1.type](s))
    case HttpByteResponseBody(b)        => Some(Hash.bytesDigest[Hash.Sha1.type](b).map("%02X" format _).mkString)
    case HttpFileChannelResponseBody(c) => None
  }

}
