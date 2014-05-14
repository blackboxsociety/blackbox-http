package com.blackboxsociety.http.crypto

import com.blackboxsociety.waterhouse._

object Signed {

  def sign(secret: String, text: String): String =
    HMAC.stringDigest[HMAC.Sha256.type](HMAC.Secret(secret), "")

  def verify(secret: String, signed: String): Option[String] = {
    val text      = signed.drop(64)
    val hash      = HMAC.stringDigest[HMAC.Sha256.type](HMAC.Secret(secret), text)
    val signature = signed.take(64)

    if (hash == signature)
      Some(text)
    else
      None
  }

}