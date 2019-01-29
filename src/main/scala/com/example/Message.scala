package com.example

case class Neighbour(name : String)
case class YourForkIs(name : String)
case class ASKED_BORROW(hungerUntilDeath : Int)

object Message {
  val TICTAC: String = "tictac"
  val HUNGRY: String = "hungry"
  val DONE_EATING: String = "done eating"
  val EAT: String = "eat"
  val ASKED_BORROW : String = "asked_borrow"
  val TAKE_IT : String ="take it"
  val DONT_TAKE_IT : String =" don't take it"
  val TAKEN : String = "take"
  val RELEASED : String = "release"
}
