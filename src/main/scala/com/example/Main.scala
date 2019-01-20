package com.example

import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {

  def thinkingTime: Int = scala.util.Random.nextInt(10) + 5

  def hungerTime: Int = scala.util.Random.nextInt(10) + 5

  def timeToEat: Int = scala.util.Random.nextInt(3) + 2

  def philosopherProp: Props = Props(new Philosopher(thinkingTime, hungerTime, timeToEat))

  val system: ActorSystem = ActorSystem("Dinner")

  val fork: ActorRef = system.actorOf(Props[Fork])

  val philosophers: List[ActorRef] = (1 to 5).map(_ => system.actorOf(philosopherProp)).toList

  while (true) {
    philosophers.foreach(philosopher => philosopher ! "tictac")
    Thread.sleep(500)
  }
}
