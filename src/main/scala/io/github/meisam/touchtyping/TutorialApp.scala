package io.github.meisam.touchtyping

import org.scalajs.dom
import org.scalajs.dom.{Event, document}
import org.scalajs.dom.html.{Div, Input}
import org.scalajs.dom.raw.{Element, HTMLDivElement, HTMLElement}
import scalatags.JsDom.all._

import scala.scalajs.js.annotation.JSExportTopLevel

object TutorialApp extends {

  private val EOF = 0.toChar

  private val box = textarea(
    `type` := "text",
    rows := 10,
    cols := 50,
    name := "inputbox",
    placeholder := "Type here!",
  ).render

  private val box2 = textarea(
    `type` := "text",
    rows := 10,
    cols := 50,
    name := "inputbox2",
    value := "The quick brown fox jumps over the lazy dog.",

  ).render

  private val wordsDiv: Div = div(name := "wordsDiv")().render

  private def remakeCharBoxes(): Unit = {
    val count = wordsDiv.childElementCount
      println(s"count = $count")
    (count - 1 to 0 by -1).foreach{ i =>
      val child = wordsDiv.children(i)
      println(s"removed $child")
      wordsDiv.removeChild(child)
      println(s"removed $child")
    }
    charBoxes(box2.value, box.value).foreach { child =>
      wordsDiv.appendChild(child.render)
    }
  }

  def main(args: Array[String]): Unit = {

    box.addEventListener("input", { e: dom.Event => checkTyping(e) })
    box2.addEventListener("input", { _: dom.Event => remakeCharBoxes() })
    val css = link(
      href := "touchtyping.css",
      `type` := "text/css",
      rel :=  "stylesheet"
    ).render

    remakeCharBoxes()
    document.head.appendChild(css)
    document.body.appendChild(box2)
    document.body.appendChild(hr().render)
    document.body.appendChild(wordsDiv)
    document.body.appendChild(hr().render)
    document.body.appendChild(box)

  }

  private def charBoxes(sentence: String, userInput: String) = {
    sentence.toCharArray.map { sentenceChar =>
        div(cls := "untyped")(sentenceChar.toString())
    }
  }

  @JSExportTopLevel("checkTyping")
  private def checkTyping(e: Event): Unit = {
    val charDivs = wordsDiv.children

    box2.value.zipAll(box.value, EOF, EOF).take(box2.value.length).zipWithIndex.foreach {
      case ((_, `EOF`), i) =>
        charDivs(i).setAttribute("class", "untyped")
      case ((`EOF`, _), i) =>
        charDivs(i).asInstanceOf[HTMLDivElement].setAttribute("class", "extra")
      case ((sentenceChar, inputChar), i) if sentenceChar != inputChar =>
        charDivs(i).asInstanceOf[HTMLDivElement].setAttribute("class", "incorrect")
      case ((_, _), i) =>
        charDivs(i).asInstanceOf[HTMLDivElement].setAttribute("class", "correct")
    }

    println(s"U: $e, ${e.`type`}, $box,${box.value + box.value} $wordsDiv")
  }
}
