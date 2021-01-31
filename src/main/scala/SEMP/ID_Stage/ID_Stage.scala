package SEMP.ID_Stage

import SEMP.IF_Stage._
import chisel3._
import common._

class ID_Stage_IO(implicit val conf: SEMPconfig) extends Bundle{
  val stall = Input(Bool())
  val flush = Input(Bool())
  val baka = if(conf.simulation==true) Input(Bool()) else null
  val exception = Input(Bool())
  val if_pipeline = Flipped(new IF_Pipeline_IO())
  val id_pipeline = new ID_Pipeline_IO()
}

class ID_Stage(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new ID_Stage_IO)

  val decoder1 = Module(new decoder())
  val decoder2 = Module(new decoder())

  decoder1.io.inst := io.if_pipeline.inst(INST_WIDTH-1, 0)
  decoder2.io.inst := io.if_pipeline.inst(INST_WIDTH*2-1, INST_WIDTH)
  io.id_pipeline.csignals := decoder1.io.csignals

}

class ID_Pipeline_IO extends Bundle {
  val csignals = Output(UInt(BR_Type_WIDTH.W))
}