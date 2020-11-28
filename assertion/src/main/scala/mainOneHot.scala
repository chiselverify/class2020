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

    when(stateReg === decode) {
      when(io.enc) {
        io.dec := false.B
        io.dout := 1.U << io.data
      }
    }.elsewhen(stateReg === encode) {
      when(io.dec) {
        io.enc := false.B
        io.dout := (1.U << 4) >> io.data
      }
    }
}