import chisel3._
import chisel3.util.ImplicitConversions.intToUInt
import chisel3.util._
//package popcounter

class PopCounterFSM extends Module{
  val io = IO(new Bundle {
    val dinValid = Input(Bool())
    val dinReady = Output(Bool())
    val popCntValid = Output(Bool())
    val popCntReady = Input(Bool())
    val load = Output(Bool())
    val done = Input(Bool())
  })

  val idle :: count :: done :: nil = Enum(3)
  val stateReg = RegInit(idle)

  io.load := false.B
  io.dinReady := false.B
  io.popCntValid := false.B


  switch(stateReg){
    is(idle) {
      io.dinReady := true.B
      when(io.dinValid) {
        io.load := true.B
        stateReg := count
      }
    }
    is(count){
      when(io.done){stateReg := done}
    }
    is(done){
      io.popCntValid := true.B
      when(io.popCntReady) {stateReg := idle}
    }
  }
}