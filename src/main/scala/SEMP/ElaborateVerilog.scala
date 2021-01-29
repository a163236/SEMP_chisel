package SEMP

import SEMP.IF_Stage.IF_Stage
import SEMP.Memory.IMEM

object ElaborateVerilog extends App {
  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new IMEM)
  print(verilogString)
}
