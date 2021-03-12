package SEMP.IF_Stage

import SEMP.Memory.IMEM_IO
import chisel3._
import common._

class IF_Stage_IO(implicit val conf: SEMPconfig) extends Bundle {
  val imem = Flipped(new IMEM_IO) // メモリとの接続
  val stall = Input(Bool())
  val flush = Input(Bool())
  val flush_tid = Input(UInt(conf.thread_width.W))  // どの実スレッドをフラッシュするか
  val exception = Input(Bool())
  val pipeline = new IF_Pipeline_IO()
}

class IF_Stage(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new IF_Stage_IO)

  // PCセレクタ - どちらのスレッドのPCを選択するか
  val PC_selector = Module(new PC_seletor())
  PC_selector.io := DontCare

  // プログラムカウンタ
  val PC1 = RegInit(START_ADDR.U(conf.xlen.W))
  val PC2 = RegInit(START_ADDR.U(conf.xlen.W))

  val IF_valid = WireInit(false.B)  // IF　が有効の出力


  when(reset.asBool()){ //  リセットのとき
    PC1 := START_ADDR.U
    PC2 := START_ADDR.U

    IF_valid := false.B

  }.elsewhen(io.exception){ //  例外のとき
    PC1 := MTVEC.U
    PC2 := MTVEC.U
    IF_valid := true.B

  }.elsewhen(io.stall){ //  ストールのとき
    PC1 := PC1
    PC2 := PC2
    IF_valid := false.B

  }.elsewhen(io.flush){ // フラッシュのとき
    PC1 := PC1
    PC2 := PC2
    IF_valid := false.B

  }.otherwise{          // 普通のとき
    PC1 := PC1 + 8.U
    PC2 := PC2 + 8.U

    IF_valid := true.B
  }

  // 命令メモリへのリクエスト
  io.imem.CLK := clock
  io.imem.req_addr := PC1
  io.imem.req_valid := true.B

  // パイプラインレジスタ
  io.pipeline.pc := RegNext(PC1)    // パイプラインとしてレジスタを噛ませる
  io.pipeline.inst := io.imem.resp_data
  io.pipeline.if_valid := RegNext(IF_valid)
  io.pipeline.if_tid := RegNext(PC_selector.io.tid_out)


}

class IF_Pipeline_IO(implicit val conf: SEMPconfig) extends Bundle{
  val pc = Output(UInt(conf.xlen.W))
  val inst = Output(UInt(conf.fetch_width.W))
  val if_valid = Output(Bool())
  val if_tid = Output(UInt(conf.thread_width.W))
}
