package SEMP.IF_Stage

import SEMP.ID_Stage._
import SEMP.RS_Stage.fifo
import chisel3._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._

class fifo_test  extends FlatSpec with ChiselScalatestTester with Matchers {

  "fifo" should "" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new fifo()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.setTimeout(10000)     // タイムアウト 設定
      c.clock.step(1)

      c.io.in.poke(1.U)
      c.io.in_valid.poke(true.B)

      c.clock.step(1)

      c.io.in.poke(2.U)
      c.io.in_valid.poke(true.B)

      c.clock.step(1)

      c.io.in.poke(3.U)
      c.io.in_valid.poke(true.B)

      c.clock.step(1)

      c.io.in_valid.poke(false.B)
      c.io.out_valid.poke(true.B)

      for (i <- 1 to 10) {
        c.clock.step(1)
      }
    }
  }

}