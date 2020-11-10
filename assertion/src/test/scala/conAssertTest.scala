import chisel3._
import chiseltest._
import org.scalatest._

class conAssertTest extends FlatSpec with ChiselScalatestTester with Matchers {
    behavior of "The conAssert"

    it should "test" in {
        test(new conAssert(true.B, 10, "Halløjsa")) {
            dut => {
                dut.io.sigA.poke(true.B)
                dut.clock.step(1)
                dut.io.testp.expect(true.B)
            }
        }
    }
}