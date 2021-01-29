package SEMP.Memory

import chiseltest._
import org.scalatest._

import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}

class IMEM_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "IMEM_test" should "" in{
    test(new IMEM()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.step(1)
    }
  }
}
