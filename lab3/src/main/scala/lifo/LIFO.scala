

import chisel3._
import chisel3.util._

class LIFO(size: Int, dataWidth: Int) extends Queue(size, dataWidth) {
    // An asynchronous read memory is used for storage here
    val queue = Mem(size, UInt(dataWidth.W))
    val ptr = RegInit(0.U(log2Ceil(size+1).W))
    
    // Definition of the states for the LIFO
    val empty :: filling :: full :: Nil = Enum(3)
    val stateReg = RegInit(empty)
    
    // The operation of the LIFO is defined below
    switch (stateReg) {
        is (empty) {
            when (io.enq.push) {
                queue(ptr) := io.enq.din
                stateReg := filling
            }
        }
        is (filling) {
            when (io.deq.pop && io.enq.push) {
                queue(ptr) := io.enq.din
            } .elsewhen (io.deq.pop) {
                when (ptr === 0.U) {
                    stateReg := empty
                } .otherwise {
                    ptr := ptr - 1.U
                }
            } .elsewhen (io.enq.push) {
                queue(ptr + 1.U) := io.enq.din
                ptr := ptr + 1.U
                when (ptr + 1.U === (size - 1).U) {
                    stateReg := full
                }
            }
        }
        is (full) {
            when (io.deq.pop) {
                ptr := ptr - 1.U
                stateReg := filling
            }
        }
    }
    io.enq.full := stateReg === full
    io.deq.empty := stateReg === empty
    io.deq.dout := Mux(stateReg =/= empty, queue(ptr), 0.U)
}
