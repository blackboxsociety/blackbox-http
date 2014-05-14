package com.blackboxsociety.http.parsers

import com.blackboxsociety.http._

trait QueryStringParser[A] {

  def fromQueryString(request: HttpRequest): A

}

object QueryStringParser {

  def queryStringToMap(q: String): Map[String, String] = {
    if(q == "") {
      Map()
    } else {
      def ands = q.split("&")
      def es: Array[Array[String]] = ands.map(_.split("="))
      es.map(s => (s(0), s(1))).toMap
    }
  }

}
