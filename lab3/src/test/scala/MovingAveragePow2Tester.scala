import chisel3._
import chiseltest._
import org.scalatest._

class AndreasTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The MovingAveragePow2"

    it should "Only be possible to make it with a power of 2 items" in {
        a [Exception] should be thrownBy { test(new MovingAveragePow2(-2, 1)) {_=> {}} } 
        a [Exception] should be thrownBy { test(new MovingAveragePow2(-1, 1)) {_=> {}} } 
        a [Exception] should be thrownBy { test(new MovingAveragePow2(0, 1)) {_=> {}} } 
        a [Exception] should be thrownBy { test(new MovingAveragePow2(3, 1)) {_=> {}} } 
        a [Exception] should be thrownBy { test(new MovingAveragePow2(5, 1)) {_=> {}} } 
        a [Exception] should be thrownBy { test(new MovingAveragePow2(6, 1)) {_=> {}} } 
        a [Exception] should be thrownBy { test(new MovingAveragePow2(7, 1)) {_=> {}} } 
        test(new MovingAveragePow2(1, 1)) {_=> {}}
        test(new MovingAveragePow2(2, 1)) {_=> {}}
        test(new MovingAveragePow2(4, 1)) {_=> {}}
        test(new MovingAveragePow2(8, 1)) {_=> {}}
    }

    it should "Only be possible to make it with a bit size over 0" in {
        a [Exception] should be thrownBy { test(new MovingAveragePow2(4, -1)) {_=> {}} }
        a [Exception] should be thrownBy { test(new MovingAveragePow2(4, 0)) {_=> {}} }
        test(new MovingAveragePow2(4, 1)) {_=> {}}
        test(new MovingAveragePow2(4, 2)) {_=> {}}
        test(new MovingAveragePow2(4, 3)) {_=> {}}
    }

    it should "Start out with an average of 0" in {
        test(new MovingAveragePow2(4, 1)) {
            dut => {
                dut.io.avg.expect(0.U)
                dut.clock.step(100)
                dut.io.avg.expect(0.U)
            }
        }
    }

    it should "Work when average is max value" in {
        test(new MovingAveragePow2(4, 4)) {
            dut => {
                dut.io.write.poke(true.B)
                
                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(3.U)

                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(7.U)

                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(11.U)

                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(15.U)
            }
        }
    }

    it should "Average should return to zero if only 0 is given as input" in {
        test(new MovingAveragePow2(4, 4)) {
            dut => {
                dut.io.write.poke(true.B)
                
                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(3.U)

                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(7.U)

                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(11.U)

                dut.io.din.poke(15.U)
                dut.clock.step()
                dut.io.avg.expect(15.U)

                dut.io.din.poke(0.U)
                dut.clock.step(4)
                dut.io.avg.expect(0.U)
            }
        }
    }

    it should "Easy average when each input is the same: 1" in {
        test(new MovingAveragePow2(4, 16)) {
            dut => {
                dut.io.din.poke(1.U)
                dut.io.write.poke(true.B)
                dut.clock.step(100)
                dut.io.avg.expect(1.U)
            }
        }
    }

    it should "Easy average when each input is the same: 3" in {
        test(new MovingAveragePow2(4, 16)) {
            dut => {
                dut.io.din.poke(3.U)
                dut.io.write.poke(true.B)
                dut.clock.step(100)
                dut.io.avg.expect(3.U)
            }
        }
    }

    it should "List size: 1, Inputs: 3" in {
        test(new MovingAveragePow2(1, 16)) {
            dut => {
                dut.io.write.poke(true.B)

                dut.io.din.poke(3.U)
                dut.clock.step()
                dut.io.avg.expect(3.U)
            }
        }
    }

    it should "List size: 1, Inputs: 3 2 5 0" in {
        test(new MovingAveragePow2(1, 16)) {
            dut => {
                dut.io.write.poke(true.B)

                dut.io.din.poke(3.U)
                dut.clock.step()
                dut.io.avg.expect(3.U)

                dut.io.din.poke(2.U)
                dut.clock.step()
                dut.io.avg.expect(2.U)

                dut.io.din.poke(5.U)
                dut.clock.step()
                dut.io.avg.expect(5.U)

                dut.io.din.poke(0.U)
                dut.clock.step()
                dut.io.avg.expect(0.U)
            }
        }
    }

    it should "List size: 4, Inputs: 1 1 2 2" in {
        test(new MovingAveragePow2(4, 4)) {
            dut => {
                dut.io.write.poke(true.B)

                dut.io.din.poke(1.U)
                dut.clock.step()
                dut.io.avg.expect(0.U)

                dut.io.din.poke(1.U)
                dut.clock.step()
                dut.io.avg.expect(0.U)

                dut.io.din.poke(2.U)
                dut.clock.step()
                dut.io.avg.expect(1.U)

                dut.io.din.poke(2.U)
                dut.clock.step()
                dut.io.avg.expect(1.U)
            }
        }
    }

    it should "List size: 4, Inputs: 3 7 5 9 8" in {
        test(new MovingAveragePow2(4, 4)) {
            dut => {
                dut.io.write.poke(true.B)
                
                dut.io.din.poke(3.U)
                dut.clock.step()
                dut.io.avg.expect(0.U)

                dut.io.din.poke(7.U)
                dut.clock.step()
                dut.io.avg.expect(2.U)

                dut.io.din.poke(5.U)
                dut.clock.step()
                dut.io.avg.expect(3.U)

                dut.io.din.poke(9.U)
                dut.clock.step()
                dut.io.avg.expect(6.U)

                dut.io.din.poke(8.U)
                dut.clock.step()
                dut.io.avg.expect(7.U)
            }
        }
    }
}