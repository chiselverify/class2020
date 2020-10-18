import chisel3._
import chisel3.util.ImplicitConversions.intToUInt
import chisel3.util._
//package popcounter
// remember
class PopCounterDP(BW: Int) extends Module {
  //BW is the bitwidth (size)

  val io = IO(new Bundle {
    val din = Input(UInt(BW.W))
    val load = Input(Bool())
    val popCnt = Output(UInt(BW.W))
    val done = Output(Bool())
  })
  val dataReg = RegInit(0.U(BW.W))
  val popCntReg = RegInit(0.U(BW.W))
  val counterReg = RegInit(0.U(BW.W))

  //dataReg := dataReg(BW-1,1) >> 1 //removes the least significant bit and concatinates with a 0 on the most significant bit
  dataReg := 0.U ## dataReg(BW-1,1)
  popCntReg := popCntReg + dataReg(0)

  val done = counterReg === 0.U
  when (!done) {
    counterReg := counterReg - 1.U
  }
  when(io.load) {
    dataReg := io.din
    popCntReg := 0.U
    counterReg := 8.U
  }

  io.popCnt := popCntReg
  io.done := done
}
