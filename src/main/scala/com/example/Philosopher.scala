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

  private val TICTAC: String = "tictac"
  private val HUNGRY: String = "hungry"
  private val DONE_EATING: String = "done eating"
  private val EAT: String = "eat"

  def thinking: Receive = {
    case TICTAC =>
      thinkingTime += 1
      numberOfTurnsSurvived +=1
      if (thinkingTime == MAX_THINKING) self ! HUNGRY
      print()
    case HUNGRY =>
      println(s"${self.path.name} is now hungry !")
      context.become(hungry)
  }
  
  def eating: Receive = {
    case TICTAC =>
    eatingTurn += 1
    numberOfTurnsSurvived +=1
    if (eatingTurn == TIME_TO_EAT) self ! DONE_EATING
    print()
    case DONE_EATING =>
      println(s"${self.path.name} is back to thinking !")
      context.become(thinking)
  }

  def hungry: Receive = {
    case TICTAC =>
    hungerTime += 1
    numberOfTurnsSurvived += 1
    if (hungerTime == MAX_HUNGER) {
      println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
      self ! PoisonPill
    }
    print()
    case EAT =>
    println(s"${self.path.name} is now eating !")
    context.become(eating)
  }
}
