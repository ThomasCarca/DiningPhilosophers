package com.example

import org.scalatest._
import akka.actor.{ActorSystem, UnhandledMessage}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.example.messages.NewTurn

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

      "should have his thinking time equal to 0" in {
        philosopherActor.thinkingTime should be (0)
      }

      "should increase his thinking time by 1 when getting a NewTurn() message" in {
        philosopherRef ! NewTurn()
        philosopherActor.thinkingTime should be (1)
      }

      "should have his hunger time equal to 0" in {
        philosopherActor.hungerTime should be (0)
      }

      "should have the same hunger time when getting a NewTurn() message" in {
        philosopherRef ! NewTurn()
        philosopherActor.hungerTime should be (0)
      }

      "should have his eating turn equal to 0" in {
        philosopherActor.eatingTurn should be (0)
      }

      "should have the same eating turn when getting a NewTurn() message" in {
        philosopherRef ! NewTurn()
        philosopherActor.eatingTurn should be (0)
      }

      "should increase his number of turns survived by 1 when getting a NewTurn() message" in {
        philosopherActor.numberOfTurnsSurvived should be (3)
        philosopherRef ! NewTurn()
        philosopherActor.numberOfTurnsSurvived should be (4)
      }

    }

    "hungry" should {

      val philosopherRef = TestActorRef(new Philosopher(MAX_THINKING, MAX_HUNGER, TIME_TO_EAT))
      val philosopherActor = philosopherRef.underlyingActor

      // Trigger a change of state thinking -> hungry
      (1 to MAX_THINKING).foreach( _ => philosopherRef ! NewTurn())

      "should have his hunger time equal to 0" in {
        philosopherActor.hungerTime should be (0)
      }

      "should increase his hunger time by 1 when getting a NewTurn() message" in {
        philosopherRef ! NewTurn()
        philosopherActor.hungerTime should be (1)
      }

      "should have his thinking time equal to 0" in {
        philosopherActor.thinkingTime should be (0)
      }

      "should have the same thinking time when getting a NewTurn() message" in {
        philosopherRef ! NewTurn()
        philosopherActor.thinkingTime should be (0)
      }

      "should have his eating turn equal to 0" in {
        philosopherActor.eatingTurn should be (0)
      }

      "should have the same eating turn when getting a NewTurn() message" in {
        philosopherRef ! NewTurn()
        philosopherActor.eatingTurn should be (0)
      }

      "should increase his number of turns survived by 1 when getting a NewTurn() message" in {
        philosopherActor.numberOfTurnsSurvived should be (11)
        philosopherRef ! NewTurn()
        philosopherActor.numberOfTurnsSurvived should be (12)
      }

      "should die if his hunger reaches its maximum value" in {
        val probe = TestProbe()
        probe watch philosopherRef
        (4 to MAX_HUNGER).foreach( _ => philosopherRef ! NewTurn())
        probe.expectTerminated(philosopherRef)
      }
    }
  }
}
