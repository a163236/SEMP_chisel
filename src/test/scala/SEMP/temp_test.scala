package SEMP

import SEMP.ID_Stage._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._

class temp_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "temp" should "" in{
    implicit val conf = SEMPconfig(simulation = true)
    test(new temp()).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)){c=>
      c.clock.setTimeout(10000)     // タイムアウト 設定
      c.clock.step(1)

      for (i <- 1 to 1000) {
        c.clock.step(1)
      }
    }
  }
}
