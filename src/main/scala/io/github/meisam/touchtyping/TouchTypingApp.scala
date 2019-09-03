package io.github.meisam.touchtyping

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLDivElement
import scalatags.JsDom.all._

import scala.scalajs.js.annotation.JSExportTopLevel

object TouchTypingApp {

  private val EOF = 0.toChar

  private val practiceArea = textarea(
    `type` := "text",
    rows := 10,
    cols := 50,
    name := "inputbox",
    placeholder := "Type here!",
  ).render

  private val practiceSource = textarea(
    `type` := "text",
    rows := 10,
    cols := 50,
    name := "inputbox2",
    value := "The quick brown fox jumps over the lazy dog.",

  ).render

  private val wordsDiv: Div = div(name := "wordsDiv")().render

  private def remakeCharBoxes(): Unit = {
    practiceArea.value = ""
    val count = wordsDiv.childElementCount
    (count - 1 to 0 by -1).foreach { i =>
      val child = wordsDiv.children(i)
      wordsDiv.removeChild(child)
    }
    charBoxes(practiceSource.value, practiceArea.value).foreach { child =>
      wordsDiv.appendChild(child.render)
    }
  }

  def main(args: Array[String]): Unit = {

    practiceArea.addEventListener("input", { _: dom.Event => checkTyping() })
    practiceSource.addEventListener("input", { _: dom.Event => remakeCharBoxes() })
    val css = link(
      href := "touch-typing.css",
      `type` := "text/css",
      rel := "stylesheet"
    ).render

    remakeCharBoxes()
    document.head.appendChild(css)
    document.body.appendChild(practiceSource)
    document.body.appendChild(hr().render)
    document.body.appendChild(wordsDiv)
    document.body.appendChild(hr().render)
    document.body.appendChild(practiceArea)

  }

  private def charBoxes(sentence: String, userInput: String) = {
    sentence.toCharArray.map { sentenceChar =>
      div(cls := "untyped")(sentenceChar.toString())
    }
  }

  @JSExportTopLevel("checkTyping")
  protected def checkTyping(): Unit = {
    val charDivs = wordsDiv.children

    practiceSource.value.zipAll(practiceArea.value, EOF, EOF).take(practiceSource.value.length).zipWithIndex.foreach {
      case ((_, `EOF`), i) =>
        charDivs(i).setAttribute("class", "untyped")
      case ((`EOF`, _), i) =>
        charDivs(i).asInstanceOf[HTMLDivElement].setAttribute("class", "extra")
      case ((sentenceChar, inputChar), i) if sentenceChar != inputChar =>
        charDivs(i).asInstanceOf[HTMLDivElement].setAttribute("class", "incorrect")
      case ((_, _), i) =>
        charDivs(i).asInstanceOf[HTMLDivElement].setAttribute("class", "correct")
    }
  }
}
