package SEMP.ID_Stage

import chisel3._
import chisel3.util._
import common.Instructions._
import common._

class decoder_IO(implicit val conf: SEMPconfig) extends Bundle{
  val inst = Input(UInt(INST_WIDTH.W))
  val decoded_inst = new ID_deocoded_info()
}

class ID_deocoded_info extends Bundle{
  val inst_br_type= Output(UInt(BR_Type_WIDTH.W))
  val op = Output(UInt(OP_WIDTH.W))
  val rd = Output(UInt(RD_WIDTH.W))
  val rs1 = Output(UInt(RS1_WIDTH.W))
  val rs2 = Output(UInt(RS2_WIDTH.W))
  val funct3 = Output(UInt(FUNCT3_WIDTH.W))
  val funct7 = Output(UInt(FUNCT7_WIDTH.W))

  val aq_bit = Output(Bool())
  val rl_bit = Output(Bool())
}

class decoder(implicit val conf: SEMPconfig) extends Module{
  val io = IO(new decoder_IO)

  //
  io.decoded_inst.op := io.inst(OP_MSB, OP_LSB)
  io.decoded_inst.rd := io.inst(RD_MSB, RD_LSB)
  io.decoded_inst.rs1 := io.inst(RS1_MSB, RS1_LSB)
  io.decoded_inst.rs2 := io.inst(RS2_MSB, RS2_LSB)
  io.decoded_inst.funct3 := io.inst(FUNCT3_MSB, FUNCT3_LSB)
  io.decoded_inst.funct7 := io.inst(FUNCT7_MSB, FUNCT7_LSB)
  // A拡張
  io.decoded_inst.aq_bit := io.inst(AQ_bit)
  io.decoded_inst.rl_bit := io.inst(RL_bit)

  // control signal
  val csignals =
    ListLookup(io.inst,
                    List(BR_N ), // 初期値
      Array(           /* BR   */
                        /* type */
        JALR    -> List(BR_N),
        BEQ     -> List(BR_EQ),
        LW      -> List(BR_N),
        SW      -> List(BR_N),
        ADD     -> List(BR_N),

        /*
        ECALL   -> List(),
        EBREAK  -> List(),
        CSRRWI  -> List()

         */
      ))

  // 上のリストで選ばれたものを変数にぶち込む
  val cs_br_type :: Nil  = csignals
  io.decoded_inst.inst_br_type := cs_br_type


}
