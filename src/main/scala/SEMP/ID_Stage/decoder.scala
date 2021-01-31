package SEMP.ID_Stage

import chisel3._
import chisel3.util._
import common.Instructions._
import common._

class decoder_IO extends Bundle{
  val inst = Input(UInt(INST_WIDTH.W))
  val csignals = Output(UInt(BR_Type_WIDTH.W))
}

class decoder extends Module{
  val io = IO(new decoder_IO)

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
  io.csignals := cs_br_type


}
