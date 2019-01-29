package com.example

import akka.actor.{Actor, ActorRef, ActorSelection, PoisonPill}

class Philosopher(val MAX_THINKING: Int, val MAX_HUNGER: Int, val TIME_TO_EAT: Int, val fork : ActorRef) extends Actor {

  var thinkingTime: Int = 0
  var hungerTime: Int = 0
  var eatingTurn: Int = 0
  var numberOfTurnsSurvived: Int = 0
  var neighbours: List[String] = Nil
  var nicelyNeighbour : String = ""

  override def receive: Receive = thinking

  def print(): Unit = {
    println(s"--- ${self.path.name} =>   thinking time : $thinkingTime/$MAX_THINKING   hunger : $hungerTime/$MAX_HUNGER  fork : $fork" )
  }

  def thinking: Receive = {



    case Neighbour(name) =>
      neighbours = neighbours :+ name

    case Message.TICTAC =>
      thinkingTime += 1
      numberOfTurnsSurvived +=1
      print()
      if (thinkingTime == MAX_THINKING){
        thinkingTime = 0
        self ! Message.HUNGRY
      }

    case ASKED_BORROW(o) =>
      println(s"${sender.path.name} asked for the fork")
      sender ! Message.TAKE_IT
      fork ! Message.TAKEN

    case Message.HUNGRY =>
      println(s"${self.path.name} is now hungry !")
      context.become(hungry)

    case  Message.RELEASED =>
      fork ! Message.RELEASED

  }

  def eating: Receive = {

    case Message.TICTAC =>
      eatingTurn += 1
      numberOfTurnsSurvived +=1
      if (eatingTurn == TIME_TO_EAT) self ! Message.DONE_EATING
      print()

    case Message.DONE_EATING =>
      fork ! Message.RELEASED
      context.actorSelection("akka://Dinner/user/"+nicelyNeighbour) ! Message.RELEASED
      println(s"${self.path.name} is back to thinking !")
      hungerTime = 0
      nicelyNeighbour = ""
      context.become(thinking)

    case ASKED_BORROW(_) =>
      println("I am already eating and can't give you my fork sorry bro !")

    case Message.TAKE_IT =>
      sender() ! Message.RELEASED
      println(s"Thank you bro but you are to late ${self.path.name}")
  }

  def hungry: Receive = {

    case Message.TICTAC =>
      hungerTime += 1
      numberOfTurnsSurvived += 1

      neighbours.foreach(neighbour => context.actorSelection("akka://Dinner/user/"+neighbour) ! ASKED_BORROW(MAX_HUNGER-hungerTime))


      if (hungerTime == MAX_HUNGER) {
        println(s"${self.path.name} just died... He survived $numberOfTurnsSurvived turns.")
        self ! PoisonPill
      }
      print()

    case ASKED_BORROW(hungerUntilDeath) =>
      if(MAX_HUNGER - hungerTime <= hungerUntilDeath)  {
        sender() ! Message.TAKE_IT
        fork ! Message.TAKEN
      } else sender() ! Message.DONT_TAKE_IT

    case Message.TAKE_IT =>
      fork ! Message.TAKEN
      nicelyNeighbour = sender().path.name
      println(s"I TOOK a FORK ${self.path.name}")
      context.become(eating)


    case  Message.RELEASED =>
      fork ! Message.RELEASED
  }
}
