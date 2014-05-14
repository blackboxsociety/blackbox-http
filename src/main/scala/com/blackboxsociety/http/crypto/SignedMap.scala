package com.blackboxsociety.http.crypto

import com.blackboxsociety.json._
import scala.collection.immutable._

object SignedMap {

  def sign(secret: String, map: Map[String, String]): String = {
    Signed.sign(secret, normalize(map))
  }

  def verify(secret: String, signed: String): Option[Map[String, String]] = for (
    json     <- JsonParser.parse(signed.drop(64)).toOption;
    map      <- json.as[Map[String, String]];
    verified <- verifySignature(secret, signed.take(64), map)
  ) yield verified

  private def verifySignature(secret: String,
                              signature: String,
                              map: Map[String, String]): Option[Map[String, String]] =
  {
    if (sign(secret, map) == signature){
      Some(map)
    } else{
      None
    }
  }

  private def normalize(map: Map[String, String]): String = {
    val sorted = SortedSet.empty[String] ++ map.keys
    sorted.foldLeft("") { (m, n) => s"$m:$n:${map.get(n).get}" }
  }

}
