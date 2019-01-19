package com.example

import org.scalatest._
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}

import scala.language.postfixOps

class PhilosopherShould(_system: ActorSystem)
  extends TestKit(_system)
    with WordSpecLike
    with ImplicitSender
    with Matchers
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("PhilosopherShould"))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A philosopher actor" when {

    val MAX_LIFE = 12
    val MAX_HUNGER = 10
    val TIME_TO_EAT = 3

    val philosopherRef = TestActorRef(new Philosopher(MAX_LIFE, MAX_HUNGER, TIME_TO_EAT))
    val philosopherActor = philosopherRef.underlyingActor

    "thinking" should {

      "not handle a 'eat' message" in {
        philosopherRef ! "eat"
        expectMsg("Mmmh ?")
      }

      "not handle a 'done eating' message" in {
        philosopherRef ! "done eating"
        expectMsg("Mmmh ?")
      }

      "be hungrier (of 1) when getting a 'tictac' message" in {
        philosopherActor.hunger should be (0)
        philosopherRef ! "tictac"
        philosopherActor.hunger should be (1)
      }

      "send back the expected message when getting a 'tictac' message" in {
        philosopherRef ! "tictac"
        expectMsg("I'm hungry !")
      }

    }




  }
  //#first-test
}
//#full-example
