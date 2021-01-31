package common

import chisel3._
import chisel3.util._

trait SEMP_Constants{

  val ADDR_WIDTH = 32
  val INST_WIDTH = 32
  val FETCH_NUM = 2
  val FETCH_WIDTH = ADDR_WIDTH * FETCH_NUM

  val RS1_MSB = 19  // instからレジスタ1への最上位ビット
  val RS1_LSB = 15  // instからレジスタ1への最下位ビット
  val RS2_MSB = 24
  val RS2_LSB = 20
  val RD_MSB  = 11  // 書き込みレジスタアドレス
  val RD_LSB  = 7
  val CSR_ADDR_MSB = 31
  val CSR_ADDR_LSB = 20

  val START_ADDR = "h00000000"  // MTVEC + 0x100 ?
}

trait SEMP_OpConstants{
  //************************************

  // PC Select
  /*
  val PC_Select_WIDTH = 2.W
  val PC_4 = 0.U(PC_Select_WIDTH) // PC + 4
  val PC_CSR = 2.U(PC_Select_WIDTH)  // CSR- 割り込みか例外
  */

  // Branch Type
  val BR_Type_WIDTH = 4
  val BR_N   = 0.asUInt(BR_Type_WIDTH.W)  // Next
  val BR_NE  = 1.asUInt(BR_Type_WIDTH.W)  // Branch on NotEqual
  val BR_EQ  = 2.asUInt(BR_Type_WIDTH.W)  // Branch on Equal
  val BR_GE  = 3.asUInt(BR_Type_WIDTH.W)  // Branch on Greater/Equal
  val BR_GEU = 4.asUInt(BR_Type_WIDTH.W)  // Branch on Greater/Equal Unsigned
  val BR_LT  = 5.asUInt(BR_Type_WIDTH.W)  // Branch on Less Than
  val BR_LTU = 6.asUInt(BR_Type_WIDTH.W)  // Branch on Less Than Unsigned
  val BR_J   = 7.asUInt(BR_Type_WIDTH.W)  // Jump
  val BR_JR  = 8.asUInt(BR_Type_WIDTH.W)  // Jump Register


}