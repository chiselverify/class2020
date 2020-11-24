import chisel3._
import chisel3.util._

class dummyDUT extends Module {
    val io = IO(new Bundle {
    val a = Input(Bool())
    val b = Input(Bool())
    val c = Output(UInt(32.W))
    val d = Output(UInt(32.W))
    val e = Output(UInt(32.W))
})
    val reg1 = RegInit(0.U (32.W))

    val reg2 = RegInit(0.U (32.W))

        reg2 := Mux(io.a, 4.U, 0.U)
        io.c := reg2

    val reg3 = RegInit(0.U (32.W))

        reg3 := Mux(io.b, 4.U, 0.U)
        io.d := reg3

   reg1 := reg1 + 1.U
   io.e := reg1

}