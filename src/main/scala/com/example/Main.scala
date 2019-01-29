package com.example

import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {

  def thinkingTime: Int = scala.util.Random.nextInt(10) + 5

  def hungerTime: Int = scala.util.Random.nextInt(10) + 5

  def timeToEat: Int = scala.util.Random.nextInt(3) + 2

  def fork: ActorRef = system.actorOf(Props[Fork])

  def philosopherProp: Props = Props(new Philosopher(thinkingTime, hungerTime, timeToEat ,fork))

  val system: ActorSystem = ActorSystem("Dinner")

  val philosophers: List[ActorRef] = (0 to 4).map(_ => system.actorOf(philosopherProp)).toList


  // TODO Refactor
  philosophers.sliding(3)
    .foreach(tripletOfPhilosopher => {
      tripletOfPhilosopher(1) ! Neighbour(tripletOfPhilosopher.last.path.name)
      tripletOfPhilosopher(1) ! Neighbour(tripletOfPhilosopher.head.path.name)
    })

  philosophers(0) ! Neighbour(philosophers(1).path.name)
  philosophers(0) ! Neighbour(philosophers.last.path.name)

  philosophers.last ! Neighbour(philosophers.head.path.name)
  philosophers.last ! Neighbour(philosophers(4).path.name)


  while (true) {

    philosophers.foreach(philosopher => philosopher ! "tictac")
    Thread.sleep(1000)
    println("--------------------------")
  }
}
