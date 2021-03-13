package SEMP.RN_Stage

import SEMP.ID_Stage._
import chisel3._
import chisel3.util._
import common._

class RN_Stage_IO(implicit val conf: SEMPconfig) extends Bundle{
  // 入力
  val id_pipeline = Flipped(new ID_Pipeline_IO)
  val stall_in = Input(Bool())

  // 出力
  val stall_out = Output(Bool())
  val rn_pipeline = new RN_Pipeline_IO()
}

class RegisterFile(implicit val conf: SEMPconfig) extends Bundle {
  val Rdy = Bool()
  val value = UInt(conf.xlen.W)
}

/*
  今の所は 1スレッドの2命令処理までを対応
  今後は物理レジスタを開放する処理 + 2スレッド対応

  how to process
  レジスタリネーミングとレジスタ読み出し，書き込みを行う
  ソースレジスタをまず読んでから，rdをマッピング
 */

class RN_Stage(implicit val conf: SEMPconfig) extends Module {
  val io = IO(new RN_Stage_IO())

  // 物理レジスタファイル
  val regfile = Reg(Vec(conf.xpregnum, new RegisterFile())) // 物理レジスタ数 * 32bit
  // マップ表
  val commited_maptable_t1 = Reg(Vec(conf.xlogregnum, UInt(conf.preg_width.W)))   // 論理レジスタと物理レジスタのマップ
  val speculac_maptable_t1 = Reg(Vec(conf.xlogregnum, UInt(conf.xlen.W))) // 投機的マップ表
  val commited_maptable_t2 = Reg(Vec(conf.xlogregnum, UInt(conf.xlen.W)))   // 論理レジスタと物理レジスタのマップ
  val speculac_maptable_t2 = Reg(Vec(conf.xlogregnum, UInt(conf.xlen.W))) // 投機的マップ表

  val freequeue = Module(new freequeue())  // 物理レジスタのフリーリスト
  val init_counter = RegInit(0.U(conf.preg_width.W))  // フリーキューのリセットのためのカウンタ
  val initializing = RegInit(true.B)  // リセット中かどうか

  freequeue.io := DontCare
  // 配線のデフォルト設定
  io := DontCare


  // ====================================================================================
  when(reset.asBool() === true.B) { // リセットのとき

    // 物理レジスタファイルの初期化
    for (i <- 0 until regfile.length) {
      regfile(i).Rdy := true.B
      regfile(i).value := i.U
    }
    // マップテーブルの初期化    めんどいから最初はx0にマッピング
    for (i <- 0 until commited_maptable_t1.length) {
      commited_maptable_t1(i) := 0.U
      speculac_maptable_t1(i) := 0.U
      commited_maptable_t2(i) := 0.U
      speculac_maptable_t2(i) := 0.U
    }
    // フリーキューのリセット
    initializing := true.B
    freequeue.io.enq1_valid := false.B
    freequeue.io.enq2_valid := false.B
    // 出力
    io.stall_out := true.B

  }.elsewhen(initializing) { // フリーキュー初期化処理

    freequeue.io.enq1_valid := true.B
    freequeue.io.deq1_ready := false.B
    freequeue.io.deq2_ready := false.B
    freequeue.io.enq1 := init_counter
    init_counter := init_counter + 1.U
    when(init_counter >= (conf.xpregnum/2).U) { // 初期化終わりの処理
      initializing := false.B
      freequeue.io.enq1_valid := false.B
      io.stall_out := false.B
    }

  }.elsewhen(io.stall_in) {   // ストール命令が入ったとき

    io.stall_out := io.stall_in   // IDにストールを送信



  }.otherwise{  // フリーキューのリセット終わっていたら

    // 命令1 について ======================
    // inst1_ソースレジスタ1　の読み出し
    when(regfile(speculac_maptable_t1(io.id_pipeline.inst1.rs1)).Rdy) { // レディbitが立っているとき読み出す
      io.rn_pipeline.inst1.rs1_value := regfile(speculac_maptable_t1(io.id_pipeline.inst1.rs1)).value
      io.rn_pipeline.inst1.rs1_Rdy := true.B
    }.otherwise{
      io.rn_pipeline.inst1.rs1_Rdy := false.B   // ソースオペランド getできず．．
    }

    // inst1_RS2 の読み出し
    when(regfile(speculac_maptable_t1(io.id_pipeline.inst1.rs2)).Rdy) { // レディbitが立っているとき読み出す
      io.rn_pipeline.inst1.rs2_value := regfile(speculac_maptable_t1(io.id_pipeline.inst1.rs2)).value
      io.rn_pipeline.inst1.rs2_Rdy := true.B
    }.otherwise{
      io.rn_pipeline.inst1.rs2_Rdy := false.B   // ソースオペランド getできず．．
    }
    // フリーリストからRDレジスタマッピング
    when(freequeue.io.count =/= 0.U) { // フリーキューが空ではない==リネーム可能なとき
      freequeue.io.deq1_ready := true.B    // フリーキューから取り出してよい
      regfile(freequeue.io.deq1).Rdy := false.B
      speculac_maptable_t1(io.id_pipeline.inst1.rd) := freequeue.io.deq1
      io.rn_pipeline.inst1.rd_renamed := freequeue.io.deq1
    }

    // 命令2 について ======================
    // inst2_ソースレジスタ1　の読み出し
    when(regfile(speculac_maptable_t1(io.id_pipeline.inst2.rs1)).Rdy) { // レディbitが立っているとき読み出す
      io.rn_pipeline.inst2.rs1_value := regfile(speculac_maptable_t1(io.id_pipeline.inst2.rs1)).value
      io.rn_pipeline.inst2.rs1_Rdy := true.B
    }.otherwise{
      io.rn_pipeline.inst2.rs1_Rdy := false.B   // ソースオペランド getできず．．
    }

    // inst1_RS2 の読み出し
    when(regfile(speculac_maptable_t1(io.id_pipeline.inst2.rs2)).Rdy) { // レディbitが立っているとき読み出す
      io.rn_pipeline.inst2.rs2_value := regfile(speculac_maptable_t1(io.id_pipeline.inst2.rs2)).value
      io.rn_pipeline.inst2.rs2_Rdy := true.B
    }.otherwise{
      io.rn_pipeline.inst2.rs2_Rdy := false.B   // ソースオペランド getできず．．
    }
    // フリーリストからRDレジスタマッピング
    when(freequeue.io.count=/=0.U) { // フリーキューが空ではない==リネーム可能なとき
      freequeue.io.deq2_ready := true.B    // フリーキューから取り出してよい
      regfile(freequeue.io.deq2).Rdy := false.B
      speculac_maptable_t1(io.id_pipeline.inst2.rd) := freequeue.io.deq2
      io.rn_pipeline.inst2.rd_renamed := freequeue.io.deq2
    }

    // リタイアした命令について．．後日追記する予定

  }

}

class RN_Pipeline_IO(implicit val conf: SEMPconfig) extends Bundle{
  val inst1 = new RN_instinfo_IO()
  val inst2 = new RN_instinfo_IO()

}

class RN_instinfo_IO(implicit val conf: SEMPconfig)  extends Bundle{
  val inst_type =   Output(UInt(Inst_Type_WIDTH.W))
  val inst_br_type= Output(UInt(BR_Type_WIDTH.W))
  val alu_op =      Output(UInt(ALU_OP_WIDTH.W))

  val rd_renamed = Output(UInt(conf.preg_width.W))
  val rs1_Rdy = Output(Bool())
  val rs1_value = Output(UInt(RS1_WIDTH.W))
  val rs2_Rdy = Output(Bool())
  val rs2_value = Output(UInt(RS2_WIDTH.W))
}