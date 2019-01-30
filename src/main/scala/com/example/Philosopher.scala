package com.example

import akka.actor.{Actor, PoisonPill}

// TODO : Add left/right fork and neighbour as class parameters
// TODO : Replace HUNGRY, DONE_EATING, EAT messages by internal context change
// TODO : Handle questions to neighbours in the simplest and naive way.

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
    case Message.TICTAC =>
      thinkingTime += 1
      numberOfTurnsSurvived +=1
      if (thinkingTime == MAX_THINKING) self ! Message.HUNGRY
      print()
    case Message.HUNGRY =>
      println(s"${self.path.name} is now hungry !")
      context.become(hungry)
  }

  def eating: Receive = {
    case Message.TICTAC =>
    eatingTurn += 1
    numberOfTurnsSurvived +=1
    if (eatingTurn == TIME_TO_EAT) self ! Message.DONE_EATING
    print()
    case Message.DONE_EATING =>
      println(s"${self.path.name} is back to thinking !")
      context.become(thinking)
  }

  def hungry: Receive = {
    case Message.TICTAC =>
    hungerTime += 1
    numberOfTurnsSurvived += 1
    if (hungerTime == MAX_HUNGER) {
      println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
      self ! PoisonPill
    }
    print()
    case Message.EAT =>
    println(s"${self.path.name} is now eating !")
    context.become(eating)
  }
}
