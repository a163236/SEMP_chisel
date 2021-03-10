package SEMP.IF_Stage

import SEMP.Memory.IMEM_IO
import chisel3._
import chisel3.aop.Select.Printf
import common._

class IF_Stage_IO(implicit val conf: SEMPconfig) extends Bundle {
  val imem = Flipped(new IMEM_IO)
  val stall = Input(Bool())
  val flush = Input(Bool())
  val flush_tid = Input(UInt(conf.thread_width.W))  // どの実スレッドをフラッシュするか
  val marumo = Input(Bool())
  val exception = Input(Bool())
  val pipeline = new IF_Pipeline_IO()
}

class IF_Stage(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new IF_Stage_IO)

  // レジスタ
  val PC = RegInit(START_ADDR.U(conf.xlen.W))

  // 配線
  val PC_next = WireInit(START_ADDR.U(conf.xlen.W))
  val IF_valid = WireInit(false.B)

  PC := PC_next;
  when(reset.asBool()){
    PC_next := START_ADDR.U
    IF_valid := false.B
  }.elsewhen(io.exception){
    PC_next := 0.U

  }.elsewhen(io.stall){
    PC_next := PC
  }.elsewhen(io.flush){
    PC_next := PC + 4.U
  }.otherwise{
    PC_next := PC + 4.U
    IF_valid := true.B
  }

  // メモリへ
  io.imem.CLK := clock
  io.imem.req_addr := PC_next
  io.imem.req_valid := true.B

  // パイプラインレジスタ
  io.pipeline.pc := RegNext(PC)
  io.pipeline.inst := io.imem.resp_data
  io.pipeline.if_valid := IF_valid
  io.pipeline.if_tid := 0.U

  printf("inst=[%x] ", io.pipeline.inst)
  printf("\n")
}

class IF_Pipeline_IO(implicit val conf: SEMPconfig) extends Bundle{
  val pc = Output(UInt(conf.xlen.W))
  val inst = Output(UInt(conf.fetch_width.W))
  val if_valid = Output(Bool())
  val if_tid = Output(UInt(conf.thread_width.W))
}
