package com.blackboxsociety.mvc.form

import com.blackboxsociety.util.parser.{BodyParser, QueryStringParser}
import com.blackboxsociety.http.{HttpResource, HttpRequest}
import scalaz.concurrent.Task

case class Form(fields: Seq[FormField]) extends BodyParser[Form] with QueryStringParser[Form] {

  def fromQueryString(request: HttpRequest): Form = {
    this.copy(fields = fields.map(f => f.copy(value = request.resource.getParam(f.key))))
  }

  def fromBody(request: HttpRequest): Task[Form] = {
    request.getBody().map { s =>
      val m = QueryStringParser.queryStringToMap(s)
      this.copy(fields = fields.map(f => f.copy(value = m.get(f.key))))
    }
  }

  def apply(key: String): Option[String] = {
    fields.find(_.key == key).flatMap(_.value)
  }

  def hasErrors(): Boolean = {
    errors.length > 0
  }

  def errors(): Seq[FormError] = {
    fields.map(f => FormError(f, f.error.getOrElse(""))).filter(_.field.hasError())
  }

  def validate[A](success: Form => A, failure: Form => A): A = {
    val check = fields.map(_.test())

    val hasErrors = check.foldLeft(false)((b, f) => b || f.hasError)
    if(!hasErrors) {
      success(Form(check))
    } else {
      failure(Form(check))
    }
  }

}

case class FormField(key: String, value: Option[String] = None, constraint: FormConstraint, error: Option[String] = None) {

  def hasError(): Boolean = !error.isEmpty

  def test(): FormField = {
    value.map { v =>
      this.copy(error = constraint.run(v).find(_._2 != None).flatMap(_._2))
    }.getOrElse(this.copy(error = Some(s"$key undefined")))
  }

}

object Form {

  def form(fields: (String, FormConstraint)*): Form = {
    Form(fields.map(f => FormField(f._1, None, f._2)))
  }

}
