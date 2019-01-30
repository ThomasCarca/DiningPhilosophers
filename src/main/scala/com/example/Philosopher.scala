package com.example

import akka.actor.{Actor, PoisonPill}
import com.example.messages.{DoneEating, Eat, Hungry, NewTurn}

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
    case NewTurn() =>
      thinkingTime += 1
      numberOfTurnsSurvived +=1
      if (thinkingTime == MAX_THINKING) self ! Hungry()
      print()
    case Hungry() =>
      println(s"${self.path.name} is now hungry !")
      context.become(hungry)
  }

  def eating: Receive = {
    case NewTurn() =>
    eatingTurn += 1
    numberOfTurnsSurvived +=1
    if (eatingTurn == TIME_TO_EAT) self ! DoneEating()
    print()
    case DoneEating() =>
      println(s"${self.path.name} is back to thinking !")
      context.become(thinking)
  }

  def hungry: Receive = {
    case NewTurn() =>
    hungerTime += 1
    numberOfTurnsSurvived += 1
    if (hungerTime == MAX_HUNGER) {
      println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
      self ! PoisonPill
    }
    print()
    case Eat() =>
    println(s"${self.path.name} is now eating !")
    context.become(eating)
  }
}
