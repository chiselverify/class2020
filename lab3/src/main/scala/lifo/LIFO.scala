import chisel3._
import chisel3.util._

class LIFO(size: Int, dataWidth: Int) extends Queue(size, dataWidth) {
    // An asynchronous read memory is used for storage here
    val queue = Mem(size, UInt(dataWidth.W))
    val ptr = RegInit(0.U(log2Up(size).W))
    
    // Definition of the states for the LIFO
    val empty :: nonempty :: full :: Nil = Enum(3)
    val state = RegInit(empty)
    
    // The operation of the LIFO is defined below
    switch (state) {
        is (empty) {
            when (io.enq.push) {
                queue(ptr) := io.enq.din
                state := nonempty
            }
        }
        is (nonempty) {
            when (io.flush) {
                ptr := 0.U
                state := empty
            } .elsewhen (io.deq.pop && io.enq.push) {
                queue(ptr) := io.enq.din
            } .elsewhen (io.deq.pop) {
                when (ptr === 0.U) {
                    state := empty
                } .otherwise {
                    ptr := ptr - 1.U
                }
            } .elsewhen (io.enq.push) {
                queue(ptr + 1.U) := io.enq.din
                ptr := ptr + 1.U
                when (ptr + 1.U === (size - 1).U) {
                    state := full
                }
            }
        }
        is (full) {
            when (io.flush) {
                ptr := 0.U
                state := empty
            } .elsewhen (io.deq.pop) {
                ptr := ptr - 1.U
                state := nonempty
            }
        }
    }
    io.enq.full := state === full
    io.deq.empty := state === empty
    io.deq.dout := Mux(state =/= empty, queue(ptr), 0.U)
}
