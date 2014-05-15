package com.blackboxsociety

import com.blackboxsociety.util._
import scalaz.ImmutableArray
import scalaz.concurrent._
import scalaz.syntax.bind._
import com.blackboxsociety.net._
import com.blackboxsociety.http._

trait BlackBox {

  def port: Int

  def host: String

  def router: HttpRouter

  def middleware: List[(HttpRequest => Task[HttpResponse]) => (HttpRequest => Task[HttpResponse])] = List()

  lazy val handleRequest = middleware.reverse.foldLeft[HttpRequest => Task[HttpResponse]](route) { (m, n) =>
    n(m)
  }

  final def main(args: Array[String]) {
    run(ImmutableArray.fromArray(args))
  }

  def route(request: HttpRequest): Task[HttpResponse] = router.route(request)

  def genServer(): Task[Unit] = for (
    server <- TcpServer(host, port);
    _      <- Concurrency.forkForever(server.accept() >>= handleConnection)
  ) yield ()

  def handleConnection(client: TcpClient): Task[Unit] = for (
    response <- parseAndRoute(client).handle(handleError());
    _        <- HttpResponseConsumer.consume(client, response)
  ) yield ()

  def handleError(): PartialFunction[Throwable, HttpResponse] = {
    case HttpParserException(e)   =>
      InternalServerError(e)
    case MissingRouteException(r) =>
      Missing("These are not the droids you're looking for :-/")
  }

  def parseAndRoute(client: TcpClient): Task[HttpResponse] = for (
    request  <- HttpParser(client);
    response <- handleRequest(request)
  ) yield response

  def run(args: ImmutableArray[String]) = {
    genServer().runAsync({ _ => Unit})
    EventLoop.run()
  }

}
