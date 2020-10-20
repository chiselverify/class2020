package fsmd
import chisel3._
import chisel3.util._

class Optim_Mult(size:Int) extends Module{
  val io = IO(new Bundle{
        val input1 = Input(UInt(size.W))
        val input2 = Input(UInt(size.W))
        val output = Output(UInt((2*size).W))
        val start = Input(Bool())
        val done = Output(Bool())
      })

  val product = RegInit(0.U((2*size).W))
  val input1 = RegInit(0.U((2*size).W))
  val input2 = RegInit(0.U(size.W))
  val start :: computation :: done :: Nil = Enum(3)
  val stateReg = RegInit(start)
  val count = RegInit(0.U(size.W))
  io.output := 0.U
  io.done :=0.B
  when(io.start === 1.B){
    input1 := io.input1
    input2 := io.input2
    stateReg := computation
    product:=0.U
    io.output := 0.U
    io.done :=0.B
  }
  switch(stateReg){
    is( computation ){
      when(input2(0) === 1.U){
        product := product + input1
        input1:=Cat(input1,0.U(1.W))
      }
        .otherwise{
          product:=product
        }
      input1:=Cat(input1,0.U(1.W))
      input2:=Cat(0.U(1.W),input2(size-1,1))
      when(input2===0.U){
        stateReg := done
      }
    }
    is(done){
      io.output:=product
      io.done:=1.B
    }
  }
}
