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

  // 出力
  val id_pipeline = new ID_Pipeline_IO()
}

class ID_Stage(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new ID_Stage_IO)

  val decoder1 = Module(new decoder())
  val decoder2 = Module(new decoder())

  // 各デコーダに命令をそれぞれ入れる
  decoder1.io.inst := io.if_pipeline.inst(INST_WIDTH-1, 0)
  decoder2.io.inst := io.if_pipeline.inst(INST_WIDTH*2-1, INST_WIDTH)

  // 出力  各デコーダからの出力を受け取る
  io.id_pipeline.inst1 := decoder1.io.decoded_inst
  io.id_pipeline.inst2 := decoder2.io.decoded_inst
  //      命令が有効化どうか
  io.id_pipeline.inst1_valid := io.if_pipeline.if_valid // １つ目の命令は有効か？
  io.id_pipeline.inst2_valid := (io.if_pipeline.if_valid &&
    !(decoder2.io.decoded_inst.inst_br_type === Inst_J)) // ２つ目の命令は1つ目がJ命令なら無効

}

class ID_Pipeline_IO extends Bundle {
  val inst1 = new ID_deocoded_info()
  val inst1_valid = Output(Bool())
  val inst2 = new ID_deocoded_info()
  val inst2_valid = Output(Bool())
  val tid = Output(Bool())
}

