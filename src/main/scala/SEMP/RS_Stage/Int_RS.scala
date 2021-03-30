package SEMP.RS_Stage

import chisel3._

class Int_RS_IO extends Bundle{
  val op = UInt(32.W)
  /*
  val dtag = UInt()
  val rs1tag = UInt()
  val rs1rdy = Bool()
  val rs1value = UInt()
  val rs2tag = UInt()
  val rs2rdy = Bool()
  val rs2value = UInt()
   */

  val Rdy = Bool()      // wakeup理論
  val selected = Bool() // 選択理論
}
class Int_RS extends Module{
  val io = IO(Input(new Int_RS_IO()))

  val a = new Int_RS_IO
  val int_RS = Reg(Vec(4 , a))    // 整数命令のリザーベーションステーション
  val pointer = Reg(UInt(3.W),0.U)

}
