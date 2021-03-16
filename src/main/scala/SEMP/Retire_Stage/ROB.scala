package SEMP.Retire_Stage

import chisel3._
import common._

class RN_Return(implicit val conf: SEMPconfig) extends Bundle{  // フリーリストを解放する出力
  // ROBがフラッシュされてもフリーキューにはちゃんと2つずつ返す
  val preg = UInt(conf.preg_width.W)  // リネーム後の物理レジスタタグ
  val logreg = UInt(conf.logreg_width.W)  // リネームする前の論理レジスタ
  val prev_preg = UInt(conf.preg_width.W) // 解放する物理レジスタタグ
  val tid = UInt(conf.thread_width.W) // リタイアしたスレッドid
  val valid = Bool()    // 有効
}


class ROB {

}
