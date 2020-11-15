import org.scalatest._

class conAssertTest extends FlatSpec with Matchers {
    behavior of "concurrentAssertion"

    "concurrentAssertion" should "work plz" in{
        assertAlways(true.B, "Error", 2) should be (true.B)
    }

}