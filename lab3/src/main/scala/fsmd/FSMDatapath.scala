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
    val reset = Input(Bool())
  })
  //io.output2 := 0.U
  //io.output1 := 0.U
  val reg1 = RegInit(0.U(size.W))
  val reg2 = RegInit(0.U(size.W))
  val FSM = Module(new FSM(size))
  //val Reg1 = Module(new Reg(size))
  //val Reg2 = Module(new Reg(size))
  val Alu = Module(new Alu(size))
  FSM.io.a := io.a
  FSM.io.b := io.b
  FSM.io.op := io.op
  FSM.io.aluoutput := reg1
  FSM.io.reset  := io.reset
  FSM.io.count_in := reg2
  Alu.io.alu_a := FSM.io.alu_a
//  when(FSM.io.sel === true.B){
//    Alu.io.alu_b := Alu.io.aluoutput
//  }
//  .elsewhen(FSM.io.sel === false.B){
//    Alu.io.alu_b := FSM.io.alu_b
//  }
  io.done := FSM.io.done
  Alu.io.alu_reset := FSM.io.alu_reset
  Alu.io.alu_b := FSM.io.alu_b//Mux(FSM.io.sel,Alu.io.aluoutput,FSM.io.alu_b)//reg1,FSM.io.alu_b) //Alu.io.aluoutput,FSM.io.alu_b)//FSM.io.alu_b
  when(FSM.io.en0 === true.B){
    reg1 := Alu.io.aluoutput//Mux(FSM.io.sel,(Alu.io.aluoutput),FSM.io.prev_aluoutput)
  }
  when(FSM.io.en1 === true.B){
    reg2 := (FSM.io.count)
  }
  io.output1 := reg1
  io.output2 := reg2
  //Reg2.io.en := FSM.io.en1
  Alu.io.op:=FSM.io.aluop
  //Reg2.io.input := FSM.io.count
  //Reg1.io.input := Alu.io.aluoutput
  //io.output1 := Reg1.io.output
  //io.output2 := Reg2.io.output
}
object DatapathMain extends App {
  println("Generating the Processor hardware")
  chisel3.Driver.execute(Array("--target-dir", "generated"), () => new fsmd.FSMDatapath(16))
}
