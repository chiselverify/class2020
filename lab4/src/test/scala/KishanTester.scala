//import chisel3._
//import chiseltest._
//import org.scalatest._
//
//object Test_param{
//  println("Enter bit width of FIFO:")
//  val bwidth : Int = 64//scala.io.StdIn.readLine.toInt
//  println("Enter Depth of FIFO:")
//  val depth : Int = 3//scala.io.StdIn.readLine.toInt
//}
//class KishanTester extends FlatSpec with ChiselScalatestTester with Matchers {
//  behavior of "The Bubble FIFO"
//
//  it should "Successfully moved data from input to output" in{
//    test(new BubbleFifo(Test_param.bwidth,Test_param.depth)){
//      dut => {
//        val inp = dut.io.enq
//        val op = dut.io.deq
//        inp.write.poke(1.B)
//        //inp.din.poke(7.U)
//        op.read.poke(0.B)
//        for(j <- 0 to 5) {
//          inp.write.poke(1.B)
//          //inp.din.poke(7.U)
//          op.read.poke(0.B)
//          inp.din.poke(j.U)
//          println("din: "+ j.toString)
//          dut.clock.step(3)
//          println("inp :" + inp.full.peek.toString)
//          inp.write.poke(0.B)
//          op.read.poke(1.B)
//          op.dout.expect(j.U((Test_param.bwidth).W))
//          dut.clock.step(3)
////          op.dout.expect(j.U((Test_param.bwidth).W))
////          dut.clock.step(3)
//        }
////        inp.write.poke(0.B)
////        op.read.poke(1.B)
////        //inp.din.poke(7.U)
////        for (i <- 0 to Test_param.depth) {
////          for (k <- 0 to Test_param.depth) {
////            println("inp :" + inp.full.peek.toString)
////            println("op :" + op.dout.peek.toString)
////            op.dout.expect(i.U((Test_param.bwidth).W))
////            dut.clock.step(1)
////          }
////        }
//      }
//    }
//  }
//}
//
