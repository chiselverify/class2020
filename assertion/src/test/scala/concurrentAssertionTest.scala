import org.scalatest._
import assertionTiming._
import chisel3._
import chiseltest._

class concurrentAssertionTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The mainClass"

// assertAlways
  it should "test assertAlways" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      }
    }
  }

  it should "test that assertAlways fails when poking false" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        val t = assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(10)
        dut.io.s.poke(false.B)
        dut.clock.step(1)
      }
    }
  }

  it should "test time depency of clock cycles" in {
    test(new mainClass()) {
      dut => {
        
        // Test of assertAlways
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        assertAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(21)
        dut.io.s.poke(false.B)
        dut.clock.step(1)

        // Test of assertNever
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 40)
      
        dut.clock.step(41)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
      }
    }
  }

// assertNever
  it should "test assertNever" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      }
    }
  }

  it should "test assertNever fails" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertNever(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)
      
        dut.clock.step(15)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
      }
    }
  }

  //assertEventually
  it should "test assertEventually passes, once true regardless of what happens next" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventually(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        dut.clock.step(5)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        dut.io.s.poke(false.B)
        t.join
      }
    }
  }

  it should "test assertEventually fails when exceeding clock cycles" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventually(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        t.join
      }
    }
  }

  // assertEventuallyAlways
  it should "test assertEventuallyAlways pass" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventuallyAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        dut.clock.step(1)
        dut.io.s.poke(true.B)

        t.join
      }
    }
  }

  it should "fail, if cond does not hold true once true" in {
    test(new mainClass()) {
      dut => {
        
        dut.io.s.poke(false.B)
        dut.clock.step(1)
        val t = assertEventuallyAlways(dut, () => dut.io.c.peek.litValue == 4, "Error", 20)

        dut.clock.step(5)
        dut.io.s.poke(true.B)
        dut.clock.step(1)
        dut.io.s.poke(false.B)

        t.join
      }
    }
  }
}

/*class concurrentAssertionTest2 extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The oneHot"
  // assertOneHot
  it should "pass if only one bit is high" in {
    test(new oneHot()) {
      dut => {
        

      }
    }
  }
}*/