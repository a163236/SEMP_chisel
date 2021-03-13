package SEMP.RN_Stage
import SEMP.RS_Stage._
import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._

class Queue_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "freelist" should "" in {
    test(new Queue(gen=UInt(32.W), entries=6, pipe=false, flow=false)).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
      c.clock.setTimeout(10000) // タイムアウト 設定

      c.io.deq.ready.poke(false.B)

      c.io.enq.valid.poke(true.B)
      c.io.enq.bits.poke(1.U)
      c.clock.step(1)

      c.io.enq.valid.poke(true.B)
      c.io.enq.bits.poke(2.U)
      c.clock.step(1)

      c.io.enq.valid.poke(true.B)
      c.io.enq.bits.poke(3.U)
      c.clock.step(1)

      c.io.enq.valid.poke(false.B)
      c.io.deq.ready.poke(true.B)
      c.io.deq.ready.poke(true.B)
      c.clock.step(1)


      c.io.deq.ready.poke(false.B)
      c.clock.step(1)


    }
  }
}
