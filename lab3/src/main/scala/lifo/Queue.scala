import chisel3._

class ProdIO(dataWidth: Int) extends Bundle {
    val push = Input(Bool())
    val din = Input(UInt(dataWidth.W))
    val full = Output(Bool())
}

class ConsIO(dataWidth: Int) extends Bundle {
    val pop = Input(Bool())
    val dout = Output(UInt(dataWidth.W))
    val empty = Output(Bool())
}

abstract class Queue(size: Int, dataWidth: Int) extends Module {
    val io = IO(new Bundle {
        val flush = Input(Bool())
        val enq = new ProdIO(dataWidth)
        val deq = new ConsIO(dataWidth)
    })
}