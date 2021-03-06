package sys

import retier._
import retier.architectures.MultiClientServer._
import retier.rescalaTransmitter._
import retier.serializable.upickle._
import retier.tcp._

import java.awt.Color

import rescala._

@multitier
object Go {
  trait Server extends ServerPeer[Client]
  trait Client extends ClientPeer[Server] with FrontEndHolder

  var currentPlayer = 0

  val clientMouseX = placed[Client] {
    implicit! =>
    Signal {
      peer.mousePosition().x
    }
  }

  val clientMouseY = placed[Client] {
    implicit! =>
    Signal {
      peer.mousePosition().y
    }
  }

  val clientClick = placed[Client] {
    implicit! =>
    Signal {
      peer.mouseClicked()
    }
  }

  val players = placed[Server].local { implicit! =>
    Signal {
      remote[Client].connected() match {
        case black :: white :: _ => Seq(Some(black), Some(white))
        case _ => Seq(None, None)
      }
    }
  }

  val isPlaying = placed[Server].local {
    implicit! =>
    Signal {
      remote[Client].connected().size >= 2
    }
  }

  val stone = placed[Server] {
    implicit! =>
    tick.fold(initStone) { (stone, _) =>
      if (isPlaying.now) {
        val x = Signal {
          players() map { _ map {
            client => (clientMouseX from client).asLocal()
          } getOrElse 0 }
        }

        val y = Signal {
          players() map { _ map {
            client => (clientMouseY from client).asLocal()
          } getOrElse 0 }
        }

        val clicked = Signal {
          players() map { _ map {
            client => (clientClick from client).asLocal()
          } getOrElse false }
        }

        Signal { println("print player pos x: " + x()(currentPlayer)) }
        Signal { println("print player pos y: " + y()(currentPlayer)) }
        println("-----------------------------")

        //Initial If to see if the y signal is less than the Grid start at offSet (50 atm), then null is returned
        val result = Signal { if ( y()(currentPlayer) <= 49) {
          println("y value of click is below 50")
          //is catched in Window.scala => stone must not be null
          null
        } else if (clicked()(currentPlayer)) {
          println("clicked by currentplayer (" + currentPlayer + ")")
        /*  for(x <- 0 to gridCount-1 ) {
            for (y <- 0 to gridCount-1) {
              if (this.playerFields(x)(y).contains(Point(newStone.x, newStone.y))) {
                //recommended in scala to create a new object each time
                currentStone = new Stone(this.playerFields(x)(y).x, this.playerFields(x)(y).y, currentPlayer)
                this.playerFields(x)(y) = new Stone(this.playerFields(x)(y).x, this.playerFields(x)(y).y, currentPlayer)
              }
            }
          }
          */
          val tmp = new Stone(x()(currentPlayer),y()(currentPlayer), currentPlayer + 1)

          //set nextPlayer
          currentPlayer = (currentPlayer + 1) % 2

          tmp
        } else {
          println("Currentplayer(" + currentPlayer + ") didnt click")
          //is catched in Window.scala => stone must not be null
          null
        }
      }
      /*TODO:
      catchen oder empty Signal wegbekommen
      */
      result.now
    } else {
      stone
    }
  }
}
/*
val blackPlayerPoints = placed[Server].local {
  implicit! =>
  var points = 0
  for(x <- 0 to gridCount-1 ) {
    for (y <- 0 to gridCount-1) {
      if ((this.playerFields(x)(y).occupied) && this.playerFields(x)(y).c == 1) {
        points = points + 1
      }
    }
  }
  Signal {
    points
  }
}

val whitePlayerPoints = placed[Server].local {
  implicit! =>
  var points = 0
  for(x <- 0 to gridCount-1 ) {
    for (y <- 0 to gridCount-1) {
      if ((this.playerFields(x)(y).occupied) && this.playerFields(x)(y).c == 2) {
        points = points + 1
      }
    }
  }
  Signal {
    points
  }
}

*/
val score = placed[Server] {
  implicit! =>

  Signal {
    0 + " : " + 0
  }
}

val frontEnd = placed[Client].local {
  implicit! =>
  peer.createFrontEnd(score.asLocal, stone.asLocal)
}

tickStart

}

object GoServer extends App {
  retier.multitier setup new Go.Server{
    def connect = TCP(1099)
  }
}

object GoClient extends App {
  retier.multitier setup new Go.Client with UI.FrontEnd {
    def connect = TCP("localhost", 1099)
  }
}
