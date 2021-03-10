package SEMP

import SEMP.IF_Stage._
import SEMP.ID_Stage._
import SEMP.Memory.IMEM
import chisel3._
import common._

class temp(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new Bundle() {
    val if_pipeline = new IF_Pipeline_IO()
    val id_pipeline = new ID_Pipeline_IO()
  })

  val IF_Stage = Module(new IF_Stage)
  IF_Stage.io := DontCare

  val IMEM = Module(new IMEM)
  val ID_Stage = Module(new ID_Stage)
  val PhyRegFile = Reg(Vec(conf.xpregnum, UInt(conf.xlen.W)))
  ID_Stage.io := DontCare

  // memory <-> IF
  IMEM.io <> IF_Stage.io.imem
  IF_Stage.io.pipeline <> ID_Stage.io.if_pipeline




  // 出力
  io.if_pipeline <> IF_Stage.io.pipeline
  io.id_pipeline <> ID_Stage.io.id_pipeline

}

