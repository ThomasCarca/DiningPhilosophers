package com.example

import akka.actor.Actor

class Philosopher(val MAX_LIFE: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int) extends Actor {

  var life = MAX_LIFE
  var hunger = MAX_HUNGER


  override def receive: Receive = thinking

  def thinking: Receive = {
    case "tictac" =>
      val e : String = self.path.name
      hunger = hunger-1
      println(s"$e => hunger : $hunger ")

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
