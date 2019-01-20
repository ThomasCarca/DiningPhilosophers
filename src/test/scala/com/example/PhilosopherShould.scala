package com.example

import org.scalatest._
import akka.actor.{ActorSystem, UnhandledMessage}
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

  val MAX_THINKING = 8
  val MAX_HUNGER = 10
  val TIME_TO_EAT = 3

  system.eventStream.subscribe(testActor, classOf[UnhandledMessage])

  "A philosopher actor" when {

    "thinking" should {

      val philosopherRef = TestActorRef(new Philosopher(MAX_THINKING, MAX_HUNGER, TIME_TO_EAT))
      val philosopherActor = philosopherRef.underlyingActor

      "should not handle a 'eat' message" in {
        philosopherRef ! "eat"
        expectMsg(UnhandledMessage("eat", testActor, philosopherRef))
      }

      "should not handle a 'done eating' message" in {
        philosopherRef ! "done eating"
        expectMsg(UnhandledMessage("done eating", testActor, philosopherRef))
      }

      "should increase his thinking time by 1 when getting a 'tictac' message" in {
        philosopherActor.thinkingTime should be (0)
        philosopherRef ! "tictac"
        philosopherActor.thinkingTime should be (1)
      }

      "should increase his number of turns survived by 1 when getting a 'tictac' message" in {
        philosopherActor.numberOfTurnsSurvived should be (1)
        philosopherRef ! "tictac"
        philosopherActor.numberOfTurnsSurvived should be (2)
      }

    }

    "hungry" should {

      val philosopherRef = TestActorRef(new Philosopher(MAX_THINKING, MAX_HUNGER, TIME_TO_EAT))
      val philosopherActor = philosopherRef.underlyingActor

      // Trigger a change of state thinking -> hungry
      (1 to MAX_THINKING).foreach( _ => philosopherRef ! "tictac")

      "should not handle a 'hungry' message" in {
        philosopherRef ! "hungry"
        expectMsg(UnhandledMessage("hungry", testActor, philosopherRef))
      }

      "should not handle a 'done eating' message" in {
        philosopherRef ! "done eating"
        expectMsg(UnhandledMessage("done eating", testActor, philosopherRef))
      }

      "should increase his hunger time by 1 when getting a 'tictac' message" in {
        philosopherActor.hungerTime should be (0)
        philosopherRef ! "tictac"
        philosopherActor.hungerTime should be (1)
      }

      "should increase his number of turns survived by 1 when getting a 'tictac' message" in {
        philosopherActor.numberOfTurnsSurvived should be (9)
        philosopherRef ! "tictac"
        philosopherActor.numberOfTurnsSurvived should be (10)
      }

    }
  }
}
