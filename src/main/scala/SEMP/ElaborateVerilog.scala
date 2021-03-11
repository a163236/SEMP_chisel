package SEMP

import SEMP.ID_Stage._
import SEMP.IF_Stage._
import SEMP.Memory.IMEM
import SEMP.RN_Stage.RN_Stage
import SEMP.RS_Stage.RS_Stage
import common._

object ElaborateVerilog extends App {

  implicit val config = new SEMPconfig(synthesize = true)
  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new RS_Stage())
  print(verilogString)
}
