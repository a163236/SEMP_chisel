package SEMP.RN_Stage

import chisel3._
import chisel3.util._
import common._

/*
  2つのキューを合体させて1つのキューとして扱えるようにする
  入り口と出口を２つ持つ，どちらを使ってもよいこととする

  enq_valid をアサートすると　エンキューを有効
  deq_ready をアサートすると  デキューを有効
 */

class freequeueIO(implicit val conf: SEMPconfig) extends Bundle{

  // 入力
  val enq1 = Input(UInt(conf.preg_width.W))
  val enq2 = Input(UInt(conf.preg_width.W))
  val enq1_valid = Input(Bool())    // エンキューの有効
  val enq2_valid = Input(Bool())
  val deq1_ready = Input(Bool())    // デキューの有効
  val deq2_ready = Input(Bool())

  // 出力
  val deq1 = Output(UInt(conf.preg_width.W))
  val deq2 = Output(UInt(conf.preg_width.W))

  val count = Output(UInt(conf.preg_width.W)) // キューの合計
}

class freequeue(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new freequeueIO())
  io := DontCare

  val queue1, queue2 = Module(new Queue(gen=UInt(conf.preg_width.W),
    entries=conf.xpregnum/2, pipe=false, flow=true))  // 物理レジスタのフリーリスト
  queue1.io := DontCare; queue2.io := DontCare



  // 合計値
  io.count := queue1.io.count + queue2.io.count

  // エンキュー

  when(io.enq1_valid && io.enq2_valid){ // 2つともエンキューするとき
    queue1.io.enq.bits := io.enq1; queue1.io.enq.valid := true.B
    queue2.io.enq.bits := io.enq2; queue2.io.enq.valid := true.B
  }.elsewhen(io.enq1_valid){

    when(queue1.io.count <= queue2.io.count){ // 少ない方を採用
      queue1.io.enq.bits := io.enq1;
      queue1.io.enq.valid := true.B
      queue2.io.enq.valid := false.B
    }.otherwise{
      queue2.io.enq.bits := io.enq1;
      queue1.io.enq.valid := false.B
      queue2.io.enq.valid := true.B

    }
  }.elsewhen(io.enq2_valid) { // 2つめだけエンキュー
    when(queue1.io.count <= queue2.io.count){ // 少ない方を採用
      queue1.io.enq.bits := io.enq2;
      queue1.io.enq.valid := true.B
      queue2.io.enq.valid := false.B
    }.otherwise{
      queue2.io.enq.bits := io.enq2;
      queue1.io.enq.valid := false.B
      queue2.io.enq.valid := true.B
    }
  }.otherwise{  // エンキューなし
    queue1.io.enq.valid := false.B; queue2.io.enq.valid := false.B
  }

  // デキュー

  when(io.deq1_ready && io.deq2_ready){ // 2つともデキューする

    io.deq1 := queue1.io.deq.bits; queue1.io.deq.ready:=true.B
    io.deq2 := queue2.io.deq.bits; queue2.io.deq.ready:=true.B
  }.elsewhen(io.deq1_ready){

    when(queue1.io.count >= queue2.io.count){ // 多い方を採用
      io.deq1 := queue1.io.deq.bits;
      queue1.io.deq.ready := true.B
      queue2.io.deq.ready := false.B
    }.otherwise{
      io.deq1 := queue2.io.deq.bits;
      queue1.io.deq.ready := false.B
      queue2.io.deq.ready := true.B
    }

  }.elsewhen(io.deq2_ready){

    when(queue1.io.count >= queue2.io.count){ // 多い方を採用
      io.deq2 := queue1.io.deq.bits;
      queue1.io.deq.ready := true.B
      queue2.io.deq.ready := false.B
    }.otherwise{
      io.deq2 := queue2.io.deq.bits;
      queue1.io.deq.ready := false.B
      queue2.io.deq.ready := true.B
    }

  }.otherwise{  // デキューなし
    queue1.io.deq.ready := false.B; queue2.io.deq.ready := false.B
  }

}
