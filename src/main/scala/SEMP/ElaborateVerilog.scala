package SEMP

import SEMP.ID_Stage._
import SEMP.IF_Stage._
import SEMP.Memory.IMEM

object ElaborateVerilog extends App {
  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new decoder)
  print(verilogString)
}
