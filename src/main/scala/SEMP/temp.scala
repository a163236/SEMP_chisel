package SEMP

import SEMP.IF_Stage.IF_Stage
import SEMP.Memory.IMEM
import chisel3._

class temp extends Module{
  val io = IO(new Bundle() {

  })

  val IF_Stage = Module(new IF_Stage)
  IF_Stage.io := DontCare

  val IMEM = Module(new IMEM)
  IMEM.io := DontCare
}
