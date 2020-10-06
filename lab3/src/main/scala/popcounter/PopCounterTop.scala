import chisel3._
import chisel3.util.ImplicitConversions.intToUInt
import chisel3.util._
//package popcounter

class PopCounterTop(BW: Int) extends Module{
  val io = IO(new Bundle{
    val dinValid = Input(Bool())
    val dinReady = Output(Bool())
    val din = Input(UInt(BW.W))
    val popCntValid = Output(Bool())
    val popCntReady = Input(Bool())
    val popCnt = Output(UInt(BW.W))
  })

  val fsm = Module(new PopCounterFSM)
  val data = Module(new PopCounterDP(BW))

  fsm.io.dinValid := io.dinValid
  io.dinReady := fsm.io.dinReady
  io.popCntValid := fsm.io.popCntValid
  fsm.io.popCntReady := io.popCntReady

  data.io.din := io.din
  io.popCnt := data.io.popCnt
  data.io.load := fsm.io.load
  fsm.io.done := data.io.done

}
