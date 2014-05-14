package com.blackboxsociety.http

import com.blackboxsociety.util.parser.QueryStringParser

case class HttpResource(path: String, queryString: Option[String] = None) {

  val queryParams: Map[String, String] = QueryStringParser.queryStringToMap(queryString.getOrElse(""))

  def getParam(key: String): Option[String] = {
    queryParams.get(key)
  }

}
