import chisel3._
import chisel3.util._
import concurrent._

// File for testing in Assertion with timing project
// Compiles, but simple test does not yet work
class mainClass extends Module {
    val io = IO(new Bundle {
        val s = Input(Bool())
        val c = Output(UInt(4.W))
    })

    val a = 3.U
    val b = 7.U
    when(io.s) {
        io.c := a
    } .otherwise {
        io.c := b
    }
    conAssert(true.B, "Error", 10)
}

object main extends App{}