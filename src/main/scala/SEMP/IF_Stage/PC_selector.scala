package SEMP.IF_Stage

import chisel3._
import common._

class PC_selector_IO(implicit val conf: SEMPconfig) extends Bundle {
  // 入力
  val if_tid = Input(UInt(conf.thread_width.W)) // IFステージのスレッドid
  val retire_tid = Input(UInt(conf.thread_width.W)) // リタイアしたスレッドid

  // 出力
  val tid_out = Output(UInt(conf.thread_width.W))
}

class PC_seletor(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new PC_selector_IO())

  io.tid_out := THREAD1

}