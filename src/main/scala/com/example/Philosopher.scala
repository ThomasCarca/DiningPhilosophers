package com.example

import akka.actor.{Actor, PoisonPill}
import com.example.messages. NewTurn

// TODO : Add left/right fork and neighbour as class parameters
// TODO : Handle questions to neighbours in the simplest and naive way.

class Philosopher(val MAX_THINKING: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int) extends Actor {

  var thinkingTime: Int = 0
  var hungerTime: Int = 0
  var eatingTurn: Int = 0
  var numberOfTurnsSurvived: Int = 0

  override def receive: Receive = state(thinking)

  def print(): Unit = {
    println(s"--- ${self.path.name} =>   thinking time : $thinkingTime/$MAX_THINKING   hunger : $hungerTime/$MAX_HUNGER")
  }

  def state(action: () => Unit): Receive = {
    case NewTurn() =>
      numberOfTurnsSurvived += 1
      action()
      print()
  }

  def thinking(): Unit = {
      thinkingTime += 1
      if (thinkingTime == MAX_THINKING) {
        println(s"${self.path.name} is now hungry !")
        thinkingTime = 0
        context.become(state(hungry))
      }
  }

  def eating(): Unit = {
    eatingTurn += 1
    if (eatingTurn == TIME_TO_EAT) {
      println(s"${self.path.name} is back to thinking !")
      eatingTurn = 0
      context.become(state(thinking))
    }
  }

  def hungry(): Unit = {
    hungerTime += 1
    if (hungerTime == MAX_HUNGER) {
      println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
      self ! PoisonPill
    }
  }
}
