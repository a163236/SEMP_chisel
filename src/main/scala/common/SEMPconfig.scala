package common

case class SEMPconfig(
  xlen: Int = 32,    // 汎用レジスタの長さ
  fetch_num: Int = 2,
  simulation: Boolean = false, // シミュレーションならtrue
  synthesize: Boolean = false, // 論理合成するならtrue
) {
  require(xlen >= 32)   // 汎用レジスタの長さは３２以上
  require(fetch_num >= 1) // 命令フェッチ数は1以上
  require(simulation ^ synthesize)  // 論理合成とシミュレーションは同時にtrueにもfalseにもならない

  val fetch_width = INST_WIDTH * fetch_num  // 命令フェッチのbit幅
}
