package SEMP.RN_Stage

import chisel3._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}

class pipe_rensyu_test  extends FlatSpec with ChiselScalatestTester with Matchers {
  "pipe_rensyu_test" should "" in{

    test(new pipe_rensyu()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){ c=>

      c.clock.step(1)

      c.io.in.data.poke(1.U)


      c.clock.step(1)
      c.clock.step(1)

      c.io.in.data.poke(2.U)
      c.io.in.stall.poke(true.B)
      c.clock.step(1)

      c.io.in.data.poke(3.U)
      c.clock.step(1)

      c.clock.step(1)

      c.io.in.stall.poke(false.B)
      c.io.in.data.poke(4.U)
      c.clock.step(1)
      c.clock.step(1)

    }

  }
}
