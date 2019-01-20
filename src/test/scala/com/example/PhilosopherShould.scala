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
        philosopherRef ! Message.EAT
        expectMsg(UnhandledMessage(Message.EAT, testActor, philosopherRef))
      }

      "should not handle a 'done eating' message" in {
        philosopherRef ! "done eating"
        expectMsg(UnhandledMessage("done eating", testActor, philosopherRef))
      }

      "should increase his thinking time by 1 when getting a 'tictac' message" in {
        philosopherActor.thinkingTime should be (0)
        philosopherRef ! Message.TICTAC
        philosopherActor.thinkingTime should be (1)
      }

      "should increase his number of turns survived by 1 when getting a 'tictac' message" in {
        philosopherActor.numberOfTurnsSurvived should be (1)
        philosopherRef ! Message.TICTAC
        philosopherActor.numberOfTurnsSurvived should be (2)
      }

    }

    Message.HUNGRY should {

      val philosopherRef = TestActorRef(new Philosopher(MAX_THINKING, MAX_HUNGER, TIME_TO_EAT))
      val philosopherActor = philosopherRef.underlyingActor

      // Trigger a change of state thinking -> hungry
      philosopherRef ! Message.HUNGRY

      "should not handle a 'hungry' message" in {
        philosopherRef ! Message.HUNGRY
        expectMsg(UnhandledMessage(Message.HUNGRY, testActor, philosopherRef))
      }

      "should not handle a 'done eating' message" in {
        philosopherRef ! "done eating"
        expectMsg(UnhandledMessage("done eating", testActor, philosopherRef))
      }

      "should increase his hunger time by 1 when getting a 'tictac' message" in {
        philosopherActor.hungerTime should be (0)
        philosopherRef ! Message.TICTAC
        philosopherActor.hungerTime should be (1)
      }

      "should increase his number of turns survived by 1 when getting a 'tictac' message" in {
        philosopherActor.numberOfTurnsSurvived should be (1)
        philosopherRef ! Message.TICTAC
        philosopherActor.numberOfTurnsSurvived should be (2)
      }

      "should die if his hunger reaches its maximum value" in {
        val probe = TestProbe()
        probe watch philosopherRef
        (3 to MAX_HUNGER).foreach( _ => philosopherRef ! Message.TICTAC)
        probe.expectTerminated(philosopherRef)
      }
    }

    "eating" should {

      val philosopherRef = TestActorRef(new Philosopher(MAX_THINKING, MAX_HUNGER, TIME_TO_EAT))
      val philosopherActor = philosopherRef.underlyingActor

      // Trigger a change of state thinking -> hungry
      philosopherRef ! Message.HUNGRY

      // Trigger a change of state hungry -> eating
      philosopherRef ! Message.EAT

      "should not handle a 'hungry' message" in {
        philosopherRef ! Message.HUNGRY
        expectMsg(UnhandledMessage(Message.HUNGRY, testActor, philosopherRef))
      }

      "should not handle a 'eat' message" in {
        philosopherRef ! Message.EAT
        expectMsg(UnhandledMessage(Message.EAT, testActor, philosopherRef))
      }

      "should increase his eating turn by 1 when getting a 'tictac' message" in {
        philosopherActor.eatingTurn should be (0)
        philosopherRef ! Message.TICTAC
        philosopherActor.eatingTurn should be (1)
      }

      "should increase his number of turns survived by 1 when getting a 'tictac' message" in {
        philosopherActor.numberOfTurnsSurvived should be (1)
        philosopherRef ! Message.TICTAC
        philosopherActor.numberOfTurnsSurvived should be (2)
      }

      "should be back to thinking when its eating turn reaches the necessary time to eat" in {
        // should trigger a change of state eating -> thinking
        (2 to TIME_TO_EAT).foreach( _ => philosopherRef ! Message.TICTAC)
        // Message should be unhandled because 'thinking' state does not allow 'done eating' messages
        philosopherRef ! "done eating"
        expectMsg(UnhandledMessage("done eating", testActor, philosopherRef))
      }
    }
  }
}
