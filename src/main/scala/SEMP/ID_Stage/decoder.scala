package SEMP.ID_Stage

import chisel3._
import chisel3.util._
import common.Instructions._
import common._

class decoder_IO(implicit val conf: SEMPconfig) extends Bundle{
  val inst = Input(UInt(INST_WIDTH.W))
  val decoded_inst = new ID_deocoded_info()
}

class ID_deocoded_info extends Bundle{    // デコードされた命令情報 -> パイプラインで流す
  val inst_type =   Output(UInt(Inst_Type_WIDTH.W))
  val inst_br_type= Output(UInt(BR_Type_WIDTH.W))
  val alu_op =      Output(UInt(ALU_OP_WIDTH.W))
  val rd = Output(UInt(RD_WIDTH.W))
  val rs1 = Output(UInt(RS1_WIDTH.W))
  val rs2 = Output(UInt(RS2_WIDTH.W))
}

class decoder(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new decoder_IO)

  // 命令を分解
  io.decoded_inst.rd := io.inst(RD_MSB, RD_LSB)
  io.decoded_inst.rs1 := io.inst(RS1_MSB, RS1_LSB)
  io.decoded_inst.rs2 := io.inst(RS2_MSB, RS2_LSB)

  // control signal
  val csignals =
    ListLookup(io.inst,
                    List(Inst_R ,BR_N, ALU_X), // 初期値
      Array(           /* BR   */
                        /* type */
        JALR    -> List(Inst_J, BR_N,  ALU_X),
        BEQ     -> List(Inst_B, BR_EQ, ALU_X),
        LW      -> List(Inst_L, BR_N,  ALU_X),
        SW      -> List(Inst_S, BR_N,  ALU_X),
        ADD     -> List(Inst_R, BR_N,  ALU_ADD),
        ADDI    -> List(Inst_I, BR_N,  ALU_ADD),

        /*
        ECALL   -> List(),
        EBREAK  -> List(),
        CSRRWI  -> List()

         */
      ))

  // 上のリストで選ばれたものを変数にぶち込む
  val cs_inst_type :: cs_br_type :: cs_alu_op :: Nil  = csignals

  io.decoded_inst.inst_type := cs_inst_type
  io.decoded_inst.inst_br_type := cs_br_type
  io.decoded_inst.alu_op := cs_alu_op

}
