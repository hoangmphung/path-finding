package com.skedulo.challenge

import com.skedulo.challenge.model.Matrix
import org.scalatest.FreeSpec

class MatrixSpec extends FreeSpec {
  "solve" - {
    "reachable case" in {
      val input =
        """
          |X X X X X X X X X X
          |X       X X   X   X
          |X   X     X       X
          |X S X X       X   X
          |X   X     X       X
          |X       X X   X   X
          |X   X     X   X   X
          |X   X X       X   X
          |X     O   X       X
          |X X X X X X X X X X
        """.stripMargin

      val inputMatrix = Matrix.fromStringArray(input.trim.split("\n"))
      val result      = inputMatrix.solve
      assert(math.abs(result._2.getOrElse(0.0) - 6.4142) < 0.0001)
    }

    "unreachable case" in {
      val input =
        """
          |X X X X X X X X X X
          |X X               X
          |X X X X X   X X   X
          |X X   X   O   X X X
          |X X X       X X X X
          |X X X X X       X X
          |X   S   X   X   X X
          |X     X X X   X X X
          |X X X X           X
          |X X X X X X X X X X
        """.stripMargin

      val inputMatrix = Matrix.fromStringArray(input.trim.split("\n"))
      val result      = inputMatrix.solve
      assert(result._2.isEmpty)
    }
  }
}
