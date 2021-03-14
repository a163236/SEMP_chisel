package SEMP

import SEMP.IF_Stage._
import SEMP.ID_Stage._
import SEMP.Memory._
import SEMP.RN_Stage._
import chisel3._
import common._

class temp(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new Bundle() {
    val if_pipeline = new IF_Pipeline_IO()
    val id_pipeline = new ID_Pipeline_IO()
    val rn_pipeline = new RN_Pipeline()
  })

  val IF_Stage = Module(new IF_Stage)
  IF_Stage.io := DontCare

  val IMEM = Module(new IMEM)
  val ID_Stage = Module(new ID_Stage)
  val RN_Stage = Module(new RN_Stage)


  ID_Stage.io := DontCare

  // memory と IF　の接続
  IMEM.io <> IF_Stage.io.imem
  // IFとID
  IF_Stage.io.pipeline <> ID_Stage.io.if_pipeline
  // IDとRN
  RN_Stage.io.id_pipeline <> ID_Stage.io.id_pipeline

  // デバッグ配線
  io.if_pipeline <> IF_Stage.io.pipeline
  io.id_pipeline <> ID_Stage.io.id_pipeline
  io.rn_pipeline <> RN_Stage.io.rn_pipeline

  // debug
  printf("inst=[%x] ", IF_Stage.io.pipeline.inst)
  printf("inst1 rd=[%x] ", ID_Stage.io.id_pipeline.inst1.rd)
  printf("rs1=[%x] ", ID_Stage.io.id_pipeline.inst1.rs1)
  printf("rs2=[%x] ", ID_Stage.io.id_pipeline.inst1.rs2)
  printf("\n")


}

