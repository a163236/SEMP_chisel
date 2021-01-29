package SEMP.Memory

import chisel3._
import chisel3.util._
import common._

class IMEM_IO extends Bundle{
  val CLK = Input(Clock())
  val req_addr = Input(UInt(ADDR_WIDTH.W))
  val req_valid = Input(Bool())
  val resp_data = Output(UInt(FETCH_WIDTH.W))
}

class IMEM extends Module{
  val io = IO(new IMEM_IO)

  val bram = Module(new IMEM_BlackBox())
  bram.io <> io
  bram.io.CLK := clock  // 必要

}

// bram
class IMEM_BlackBox extends BlackBox(Map(
  "MEMSIZE"->4*1024,
  "INST_NUM"->FETCH_NUM,
  "ADDR_WIDTH"->ADDR_WIDTH,
  "FETCH_WIDHT"->FETCH_WIDTH,
)) with HasBlackBoxResource {
  val io = IO(new IMEM_IO)
  addResource("/I_memory_bram.v")
}