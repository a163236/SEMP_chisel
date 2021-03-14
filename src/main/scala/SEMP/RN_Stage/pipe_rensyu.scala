package SEMP.RN_Stage

import chisel3._
import chisel3.util._

class pipe_in extends Bundle{
  val data = UInt(32.W)
  val stall = Bool()
}

class pipe_out extends Bundle{
  val data = UInt(32.W)
}



class pipe_rensyu extends Module{
  val io = IO(new Bundle() {
    val in = Input(new pipe_in)
    val out = Output(new pipe_out)
  })
  io := DontCare

  val regs = Reg(new pipe_in())

  when(io.in.stall){
    regs.data := regs.data
  }.otherwise{
    regs.data := io.in.data
  }

  io.out.data := regs.data


}
