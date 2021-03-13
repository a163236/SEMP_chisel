package SEMP.RN_Stage

import chisel3._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._


class freequeue_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "freequeue" should "" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new freequeue()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.setTimeout(10000)     // タイムアウト 設定

      c.io.enq1.poke(1.U)
      c.io.enq1_valid.poke(true.B)
      c.clock.step(conf.xpregnum/2)

      c.io.enq1_valid.poke(false.B)
      c.io.deq2_ready.poke(true.B)
      c.clock.step(conf.xpregnum/2)

      c.clock.step(1)


      c.clock.step(1)



    }
  }
}