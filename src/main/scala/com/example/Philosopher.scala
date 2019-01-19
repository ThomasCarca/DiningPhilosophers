package com.example

import akka.actor.Actor

class Philosopher(val MAX_LIFE: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int) extends Actor {

  var life: Int = MAX_LIFE
  var hunger: Int = 0


  override def receive: Receive = thinking

  def print(): Unit = {
    println(s"--- ${self.path.name} =>   life : $life/$MAX_LIFE   hunger : $hunger/$MAX_HUNGER")
  }

  def thinking: Receive = {
    case "tictac" =>
      hunger += 1
      sender() ! "I'm hungry !"
      print()
  }

  def eating: Receive = {
    case "done eating" =>
      context.become(thinking)
  }

  def hungry: Receive = {
    case "eat" =>
    context.become(eating)
  }

  override def unhandled(message: Any): Unit = sender() ! "Mmmh ?"
}
