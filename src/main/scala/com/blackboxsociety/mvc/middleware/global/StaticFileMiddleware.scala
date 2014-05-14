package com.blackboxsociety.mvc.middleware.global

import com.blackboxsociety.http._
import scalaz.concurrent.Task
import java.io._

object StaticFileMiddleware {

  def apply(url: String, path: String)
           (next: HttpRequest => Task[HttpResponse])
           (request: HttpRequest): Task[HttpResponse] =
  {
    if(request.resource.path.startsWith(url)) {
      val src = path + request.resource.path.substring(8)
      val file = new File(src)
      if(file.exists() && !src.contains("..")) {
        Task.now { Ok(new RandomAccessFile(file, "r").getChannel) }
      } else {
        next(request)
      }
    } else {
      next(request)
    }
  }

}
