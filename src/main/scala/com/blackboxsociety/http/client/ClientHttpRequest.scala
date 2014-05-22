package com.blackboxsociety.http.client

import scalaz.concurrent.Task
import com.blackboxsociety.http._
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import com.blackboxsociety.net.TcpClient

/**
 * TODO:
 *   * Host vs Path separation
 *   * Empirical testing
 *   * Support multiple body types
 *     * File streams
 *     * Strings
 */

case class ClientHttpRequest(url: String,
                             method: HttpMethod = HttpGet,
                             headers: Option[Seq[HttpHeader]] = None,
                             version: HttpVersion = HttpVersionOneDotOne,
                             body: Option[Array[Byte]] = None)
{

  def withHttpVersion(v: HttpVersion): ClientHttpRequest = {
    copy(version = v)
  }

  def withAuthentication(username: String, password: String, authType: AuthType): ClientHttpRequest = {
    copy(headers = Some(headers.getOrElse(List()) :+ AuthorizationHeader(authType(username, password))))
  }

  def withMethod(method: HttpMethod): ClientHttpRequest = {
    copy(method = method)
  }

  def withHeader(h: HttpHeader): ClientHttpRequest = {
    copy(headers = Some(headers.getOrElse(List() :+ h)))
  }

  def withHeaders(hs: HttpHeader*): ClientHttpRequest = {
    copy(headers = Some(headers.getOrElse(List()) ++ hs))
  }

  def withBody(b: Array[Byte]): ClientHttpRequest = {
    copy(body = Some(b))
  }

  def delete(): Task[ClientHttpResponse] = {
    withMethod(HttpDelete).query()
  }

  def get(params: (String, String)*): Task[ClientHttpResponse] = {
    withMethod(HttpGet).query()
  }

  def head(): Task[ClientHttpResponse] = {
    withMethod(HttpHead).query()
  }

  def options(): Task[ClientHttpResponse] = {
    withMethod(HttpOptions).query()
  }

  def post(params: (String, String)*): Task[ClientHttpResponse] = {
    post(params.map({ p => s"$p._1=$p._2" }).mkString("&").getBytes)
  }

  def post(b: Array[Byte]): Task[ClientHttpResponse] = {
    withMethod(HttpPost).withBody(b).query()
  }

  def put(params: (String, String)*): Task[ClientHttpResponse] = {
    put(params.map({ p => s"$p._1=$p._2" }).mkString("&").getBytes)
  }

  def put(b: Array[Byte]): Task[ClientHttpResponse] = {
    withMethod(HttpPut).withBody(b).query()
  }

  private def query(): Task[ClientHttpResponse] = {
    val c = TcpClient(SocketChannel.open(new InetSocketAddress(url, 80)))
    for (
      w <- c.write(this);
      r <- HttpResponseParser(c)
    ) yield r
  }

}

object ClientHttpRequest {

  implicit def toByteArray(request: ClientHttpRequest): Array[Byte] = {
    val init    = s"${request.method} ${request.url} ${request.version}\r\n"
    val headers = request.headers.getOrElse(List()).map({ n => s"${n.key}: ${n.value}\r\n" }).mkString + "\r\n"
    request.body match {
      case None    => init.getBytes ++ headers.getBytes
      case Some(b) => init.getBytes ++ headers.getBytes ++ b
    }

  }

}