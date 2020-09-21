package fsmd
import chisel3._
import chisel3.util._

// a/b or a*b format
class FSMDatapath(size: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(size.W))
    val b = Input(UInt(size.W))
    val op = Input(Bool()) // 0 for multiplication ;1 for division
    val output1 = Output(UInt((2*size).W))//product;remainder
    val output2 = Output(UInt((2*size).W))//b;quotient
    val done = Output(Bool())
  })
  io.output2 := 0.U
  io.output1 := 0.U
  val reg1 = RegInit(0.U(size.W))
  val reg2 = RegInit(0.U(size.W))
  val FSM = Module(new FSM(size))
  val Alu = Module(new Alu(size))
  FSM.io.a := io.a
  FSM.io.b := io.b
  FSM.io.op := io.op
  FSM.io.aluoutput := reg1
  FSM.io.count_in := reg2
  Alu.io.alu_a := FSM.io.alu_a
  io.done := FSM.io.done
  Alu.io.alu_reset := FSM.io.alu_reset
  Alu.io.alu_b := FSM.io.alu_b
  when(FSM.io.en0 === true.B){
    reg1 := Alu.io.aluoutput
  }
  when(FSM.io.en1 === true.B){
    reg2 := (FSM.io.count)
  }
  io.output1 := reg1
  io.output2 := reg2
  Alu.io.op:=FSM.io.aluop
}
object DatapathMain extends App {
  println("Generating the Processor hardware")
  chisel3.Driver.execute(Array("--target-dir", "generated"), () => new fsmd.FSMDatapath(16))
}