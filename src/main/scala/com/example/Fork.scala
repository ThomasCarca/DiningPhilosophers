package com.example

import akka.actor.Actor

class Fork extends Actor{
  override def receive: Receive = free


  def free : Receive = {
    case _ => println("je suis une fourchette.")
    context.become(taken)
  }

  def taken : Receive = {
    case _ => println("je suis une fourchette sale.")
  }


}
