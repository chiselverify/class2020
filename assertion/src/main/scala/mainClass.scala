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

    
    val reg = RegInit(0.U (4.W))

    reg := Mux(io.s, 4.U, 0.U)
    io.c := reg
}

object main extends App{}