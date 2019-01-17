package com.example

import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {
  val system: ActorSystem = ActorSystem("Dinner")

  val fork: ActorRef = system.actorOf(Props[Fork])

  val r = scala.util.Random

  val philosophers: List[ActorRef] = (1 to 5).map(_ => system.actorOf(Props(new Philosopher(r.nextInt(10)+5,r.nextInt(10)+5, r.nextInt(3)+1)))).toList


  (1 to 10).foreach{ _ => println("----------------------------");philosophers.foreach( philosopher =>  philosopher ! "tictac" ); Thread.sleep(2000)}

}
