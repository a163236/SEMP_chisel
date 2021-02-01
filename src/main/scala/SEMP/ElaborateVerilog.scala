package SEMP

import SEMP.ID_Stage._
import SEMP.IF_Stage._
import SEMP.Memory.IMEM
import common._

object ElaborateVerilog extends App {

  implicit val config = new SEMPconfig(synthesize = true)
  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new temp())
  print(verilogString)
}
