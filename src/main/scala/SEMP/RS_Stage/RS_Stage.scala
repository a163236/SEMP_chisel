package SEMP.RS_Stage

import chisel3._

/*
  バッファはリングバッファではなく普通にやって、発行があればupdateする
  ウェイクアップは普通にやる
  選択論理は前方の命令からNOR論理で当該命令よりも古い命令がないかを確認する

 */

class RS_Stage_IO extends Bundle{
}



class RS_Stage extends Module{
  val io = IO(new RS_Stage_IO)

}
