package com.example

import akka.actor.Actor

class Philosopher extends Actor {
  override def receive: Receive = thinking

  def thinking: Receive = {
    case "hungry" =>
      println("I'm hungry !")
      Thread.sleep(1000)
      context.become(starving)
  }

  def eating: Receive = {

    case "done eating" =>
      println("I'm done eating !")
      Thread.sleep(1000)
      context.become(thinking)
  }

  def starving: Receive = {
    case "eat" =>
      println("Let's start eating !")
      Thread.sleep(1000)
    context.become(eating)
  }

  override def unhandled(message: Any): Unit = println(s"unhandled message : $message")
}
