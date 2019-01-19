package com.example

import akka.actor.{Actor, PoisonPill}

class Philosopher(val MAX_LIFE: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int) extends Actor {

  var life: Int = MAX_LIFE
  var hunger: Int = 0
  var eatingTurn: Int = 0
  var numberOfTurnsSurvived: Int = 0

  override def receive: Receive = thinking

  def print(): Unit = {
    println(s"--- ${self.path.name} =>   life : $life/$MAX_LIFE   hunger : $hunger/$MAX_HUNGER")
  }

  def thinking: Receive = {
    case "tictac" =>
      hunger += 1
      numberOfTurnsSurvived +=1
      if (hunger == MAX_HUNGER) self ! "hungry"
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
    life -= 1
    numberOfTurnsSurvived += 1
    if (life == 0) {
      println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
      self ! PoisonPill
    }
    print()
    case "eat" =>
    println(s"${self.path.name} is now eating !")
    context.become(eating)
  }
}
