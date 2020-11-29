import chisel3._
import chisel3.util._
import concurrent._

// File for testing in Assertion with timing project
// Compiles, but simple test does not yet work
class mainOneHot extends Module {
    val io = IO(new Bundle {
        val data = Input(UInt(4.W))
        val enc = Input(Bool())
        val dec = Input(Bool())
        val dout = Output(UInt(4.W))
    })

    val decode :: encode :: Nil = Enum(2)
    val stateReg = RegInit(decode)

    io.dout := 0.U

    when(stateReg === decode) {
      io.dout := 1.U << io.data
      when(io.enc) {
        stateReg := encode
      }
    }. elsewhen (stateReg === encode) {
      switch (io.data) {
        is ("b0001".U) { io.dout := "b00".U}
        is ("b0010".U) { io.dout := "b01".U}
        is ("b0100".U) { io.dout := "b10".U}
        is ("b1000".U) { io.dout := "b11".U}
      }
      when(io.dec) {
        stateReg := decode
      }
    }. otherwise {
    }
}
