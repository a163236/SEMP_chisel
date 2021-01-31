package SEMP.IF_Stage

import SEMP.Memory.IMEM_IO
import chisel3._
import chisel3.aop.Select.Printf
import common._

class IF_Stage_IO extends Bundle {
  val imem = Flipped(new IMEM_IO)
  val stall = Input(Bool())
  val flush = Input(Bool())
  val exception = Input(Bool())
  val pipeline = new IF_Pipeline_IO()

}

class IF_Stage extends Module{
  val io = IO(new IF_Stage_IO)

  // レジスタ
  val PC = RegInit(START_ADDR.U(ADDR_WIDTH.W))

  // 配線
  val PC_next = WireInit(START_ADDR.U(ADDR_WIDTH.W))
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
}

class IF_Pipeline_IO extends Bundle{
  val pc = Output(UInt(ADDR_WIDTH.W))
  val inst = Output(UInt(FETCH_WIDTH.W))
  val if_valid = Output(Bool())
}
