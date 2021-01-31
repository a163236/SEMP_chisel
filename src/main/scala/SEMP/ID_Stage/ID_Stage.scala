package SEMP.ID_Stage

import SEMP.IF_Stage.IF_Pipeline_IO
import chisel3._

class ID_Stage_IO extends Bundle{
  val stall = Input(Bool())
  val flush = Input(Bool())
  val exception = Input(Bool())
  val if_pipeline = Flipped(new IF_Pipeline_IO())
  val id_pipeline = new ID_Pipeline_IO()
}

class ID_Stage extends Module{
  val io = IO(new ID_Stage_IO)

  val decoder = Module(new decoder)



}

class ID_Pipeline_IO extends Bundle {
  val
}