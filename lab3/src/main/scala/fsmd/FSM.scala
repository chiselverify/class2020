package fsmd
import chisel3._

class FSM(size :Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(size.W))
    val b = Input(UInt(size.W))
    val op: Bool = Input(Bool()) //0 for * ; 1 for /
    val aluop = Output(Bool())
    val aluoutput: UInt = Input(UInt(size.W))
    val alu_a = Output(UInt(size.W)) //input 1 to alu
    val alu_b = Output(UInt(size.W)) // input 2 to alu
    val alu_reset = Output(Bool())
    val count: UInt = Output(UInt(size.W))
    val count_in = Input(UInt(size.W))
    val en0 = Output(Bool())
    val en1 = Output(Bool())
    //val prev_aluoutput = Output(UInt((2*size).W))
    //val sel = Output(Bool())
    val done = Output(Bool())
    //val reset = Output(Bool())
  })
  //multiplication
  io.aluop := 0.B
  io.count := 0.U
  io.en0 := 1.B
  io.en1 := 1.B
  io.alu_a := 0.U
  io.alu_b := 0.U
  //io.sel := true.B
  io.alu_reset := 0.B
  io.done := 0.B
  when(io.op === false.B) {
    when(io.count_in < io.b) {
      io.alu_reset := 0.B
      io.alu_a := io.a
      //println("alua"+io.alu_a.toString)
      io.en1 := true.B
      io.en0 := true.B
      io.alu_b := io.aluoutput
      io.aluop := false.B
      //io.sel := true.B
      io.done := 0.B
      io.count := io.count_in + 1.U
    }
      .otherwise {
        io.alu_a := 0.B //io.aluoutput
        io.en0 := true.B
        io.en1 := true.B
        io.alu_b := 0.U
        io.aluop := false.B
        io.done := 1.B
        //io.sel := false.B
        io.count := 0.U(size.W)
        io.alu_reset := 1.B
      }
  }
  when(io.op === true.B) {
    when(io.count_in === 0.U) {
      io.alu_a := io.a
      io.alu_b := io.b
      io.alu_reset := 1.B
      io.aluop := true.B
      //io.sel := false.B
      io.count := io.count_in + 1.U
      io.en1 := true.B
      io.en0 := true.B
      io.done := false.B
    }
    .otherwise{
      when(io.aluoutput >= io.a){
        io.alu_a := io.aluoutput
        io.alu_b := io.b
        io.alu_reset := 0.B
        io.aluop := true.B
        //io.sel := false.B
        io.count := io.count_in + 1.U
        io.en1 := true.B
        io.en0 := true.B
        io.done := false.B
      }
      .otherwise{
        io.alu_a := 0.U
        io.alu_b := 0.U
        io.en0 := true.B
        io.en1 := true.B
        io.count := 0.U
        io.done := 1.B
        io.alu_reset := 1.B
        io.aluop := true.B
        //io.sel := true.B
      }
    }
  }
}
object FSMMain extends App {
  println("Generating the Processor hardware")
  chisel3.Driver.execute(Array("--target-dir", "generated"), () => new FSMD.FSM(16))
}
