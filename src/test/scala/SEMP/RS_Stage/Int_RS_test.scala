package SEMP.RS_Stage

import chisel3._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._

class Int_RS_test extends FlatSpec with ChiselScalatestTester with Matchers {

  "Int_RS" should "test1" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new Int_RS()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.setTimeout(10000)     // タイムアウト 設定


      c.clock.step(1)
      c.clock.step(1)

    }

  }
}
