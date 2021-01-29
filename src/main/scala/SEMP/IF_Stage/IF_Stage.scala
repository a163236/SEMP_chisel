package SEMP.IF_Stage

import SEMP.Memory.IMEM_IO
import chisel3._
import common._


class IF_Stage_IO extends Bundle {
  val imem = Flipped(new IMEM_IO)
  val stall = Input(Bool())
  val exception = Input(Bool())
}

class IF_Stage extends Module{
  val io = IO(new IF_Stage_IO)


  val PC = RegInit(START_ADDR.U)

  io.imem.req_addr := PC
  io.imem.req_valid := true.B

}