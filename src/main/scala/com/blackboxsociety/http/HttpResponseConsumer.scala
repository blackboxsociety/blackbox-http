package com.blackboxsociety.http

import scalaz.concurrent.Task
import com.blackboxsociety.net.TcpClient

object HttpResponseConsumer {

  def consume(client: TcpClient, response: HttpResponse): Task[Unit] = {
    val statusLine  = genStatusLine(response)
    val headerLines = genHeaderLines(response)
    val sessionLine = genSessionHeaderLines(response).getOrElse("")
    val flashLine   = genFlashHeaderLines(response)

    for (
      _ <- client.write(s"$statusLine$headerLines$sessionLine$flashLine");
      n <- response.body.write(client)
    ) yield n
  }

  private def genStatusLine(response: HttpResponse): String = {
    s"HTTP/1.1 ${response.statusCode} OK\r\n"
  }

  private def genHeaderLines(response: HttpResponse): String = {
    if (response.headers.size > 0) {
      response.headers.map(_.toString).mkString("\r\n") + "\r\n"
    } else {
      ""
    }
  }

  private def genSessionHeaderLines(response: HttpResponse): Option[String] = {
    response.session map { s =>
      SetCookieHeader(s"session=${s.signature()}${s.toJson}; Path=/; HttpOnly").toString + "\r\n"
    }
  }

  private def genFlashHeaderLines(response: HttpResponse): String = {
    response.flash match {
      case Some(f) => SetCookieHeader(s"flash=${f.signature()}${f.toJson}; Path=/; HttpOnly").toString + "\r\n"
      case None    =>
        if (response.fromController)
          SetCookieHeader(s"flash=none; Path=/; HttpOnly").toString + "\r\n"
        else
          ""
    }
  }

}
