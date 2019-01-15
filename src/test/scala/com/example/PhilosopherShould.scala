package com.example

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}

import scala.concurrent.duration._
import scala.language.postfixOps

class PhilosopherShould(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll {
  //#test-classes

  def this() = this(ActorSystem("PhilosopherShould"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A philosopher actor" should {
    val philosopherTest = system.actorOf(Props[Philosopher], "philosopherTest")
    "pass on a greeting message when instructed to" in {
      philosopherTest ! "hungry"
      expectMsg("fail")
    }
  }
  //#first-test
}
//#full-example
