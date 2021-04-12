package SEMP.RS_Stage

import chisel3._
import chisel3.util._
import common.SEMPconfig

class Int_RS_info extends Bundle{
  val valid = Bool()
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

class Int_RS_IO extends Bundle{
  val in = Input(new Int_RS_info)
  val out = Output(UInt(32.W))
}
class Int_RS(implicit conf:SEMPconfig) extends Module{
  val io = IO(new Int_RS_IO())
  io := DontCare


  val a = new Int_RS_info
  val int_RS = Reg(Vec(4 , a))    // 整数命令のリザーベーションステーション
  //val next_int_RS = Wire(Vec(4, a)) // ↑のupdate配線
  val select_wire = Wire(Vec(4, Bool()))
  val move_wire = Wire(Vec(4,Bool())) // いくら移動させる
  val pointer = RegInit(UInt(3.W),0.U)

  pointer := MuxCase(pointer, Array(
    (io.in.valid && io.in.selected) -> (pointer),
    io.in.valid -> (pointer + 1.U),
    io.in.selected -> (pointer - 1.U),
  ))

  io.out := int_RS(pointer).op

  // update 更新
  for (i <- 1 until conf.rsQuelen){
    when(move_wire(i)){
      int_RS(i-1) := int_RS(i)
    }
  }

  // 選択
  int_RS(0).selected := select_wire(0)
  select_wire(0) := int_RS(0).Rdy
  move_wire(0) := select_wire(0)
  for (i <- 1 until conf.rsQuelen){
    select_wire(i) := !select_wire(i-1) && int_RS(i).Rdy
    int_RS(i).selected := select_wire(i) // 選択論理
    move_wire(i) := move_wire(i-1) + select_wire(i)
  }

  // wakeup
  



  printf("pointer=[%x] ",pointer)
  printf("\n")
}
