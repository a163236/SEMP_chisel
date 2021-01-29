package common

trait config{

  val ADDR_WIDTH = 32
  val FETCH_NUM = 2
  val FETCH_WIDTH = ADDR_WIDTH * FETCH_NUM

  val RS1_MSB = 19  // instからレジスタ1への最上位ビット
  val RS1_LSB = 15  // instからレジスタ1への最下位ビット
  val RS2_MSB = 24
  val RS2_LSB = 20
  val RD_MSB  = 11  // 書き込みレジスタアドレス
  val RD_LSB  = 7
  val CSR_ADDR_MSB = 31
  val CSR_ADDR_LSB = 20

  val START_ADDR = "h00000000"  // MTVEC + 0x100 ?

}