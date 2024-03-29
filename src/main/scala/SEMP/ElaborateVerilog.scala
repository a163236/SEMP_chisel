package SEMP

import SEMP.ID_Stage._
import SEMP.IF_Stage._
import SEMP.Memory.IMEM
import SEMP.RN_Stage._
import SEMP.RS_Stage.Int_RS
import common._

object ElaborateVerilog extends App {

  implicit val config = new SEMPconfig(synthesize = true)
  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new Int_RS())
  print(verilogString)
}
