package common

import chisel3._

case class SEMPconfig(
                       xlen: Int = 32, // 汎用レジスタの長さ
                       fetch_num_param: Int = 2,
                       thread_num_param: Int = 2,
                       simulation: Boolean = false, // シミュレーションならtrue
                       synthesize: Boolean = false, // 論理合成するならtrue
) {
  require(xlen >= 32)   // 汎用レジスタの長さは３２以上
  require(fetch_num_param >= 1) // 命令フェッチ数は1以上
  require(thread_num_param >= 1)  // スレッド数 1以上
  require(simulation ^ synthesize)  // 論理合成とシミュレーションは同時にtrueにもfalseにもならない

  val fetch_width = INST_WIDTH * fetch_num_param  // 命令フェッチのbit幅
  val thread_width = thread_num_param.U.getWidth  // スレッド数のbit幅
}
