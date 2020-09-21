import chisel3._

abstract class RegFile(addrWidth: Int, dataWidth: Int) extends Module {
    val io = IO(new Bundle {
        // Read port 1
        val registerRs1 = Input(UInt(addrWidth.W))
        val data1 = Output(UInt(dataWidth.W))
    
        // Read port 2
        val registerRs2 = Input(UInt(addrWidth.W))
        val data2 = Output(UInt(dataWidth.W))
    
        // Write port
        val regWrite = Input(Bool())
        val registerRd = Input(UInt(addrWidth.W))
        val writeData = Input(UInt(dataWidth.W))
    })
}