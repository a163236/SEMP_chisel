package SEMP.RN_Stage

import SEMP.ID_Stage._
import chisel3._
import common._

class RN_Stage_IO(implicit val conf: SEMPconfig) extends Bundle{
  // 入力
  val id_pipeline = Flipped(new ID_Pipeline_IO)

  // 出力
  val rn_pipeline = new RN_Pipline_IO()
}

class RN_Stage(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new RN_Stage_IO())

  val maptable = Reg(Vec(conf.xlogregnum, UInt(conf.xlen.W)))   // 論理レジスタと物理レジスタのマップ

  // フリーリスト
  val freelist = Reg(Vec(conf.xpregnum, UInt(conf.xpregnum.U.getWidth.W)))  // 物理レジスタ番号の数もつ
  val head = RegInit(0.U(conf.xpregnum.W))
  val tail = RegInit(((conf.xpregnum-1).U)(conf.xpregnum.W))  // headから入れてtailから出す

  maptable(io.id_pipeline.inst1.rd) := freelist(tail)
  tail := tail - 1.U


}

class RN_Pipline_IO extends Bundle{

}