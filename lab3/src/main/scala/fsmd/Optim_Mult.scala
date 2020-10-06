package fsmd
import chisel3._
import chisel3.util._

class Optim_Mult(size: Int) extends Module{
  val io = IO(new Bundle{
    val a = Input(UInt(size.W))
    val b = Input(UInt(size.W))
    val output = Output(UInt((2*size).W))
    val start = Input(Bool())
    val done = Output(Bool())
  })
  val prod = RegInit(0.U((2*size).W))
  io.output := 0.U
  io.done := 0.B
  when(io.start === 1.B){
    prod := 0.U
    for (i <- 0 until size){
      when (io.b(i) === 1.B){
        prod := (io.a << i).asUInt() + prod
      }
    }
    io.output := prod
    io.done := 1.B
  }
    .otherwise {
    }
}
