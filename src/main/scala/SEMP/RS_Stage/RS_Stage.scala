package SEMP.RS_Stage

import chisel3._

class RS_Stage_IO extends Bundle{

}

class Int_RS extends Bundle{
  val Rdy = Bool()
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

}

class RS_Stage extends Module{
  val io = IO(new RS_Stage_IO)
  val a = new Int_RS
  val int_RS = Reg(Vec(4 , a))    // 整数命令のリザーベーションステーション
}
