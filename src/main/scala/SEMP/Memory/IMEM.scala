package SEMP.Memory

import chisel3._
import chisel3.util._
import common._

class IMEM_IO(implicit val conf: SEMPconfig) extends Bundle{
  val CLK = Input(Clock())
  val req_addr = Input(UInt(conf.xlen.W))
  val req_valid = Input(Bool())
  val resp_data = Output(UInt(conf.fetch_width.W))
}

class IMEM(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new IMEM_IO)

  val bram = Module(new I_memory_bram())
  bram.io <> io
  bram.io.CLK := clock  // 必要 これがないとCLKが入らないから

}

// bram
class I_memory_bram(implicit val conf: SEMPconfig) extends BlackBox(Map(
  "MEM_SIZE"->4*1024,
  "INST_NUM"->conf.fetch_width,
  "ADDR_WIDTH"->conf.xlen,
  "FETCH_WIDTH"->conf.fetch_width,
)) with HasBlackBoxResource {
  val io = IO(new IMEM_IO)
  addResource("/I_memory_bram.v")
}