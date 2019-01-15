package com.example

import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {
  val system: ActorSystem = ActorSystem("Dinner")
  val philosopher1: ActorRef = system.actorOf(Props[Philosopher], "philosopher1")

  // hungry
  philosopher1 ! "hungry"

  // eating
  philosopher1 ! "eat"

  // thinking
  philosopher1 ! "done eating"

}
