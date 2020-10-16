/*
 * Author: Martin Schoeberl (martin@jopdesign.com)
 *
 * A simple bubble FIFO
 *
 */

import chisel3._
import chisel3.util._

/*
 * On signal naming:
 * 
 * Alter's FIFO component:
 * 
 * data - data in, q - data out, wrreq and rdreq
 * state: full and empty
 * 
 * Xilinx's FIFO component:
 * din and dout, wr_en, rd_en
 * state: full and empty
 * 
 */

class WriterIO(size: Int) extends Bundle {
  val write = Input(Bool())
  val busy = Output(Bool())
  val din = Input(UInt(size.W))
}

class ReaderIO(size: Int) extends Bundle {
  val read = Input(Bool())
  val notReady = Output(Bool())
  val dout = Output(UInt(size.W))
}


/**
 * A single register (=stage) to build the FIFO.
 */
class FifoRegister(size: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })

  val empty :: full :: Nil = Enum(2)
  val stateReg = RegInit(empty)
  val dataReg = RegInit(0.U(size.W))

  when(stateReg === empty) {
    when(io.enq.write) {
      stateReg := full
      dataReg := io.enq.din
    }
  }.elsewhen(stateReg === full) {
    when(io.deq.read) {
      stateReg := empty
      dataReg := 0.U // just to better see empty slots in the waveform
    }
  }.otherwise {
    // There should not be an otherwise state
  }

  io.enq.busy := (stateReg === full)
  io.deq.notReady := (stateReg === empty)
  io.deq.dout := dataReg
}
/**
 * This is a bubble FIFO.
 */
class BubbleFifo(size: Int, depth: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })

  val buffers = Array.fill(depth) { Module(new FifoRegister(size)) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq.din := buffers(i).io.deq.dout
    buffers(i + 1).io.enq.write := ~buffers(i).io.deq.notReady
    buffers(i).io.deq.read := ~buffers(i + 1).io.enq.busy
  }
  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}