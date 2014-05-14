package com.blackboxsociety.http

object Cookie {

  def serialize(data: Map[String, String]): String =
    data.map { case (k, v) => s"$k=$v"  } mkString "; "

  def parse(str: String): Map[String, String] = {
    val pairs = str.split("; ")
    val keys  = pairs.map(_.split("="))
    keys.foldLeft[Map[String, String]](Map()) { (m, n) =>
      if (n.length > 1) {
        m + (n.head -> n.drop(1).head)
      } else {
        m
      }
    }
  }

}