package SEMP.RN_Stage

import chisel3._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._

class RN_Stage_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "RN_Stage" should "" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new RN_Stage()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.setTimeout(10000)     // タイムアウト 設定


      c.clock.step(conf.xpregnum/2)

      c.clock.step(1)

      c.io.id_pipeline.inst1.rd.poke(1.U)
      c.io.id_pipeline.inst1.rs1.poke(2.U)
      c.io.id_pipeline.inst1.rs2.poke(3.U)
      c.clock.step(1)

      c.io.id_pipeline.inst1.rd.poke(7.U)
      c.io.id_pipeline.inst1.rs1.poke(8.U)
      c.io.id_pipeline.inst1.rs2.poke(9.U)
      c.clock.step(1)


      c.io.stall_in.poke(true.B)
      c.clock.step(1)

      for (i <- 0 until 20) {
        c.clock.step(1)
      }

    }
  }
}
