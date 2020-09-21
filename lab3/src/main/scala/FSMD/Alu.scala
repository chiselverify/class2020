package FSMD
import chisel3._
import chisel3.util.ImplicitConversions.intToUInt
import chisel3.util._

class Alu(size: Int) extends Module {
  val io = IO(new Bundle {
    val alu_a = Input(UInt(size.W))
    val alu_b = Input(UInt(size.W))
    val alu_reset = Input(Bool())
    val op = Input(Bool()) //0 for *;1 for /
    val aluoutput = Output(UInt((2 * size).W))
  })
  io.aluoutput := 0.U //multiplication
  when(io.alu_reset === 0.B) {
    switch(io.op) {
      is(false.B) {
        io.aluoutput := io.alu_a + io.alu_b
      }
      is(true.B) { //division
        io.aluoutput := io.alu_a - io.alu_b
      }
    }
  }
    .otherwise{
      io.aluoutput := 0.U
    }
}