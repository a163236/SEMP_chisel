package SEMP.Memory

import common._

import chisel3._
import chiseltest._
import org.scalatest._

import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}


class IMEM_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "IMEM_test" should "" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new IMEM()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.setTimeout(10000)     // タイムアウト 設定
      c.io.req_addr.poke(0.U)
      c.io.req_valid.poke(true.B)
      c.clock.step(1)
      println(c.io.resp_data.peek())
      c.clock.step(1)

      for (i <- 1 to 1000) {
        c.clock.step(1)
      }

    }
  }
}
