
// メモリモジュール 慶太 BRAMバージョン
module I_memory_bram #(
	parameter MEM_SIZE = 4*1024,
	parameter INST_NUM = 2,
	parameter ADDR_WIDTH = 32,
	parameter FETCH_WIDTH = 32*INST_NUM
) (
  input wire CLK,
  input wire [ADDR_WIDTH-1:0] req_addr,   // アドレス指定	32bit
  input wire req_valid,	// read enable
  output reg [FETCH_WIDTH-1:0] resp_data // 出力データ	64bit
);

  (* RAM_STYLE="BLOCK" *) reg [FETCH_WIDTH-1:0] mem [0: MEM_SIZE - 1];	// 32bit * size

	initial begin
		$readmemh("memory.mem", mem);
	end

	// 読む
	always @(posedge CLK) begin
		// read のみ
		if(req_valid) resp_data <= {mem[req_addr[ADDR_WIDTH-1:2]+1], mem[req_addr[ADDR_WIDTH-1:2]]}; 	//
		else resp_data <= 0;
	end

endmodule


