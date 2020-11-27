import chisel3._
import chisel3.util._
import concurrent._

// File for testing in Assertion with timing project
// Compiles, but simple test does not yet work
class mainOneHot extends Module {
    val io = IO(new Bundle {
        val s = Input(UInt(2.W))
        val c = Output(UInt(4.W))
    })

    

    val reg = RegInit(0.U(4.W))

    switch (io.s) {
        is ("b00".U) { reg := "b0001".U}
        is ("b01".U) { reg := "b0010".U}
        is ("b10".U) { reg := "b0100".U}
        is ("b11".U) { reg := "b1000".U}
    }

    io.c := reg
}