package SEMP

import chiseltest._
import org.scalatest._

class temp_test extends FlatSpec with ChiselScalatestTester with Matchers {
  "temp" should "" in{
    test(new temp()){c=>
      c.clock.step(1)
    }
  }
}
