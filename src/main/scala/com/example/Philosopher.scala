package com.example

import akka.actor.{Actor, PoisonPill}

class Philosopher(val MAX_THINKING: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int) extends Actor {

  var thinkingTime: Int = 0
  var hungerTime: Int = 0
  var eatingTurn: Int = 0
  var numberOfTurnsSurvived: Int = 0

  override def receive: Receive = thinking

  def print(): Unit = {
    println(s"--- ${self.path.name} =>   thinking time : $thinkingTime/$MAX_THINKING   hunger : $hungerTime/$MAX_HUNGER")
  }

  def thinking: Receive = {
    case "tictac" =>
      thinkingTime += 1
      numberOfTurnsSurvived +=1
      if (thinkingTime == MAX_THINKING) self ! "hungry"
      print()
    case "hungry" =>
      println(s"${self.path.name} is now hungry !")
      context.become(hungry)
  }

  def eating: Receive = {
    case "tictac" =>
    eatingTurn += 1
    numberOfTurnsSurvived +=1
    if (eatingTurn == TIME_TO_EAT) self ! "done eating"
    print()
    case "done eating" =>
      println(s"${self.path.name} is back to thinking !")
      context.become(thinking)
  }

  def hungry: Receive = {
    case "tictac" =>
    hungerTime += 1
    numberOfTurnsSurvived += 1
    if (hungerTime == MAX_HUNGER) {
      println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
      self ! PoisonPill
    }
    print()
    case "eat" =>
    println(s"${self.path.name} is now eating !")
    context.become(eating)
  }
}
