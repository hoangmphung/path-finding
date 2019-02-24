package com.skedulo.challenge
import com.skedulo.challenge.model.Matrix

object Main extends App {
  def checkSwitches(switches: List[String], options: Set[String]) = switches.forall { s =>
    s.startsWith(("--")) && options.contains(s.substring(2))
  }

  args.toList match {
    case Nil =>
      println("Usage: router COMMAND")
      println("\n  generate  :  Generate a random map")
      println("    --size MAP_SIZE  :  Size of the map (Integer, no smaller than 4")
      println("    --density OBSTACLE_DENSITY  :  Obstacle density (Decimal between 0 and 1, not including 0 or 1)")

    case "generate" :: flag1 :: val1 :: flag2 :: val2 :: Nil
        if checkSwitches(List(flag1, flag2), Set("size", "density")) =>
      // FIXME: Better pattern matching and validation
      val optMap  = Map(flag1 -> val1, flag2 -> val2)
      val size    = optMap("--size").toInt
      val density = optMap("--density").toDouble

      println(Matrix.random(size, density))

    case "solve" :: "--file" :: path :: Nil =>
      val m      = Matrix.fromFile(path)
      val solved = m.solve
      println(solved._1)
      solved._2 match {
        case None       => println("Not reachable")
        case Some(cost) => println(s"Cost: $cost")
      }

    case _ => println("Invalid command or flag. Type router for list of supported commands and flags.")
  }
}
