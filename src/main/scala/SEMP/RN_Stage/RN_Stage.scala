package SEMP.RN_Stage

import SEMP.ID_Stage._
import SEMP.Retire_Stage.RN_Return
import chisel3._
import chisel3.util._
import common._


class RN_Stage_IO(implicit val conf: SEMPconfig) extends Bundle{
  // 入力
  val id_pipeline = Flipped(new ID_Pipeline_IO)
  val stall_in = Input(Bool())
  val flush = Input(Bool())
  val flush_tid = Input(UInt(conf.thread_width.W))
  val except = Input(Bool())    // 例外
  val except_tid = Input(Bool())
  val rob = Input(new RN_Return()) // レジスタを解放

  // 出力
  val stall_out = Output(Bool())
  val rn_pipeline = Output(new RN_Pipeline())
}

class RegisterFile(implicit val conf: SEMPconfig) extends Bundle {
  val Rdy = Bool()    // 物理レジスタファイル が読み出し可能かどうか
  val value = UInt(conf.xlen.W)
}

class Maptable(implicit val conf: SEMPconfig) extends Bundle {
  val Rdy = Bool()    // reset or flush 後にrdレジスタをマッピングしたことあるかどうか  retireでは使用しない
  val value = UInt(conf.xlen.W)
}

/*
  今の所は 1スレッドの2命令処理までを対応
  今後は物理レジスタを開放する処理 + 2スレッド対応

  how to process
    全体の流れ
  レジスタリネーミングとレジスタ読み出し，書き込みを行う
  ソースレジスタをまず読んでから，rdをマッピング

    rs の読み出し方法
  futureテーブルのRdyが有効ならそこからRegfileを検索，Rdyが無効ならretireテーブルから検索
  物理レジスタファイルのRdyが無効のときはリザベーションでオペランド待ち

    rd のマッピング
  futureテーブルのRdyを立てる．検索したレジスタファイルのRdyを消す．


*/

class RN_Stage(implicit val conf: SEMPconfig) extends Module {
  val io = IO(new RN_Stage_IO())

  // 物理レジスタファイル
  val regfile = Reg(Vec(conf.xpregnum, new RegisterFile())) // 物理レジスタ数 * 32bit
  // マップ表
  val future_map_t1 = Reg(Vec(conf.xlogregnum, new Maptable())) // スレッド1 投機的マップ表
  val future_map_t2 = Reg(Vec(conf.xlogregnum, new Maptable())) // スレッド2 投機的マップ表
  val retire_map_t1 = Reg(Vec(conf.xlogregnum, new Maptable()))   // 論理レジスタと物理レジスタのマップ
  val retire_map_t2 = Reg(Vec(conf.xlogregnum, new Maptable()))   // 論理レジスタと物理レジスタのマップ

  val freequeue = Module(new freequeue())  // 物理レジスタのフリーリスト
  val init_counter = RegInit(0.U(conf.preg_width.W))  // フリーキューのリセットのためのカウンタ
  val initializing = RegInit(true.B)  // リセット中かどうか

  freequeue.io := DontCare
  // 配線のデフォルト設定
  io := DontCare


  //  パイプラインレジスタ郡
  val PipelineRegs = Reg(new RN_Pipeline())
  io.rn_pipeline := PipelineRegs // 出力 <- パイプラインレジスタ



  // ====================================================================================
  when(reset.asBool() === true.B) { // リセットのとき

    // 物理レジスタファイルの初期化
    for (i <- 0 until regfile.length) {
      regfile(i).Rdy := true.B
      regfile(i).value := i.U
    }
    // マップテーブルの初期化    めんどいから最初はx0にマッピング
    for (i <- 0 until retire_map_t1.length) {
      future_map_t1(i).Rdy := 0.U
      future_map_t2(i).Rdy := 0.U
      retire_map_t1(i).Rdy := 0.U
      retire_map_t2(i).Rdy := 0.U
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

  }.elsewhen(io.stall_in) { // ストール命令が入ったとき

    io.stall_out := io.stall_in // IDにストールを送信  必要ないかも
    // パイプラインレジスタをストール
    PipelineRegs := PipelineRegs
    // validを無効にしなければ．．勝手にリネームされる



  }.elsewhen(io.flush || io.except){ // フラッシュ or 例外のとき

    when(io.flush_tid === THREAD1 || io.except_tid === THREAD1){  // スレッド1に対してのとき

    }.elsewhen(io.flush_tid === THREAD2 || io.except_tid === THREAD2){  // スレッド2に対してのとき

    }

  }.otherwise{  // フリーキューのリセット終わっていたら


    // 命令1 について ======================
    // inst1_ソースレジスタ1　の読み出し
    when(regfile(future_map_t1(io.id_pipeline.inst1.rs1).value).Rdy) { // レディbitが立っているとき読み出す
      PipelineRegs.inst1.rs1_value := regfile(future_map_t1(io.id_pipeline.inst1.rs1).value).value
      PipelineRegs.inst1.rs1_renamed := future_map_t1(io.id_pipeline.inst1.rs1).value
      PipelineRegs.inst1.rs1_Rdy := true.B
    }.otherwise{
      PipelineRegs.inst1.rs1_renamed := future_map_t1(io.id_pipeline.inst1.rs1).value
      PipelineRegs.inst1.rs1_Rdy := false.B   // ソースオペランド getできず．．
    }

    // inst1_RS2 の読み出し
    when(regfile(future_map_t1(io.id_pipeline.inst1.rs2).value).Rdy) { // レディbitが立っているとき読み出す
      PipelineRegs.inst1.rs2_value := regfile(future_map_t1(io.id_pipeline.inst1.rs2).value).value
      PipelineRegs.inst1.rs2_renamed := future_map_t1(io.id_pipeline.inst1.rs2).value
      PipelineRegs.inst1.rs2_Rdy := true.B
    }.otherwise{
      PipelineRegs.inst1.rs2_renamed := future_map_t1(io.id_pipeline.inst1.rs2).value
      PipelineRegs.inst1.rs2_Rdy := false.B   // ソースオペランド getできず．．
    }
    // フリーリストからRDレジスタマッピング
    when(freequeue.io.count =/= 0.U) { // フリーキューが空ではない==リネーム可能なとき
      freequeue.io.deq1_ready := true.B    // フリーキューから取り出してよい
      regfile(freequeue.io.deq1).Rdy := false.B
      future_map_t1(io.id_pipeline.inst1.rd).value := freequeue.io.deq1
      PipelineRegs.inst1.rd_renamed := freequeue.io.deq1
    }

    // 命令2 について ======================
    // inst2_ソースレジスタ1　の読み出し
    when(regfile(future_map_t1(io.id_pipeline.inst2.rs1).value).Rdy) { // レディbitが立っているとき読み出す
      PipelineRegs.inst2.rs1_value := regfile(future_map_t1(io.id_pipeline.inst2.rs1).value).value
      PipelineRegs.inst2.rs1_renamed := future_map_t1(io.id_pipeline.inst2.rs1).value  // レジスタリネーム
      PipelineRegs.inst2.rs1_Rdy := true.B
    }.otherwise{
      PipelineRegs.inst2.rs1_renamed := future_map_t1(io.id_pipeline.inst2.rs1).value  // レジスタリネーム
      PipelineRegs.inst2.rs1_Rdy := false.B   // ソースオペランド getできず．．
    }

    // inst1_RS2 の読み出し
    when(regfile(future_map_t1(io.id_pipeline.inst2.rs2).value).Rdy) { // レディbitが立っているとき読み出す
      PipelineRegs.inst2.rs2_value := regfile(future_map_t1(io.id_pipeline.inst2.rs2).value).value
      PipelineRegs.inst2.rs2_renamed := future_map_t1(io.id_pipeline.inst2.rs2).value
      PipelineRegs.inst2.rs2_Rdy := true.B
    }.otherwise{
      PipelineRegs.inst2.rs2_renamed := future_map_t1(io.id_pipeline.inst2.rs2).value
      PipelineRegs.inst2.rs2_Rdy := false.B   // ソースオペランド getできず．．
    }
    // フリーリストからRDレジスタマッピング
    when(freequeue.io.count=/=0.U) { // フリーキューが空ではない==リネーム可能なとき
      freequeue.io.deq2_ready := true.B    // フリーキューから取り出してよい
      regfile(freequeue.io.deq2).Rdy := false.B
      future_map_t1(io.id_pipeline.inst2.rd).value := freequeue.io.deq2
      PipelineRegs.inst2.rd_renamed := freequeue.io.deq2
    }
  }


  // リタイアした命令について．．後日追記する予定
  when(io.rob.valid){  //  実際はリタイアされなくても返却される

    when(io.rob.tid === THREAD1){  // スレッド1のリタイアのとき
      regfile(io.rob.preg).Rdy := true.B // 物理レジスタファイルを読み出し可能 Rdyをアサート

    }.elsewhen(io.rob.tid === THREAD2){}

  }

}

class RN_Pipeline(implicit val conf: SEMPconfig) extends Bundle{  // パイプラインレジスタ
  val inst1 = new RN_instinfo()
  val inst2 = new RN_instinfo()
}

class RN_instinfo(implicit val conf: SEMPconfig)  extends Bundle{
  val inst_type =   UInt(Inst_Type_WIDTH.W)
  val inst_br_type= UInt(BR_Type_WIDTH.W)
  val alu_op =      UInt(ALU_OP_WIDTH.W)

  val rd_renamed = UInt(conf.preg_width.W)
  val rs1_Rdy = Bool()
  val rs1_renamed = UInt(conf.preg_width.W)  // オペランドバイパスで値をもらうためのタグ
  val rs1_value = UInt(RS1_WIDTH.W)
  val rs2_Rdy = Bool()
  val rs2_renamed = UInt(conf.preg_width.W)
  val rs2_value = UInt(RS2_WIDTH.W)
}
