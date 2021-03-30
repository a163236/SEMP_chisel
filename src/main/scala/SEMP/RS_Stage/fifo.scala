package SEMP.RS_Stage

import chisel3._

// 2in 1out のfifo

class fifo_io extends Bundle{
  val in = Input(UInt(32.W))
  val in_valid = Input(Bool())
  val out_valid = Input(Bool())
  val out = Output(UInt(32.W))
}

class fifo extends Module{
  val io = IO(new fifo_io)
  io := DontCare

  val fifo = Reg(Vec(4, UInt(32.W)))

  val head, tail = RegInit(UInt(2.W), 0.U)

  when(io.in_valid){
    fifo(tail) := io.in // 入力をいれる
    tail := tail + 1.U
  }

  when(io.out_valid){
    io.out := fifo(head)  // 出力
    head := head + 1.U
  }


}


object fifo_23 extends App{
  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new fifo())
  print(verilogString)
}