package SEMP.IF_Stage
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._
import chisel3._

class IF_Stage_test extends FlatSpec with ChiselScalatestTester with Matchers{
  "" should "" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new IF_Stage){c =>

      c.clock.step(1)
      c.clock.step(1)
      c.io.stall.poke(true.B)
      c.clock.step(1)
      c.io.stall.poke(false.B)
      for (i <- 0 to 10){
        c.clock.step(1)
      }

    }
  }
}
