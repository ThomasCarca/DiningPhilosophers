package com.example

import akka.actor.{Actor, PoisonPill}

class Philosopher(val MAX_THINKING: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int) extends Actor {


  var thinkingTime: Int = 0
  var hungerTime: Int = 0
  var eatingTurn: Int = 0
  var numberOfTurnsSurvived: Int = 0
  var neighbour: String = ""

  override def receive: Receive = thinking

  def print(): Unit = {
    println(s"--- ${self.path.name} =>   thinking time : $thinkingTime/$MAX_THINKING   hunger : $hungerTime/$MAX_HUNGER neighbour : $neighbour")
  }

  def thinking: Receive = {
    case Neighbour(name) =>
      neighbour = name
    case Message.TICTAC =>
      thinkingTime += 1
      numberOfTurnsSurvived +=1
      print()
      if (thinkingTime == MAX_THINKING){
        thinkingTime = 0
        self ! Message.HUNGRY
      }
    case Message.BORROW =>
      println(s"${sender.path.name} asked for the fork")
      sender ! Message.TAKE_IT
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
      hungerTime = 0
      context.become(thinking)
  }

  def hungry: Receive = {
    case Message.TICTAC =>
      hungerTime += 1
      numberOfTurnsSurvived += 1
      context.actorSelection("akka://Dinner/user/"+neighbour) ! Message.BORROW
/*      if (hungerTime == MAX_HUNGER) {
        println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
        self ! PoisonPill
      }*/
      print()
    case Message.TAKE_IT =>
      self ! Message.EAT
    case Message.EAT =>
    println(s"${self.path.name} is now eating !")
    context.become(eating)
  }
}
