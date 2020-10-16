import chisel3._
import chiseltest._
import org.scalatest._

object Test_param{
  println("Enter bit width of FIFO:")
  val bwidth : Int = scala.io.StdIn.readLine.toInt
  println("Enter Depth of FIFO:")
  val depth : Int = scala.io.StdIn.readLine.toInt
}
class KishanTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of Test_param.bwidth + "-bit Bubble FIFO with depth " + Test_param.depth
  it should "Successfully move data from input to output" in {
    test(new BubbleFifo(Test_param.bwidth, Test_param.depth)) {
      dut => {
        val inp = dut.io.enq
        val op = dut.io.deq
        inp.write.poke(1.B)
        //inp.din.poke(7.U)
        op.read.poke(0.B)
        var j = 0
        //----------Poke stream----------
        for (j <- 0 until (Test_param.depth)) {
          inp.write.poke(1.B)
          op.read.poke(0.B)
          inp.din.poke(j.U)
          dut.clock.step(1)
          dut.clock.step(1)
        }
        //-----------Read stream----------
        for (j <- 0 until (Test_param.depth)) {
          inp.write.poke(0.B)
          op.read.poke(1.B)
          op.dout.expect(j.U((Test_param.bwidth).W))
          dut.clock.step(1)
          dut.clock.step(1)
        }
      }
    }
  }
  it should "Successfully Reset" in {
    test(new BubbleFifo(Test_param.bwidth, Test_param.depth)) {
      dut => {
        val inp = dut.io.enq
        val op = dut.io.deq
        inp.write.poke(1.B)
        //inp.din.poke(7.U)
        op.read.poke(0.B)
        var j = 0
        for (j <- 0 until (Test_param.depth)) {
          inp.write.poke(1.B)
          //inp.din.poke(7.U)
          op.read.poke(0.B)
          inp.din.poke(1.U)
          println("din: " + j.toString)
          //println("output: "+op.dout.peek.toString)
          dut.clock.step(1)
          dut.clock.step(1)
        }
        //-----------Reset poke----------
        dut.reset.poke(1.B)
        dut.clock.step(1)
        dut.reset.poke(0.B)
        dut.clock.step(1)
        //-----------Emtpy Signal Poke Test----------
        op.notReady.expect(1.B)
      }
    }
  }
  it should "Successfully Indicate Full and Empty Queue" in {
    test(new BubbleFifo(Test_param.bwidth, Test_param.depth)) {
      dut => {
        val inp = dut.io.enq
        val op = dut.io.deq
        for (j <- 0 until (Test_param.depth)) {
          inp.write.poke(1.B)
          //inp.din.poke(7.U)
          op.read.poke(0.B)
          inp.din.poke(1.U(67.W))
          println("din: " + j.toString)
          dut.clock.step(1)
          dut.clock.step(1)
        }
        dut.io.enq.busy.expect(1.B)
        for (j <- 0 until (Test_param.depth)) {
          inp.write.poke(0.B)
          op.read.poke(1.B)
          println("output : " + op.dout.peek)
          dut.clock.step(1)
          dut.clock.step(1)
        }
        op.notReady.expect(1.B)
      }
    }
  }
  it should "Use out of Bounds value" in {
    test(new BubbleFifo(8, 4)) {
      dut => {
        val inp = dut.io.enq
        val op = dut.io.deq
        for (j <- 0 until 4) {
          inp.write.poke(1.B)
          //inp.din.poke(7.U)
          op.read.poke(0.B)
          //Poking IntMaxValue = 2147483647
          println(Int.MaxValue)
          inp.din.poke(Int.MaxValue.U)
          dut.clock.step(1)
          dut.clock.step(1)
        }
        dut.io.enq.busy.expect(1.B)
        //8 bit so max value is 255
        for (j <- 0 until 4) {
          inp.write.poke(0.B)
          op.read.poke(1.B)
          op.dout.expect(255.U)
          dut.clock.step(1)
          dut.clock.step(1)
        }
        op.notReady.expect(1.B)
      }
    }
  }
  it should "Successfully Operate at Maximum Throughput" in {
    test(new BubbleFifo(Test_param.bwidth,Test_param.depth)) {
      dut => {
        var i =0
        val inp = dut.io.enq
        val op = dut.io.deq
        var j = 0
        while(j < 5 * Test_param.depth){
          println("j =" + j)
          if(Test_param.depth % 2 == 1 || i < Test_param.depth - 1 ) { // For odd-depth fifo and even-depth FIFO till it gets full
            j = j + 1
            inp.din.poke(j.U)
            inp.write.poke(1.B)
            op.read.poke(0.B)
            println("Full is " + inp.busy.peek)
            //if(j==1) dut.clock.step(2*(Test_param.depth - 1))
            dut.clock.step(1)
            i = i + 1
            println(i)
            println("Empty is " + op.notReady.peek)
          }
          if(Test_param.depth % 2 == 0 && i<Test_param.depth-1){
            inp.write.poke(0.B)
            op.read.poke(1.B)
            //inp.din.poke(j.U)
            dut.clock.step(1)
            i = i + 1
          }
          //--Poke Expect Test--
          else {
            //val expval = j.U - Test_param.depth.U
            //op.dout.expect()
            //op.notReady.expect(1.B)
            if (Test_param.depth % 2 == 0 && i == Test_param.depth - 1) {
              inp.write.poke(0.B)
              inp.din.poke(j.U)
              op.read.poke(0.B)
              dut.clock.step(1)
              i = i + 1
            }
            if(Test_param.depth %2 ==0 && i > Test_param.depth - 1) {
              j = j + 1
              inp.din.poke(j.U)
              inp.write.poke(1.B)
              op.read.poke(1.B)
              op.dout.expect((j - ((Test_param.depth / 2))).U)
              dut.clock.step(2)
              i = i + 2
              j= j + 1
              inp.din.poke(j.U)
              inp.write.poke(1.B)
              op.read.poke(1.B)
              op.dout.expect((j - ((Test_param.depth / 2))).U)
              dut.clock.step(2)
              //println(i)
              println(i)
              }
            if(Test_param.depth % 2 == 1 && i<Test_param.depth-1){
              inp.write.poke(0.B)
              op.read.poke(1.B)
              dut.clock.step(1)
              i = i + 1
              println(i)
            }
            if(Test_param.depth %2 ==1 && i > Test_param.depth - 1){
              inp.write.poke(0.B)
              op.read.poke(1.B)
              op.dout.expect((j - (Test_param.depth / 2)).U)
              dut.clock.step(1)
              i = i + 1
            }
            }
        }
      }
    }
  }
}
