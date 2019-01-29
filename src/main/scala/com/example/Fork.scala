package com.example

import akka.actor.Actor

class Fork extends Actor{
  override def receive: Receive = free


  def free : Receive = {
     case Message.TAKEN =>
      println(s"Je suis prise ${self.path.name}")
      context.become(taken)
     case _ => println(s"Je suis libre ${self.path.name}")
  }

  def taken : Receive = {
    case Message.RELEASED =>
      println(s"Je suis maintenant libre ${self.path.name}")
    case  _ => println(s"Je suis occupé ${self.path.name}")
  }


}
