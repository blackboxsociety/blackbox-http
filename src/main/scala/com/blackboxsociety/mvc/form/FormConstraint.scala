package com.blackboxsociety.mvc.form

case class FormConstraint(constraints: List[(String => Boolean, String)] = List()) {

  def run(value: String): List[(Boolean, Option[String])] = {
    constraints.map(c => {
      val t = c._1(value)
      if(t) {
        (t, None)
      } else {
        (t, Some(c._2))
      }
    })
  }

  def restrict(test: String => Boolean, error: String): FormConstraint = {
    this.copy(constraints.::(test -> error))
  }

}
