package common

import chisel3._
import chisel3.util._

trait SEMP_Constants{

  // スレッド番号
  val THREAD1 = 0.asUInt(1.W)
  val THREAD2 = 1.asUInt(1.W)

  val OP_MSB = 6
  val OP_LSB = 0
  val OP_WIDTH = OP_MSB-OP_LSB + 1
  val RS1_MSB = 19  // instからレジスタ1への最上位ビット
  val RS1_LSB = 15  // instからレジスタ1への最下位ビット
  val RS1_WIDTH = RS1_MSB-RS1_LSB + 1
  val RS2_MSB = 24
  val RS2_LSB = 20
  val RS2_WIDTH = RS2_MSB-RS2_LSB + 1
  val RD_MSB  = 11  // 書き込みレジスタアドレス
  val RD_LSB  = 7
  val RD_WIDTH = RD_MSB-RD_LSB + 1
  val FUNCT3_MSB = 14
  val FUNCT3_LSB = 12
  val FUNCT3_WIDTH = FUNCT3_MSB-FUNCT3_LSB + 1
  val FUNCT7_MSB = 31
  val FUNCT7_LSB = 25
  val FUNCT7_WIDTH = FUNCT7_MSB-FUNCT7_LSB + 1

  // A拡張
  val AQ_bit = 26
  val RL_bit = 25

  val CSR_ADDR_MSB = 31
  val CSR_ADDR_LSB = 20

  val INST_WIDTH = 32 // 1命令のbit幅

  val START_ADDR = "h00000000"  // 最初のアドレス

  // システムレジスタアドレス
  // マシンのトラップハンドリングのベースレジスタ
  // https://people.eecs.berkeley.edu/~krste/papers/riscv-privileged-v1.9.1.pdf
  val MTVEC = 0x100

}

trait SEMP_OpConstants{
  //************************************

  // Inst_Type  デコードされた命令がどの形式の命令なのか
  // どのリザーベーションステーションに放り込むか
  val Inst_Type_WIDTH = 4
  val Inst_R = 0.asUInt(Inst_Type_WIDTH.W)
  val Inst_I = 1.asUInt(Inst_Type_WIDTH.W)  // ロード命令以外の即値命令
  val Inst_S = 2.asUInt(Inst_Type_WIDTH.W)  // Store 命令
  val Inst_B = 3.asUInt(Inst_Type_WIDTH.W)
  val Inst_U = 4.asUInt(Inst_Type_WIDTH.W)
  val Inst_J = 5.asUInt(Inst_Type_WIDTH.W)
  val Inst_L = 6.asUInt(Inst_Type_WIDTH.W)  // Load 命令

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

  // ALU_OP,  ALUを使用するR形式とI形式のときに必要
  val ALU_OP_WIDTH = 4
  val ALU_ADD = 1.asUInt(ALU_OP_WIDTH.W)
  val ALU_SUB = 2.asUInt(ALU_OP_WIDTH.W)
  val ALU_SLL = 3.asUInt(ALU_OP_WIDTH.W)
  val ALU_SRL = 4.asUInt(ALU_OP_WIDTH.W)
  val ALU_SRA = 5.asUInt(ALU_OP_WIDTH.W)
  val ALU_AND = 6.asUInt(ALU_OP_WIDTH.W)
  val ALU_OR  = 7.asUInt(ALU_OP_WIDTH.W)
  val ALU_XOR = 8.asUInt(ALU_OP_WIDTH.W)
  val ALU_SLT = 9.asUInt(ALU_OP_WIDTH.W)
  val ALU_SLTU= 10.asUInt(ALU_OP_WIDTH.W)
  val ALU_COPYrs1= 11.asUInt(ALU_OP_WIDTH.W)
  val ALU_COPYrs2= 12.asUInt(ALU_OP_WIDTH.W)
  val ALU_X   = 0.asUInt(ALU_OP_WIDTH.W)

}