import chisel3._
import chisel3.util._
import concurrent._

// File for testing in Assertion with timing project
// Compiles, but simple test does not yet work
class mainClass extends Module {
    val io = IO(new Bundle {
        val s = Input(UInt(4.W))
        val c = Output(UInt(4.W))
    })

    io.c := io.s

    //conAssert(true.B, "Error", 10)
}

object main extends App{}