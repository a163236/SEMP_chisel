package SEMP

import chisel3._


class CPU_Core_IO extends Bundle {

}

class CPU_Core extends Module{
  val io = IO(new CPU_Core_IO)
}
