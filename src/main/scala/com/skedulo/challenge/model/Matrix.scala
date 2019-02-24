package com.skedulo.challenge.model

import com.skedulo.challenge.service.Graph

import scala.io.Source
import scala.util.Random

object Matrix {
  def fromFile(path: String) = fromStringArray(Source.fromFile(path).getLines.toSeq)

  def fromStringArray(lines: Seq[String]): Matrix = {
    val data =
      lines.map(_.toCharArray.sliding(1, 2).map(k => k(0).toString).toVector).toVector

    def findPos(c: String) =
      data.zipWithIndex
        .find {
          case (line, y) => line.indexOf(c) != -1
        }
        .map {
          case (line, y) => (line.indexOf(c), y)
        }

    (for {
      source <- findPos("S")
      dest   <- findPos("O")
    } yield Matrix(data, source, dest)).getOrElse(throw new RuntimeException("File missing source or destination."))
  }

  /**
    * Generate a random Matrix of given size and obstacle density and random source and destination
    *
    * @param size Integer no smaller than 4
    * @param density Double between 0 and 1 exclusively
    */
  def random(size: Int, density: Double) = {
    val data = (0 to size - 1).map { y =>
      (0 to size - 1).map { x =>
        if (y == 0 || y == size - 1 || x == 0 || x == size - 1 || Random.nextDouble < density) "X"
        else " "
      }.toVector
    }.toVector

    val flatData      = data.flatten
    val numBlankCells = flatData.count(_ == " ")

    if (numBlankCells <= 2)
      throw new RuntimeException("Not enough blank cells for source and destination. Reduce the input density.")

    // Given an Int < numBlankCells, generate a tuple (x, y)
    // corresponding to the blank position of that Int within the matrix
    def findPos(num: Int) = {
      val index = flatData
        .foldLeft((-1, num)) {
          case ((i, n), char) =>
            if (n < 0) (i, n)
            else if (char == " ") (i + 1, n - 1)
            else (i + 1, n)
        }
        ._1

      val y = index / size
      val x = index - y * size
      (x, y)
    }

    // Randomly generate a source, destination pair within the matrix
    val sourceIndex = Random.nextInt(numBlankCells)
    val source      = findPos(sourceIndex)

    // Make sure that the destination index is different from source index
    def getDestIndex: Int = {
      val destIndex = Random.nextInt(numBlankCells)
      if (destIndex == sourceIndex) getDestIndex
      else destIndex
    }
    val dest = findPos(getDestIndex)

    // Update data matrix with source, destination pair
    val dataSD = data
      .updated(source._2, data(source._2).updated(source._1, "S"))
      .updated(dest._2, data(dest._2).updated(dest._1, "O"))

    Matrix(dataSD, source, dest)
  }
}

case class Matrix(data: Vector[Vector[String]], source: (Int, Int), destination: (Int, Int)) {
  override def toString = data.map(_.mkString(" ")).mkString("\n")

  private def toGraph = {
    val nodeMatrix = data.zipWithIndex.map {
      case (line, y) => line.zipWithIndex.map { case (c, x) => Node(x, y, c) }
    }

    // Get the list of edges from a node to adjacent unblocked nodes in the matrix
    def edges4Node(node: Node) =
      (Integer.max(0, node.y - 1) to Integer.min(nodeMatrix.size - 1, node.y + 1))
        .flatMap { yIndex =>
          (Integer.max(0, node.x - 1) to Integer.min(nodeMatrix.size - 1, node.x + 1)).map { xIndex =>
            val dest = nodeMatrix(yIndex)(xIndex)
            if (xIndex == node.x && yIndex == node.y || dest.symbol == "X") Seq.empty
            else {
              val weight = if (yIndex == node.y || xIndex == node.x) 1 else math.sqrt(2)
              Seq(Edge(node, dest, weight))
            }
          }
        }
        .flatten
        .toVector

    Graph(
      (for {
        line <- nodeMatrix
        node <- line
        if node.symbol != "X"
      } yield (node, edges4Node(node))).toMap
    )
  }

  def solve = {
    val sNode = Node(source._1, source._2, "S")
    val dNode = Node(destination._1, destination._2, "O")

    val pathAndCost = toGraph.getShortestPathAndCost(sNode, dNode)

    def withPath(prev: Node, path: List[Node], _data: Vector[Vector[String]]): Vector[Vector[String]] =
      path match {
        case Nil => _data
        case head :: tail =>
          val moveSymbol = (head.x - prev.x, head.y - prev.y) match {
            case (-1, -1) => "`"
            case (-1, 0)  => "<"
            case (-1, 1)  => ","
            case (0, -1)  => "^"
            case (0, 1)   => "V"
            case (1, -1)  => "/"
            case (1, 0)   => ">"
            case (1, 1)   => "\\"
            case _        => throw new RuntimeException("Unexpected symbol.")
          }
          withPath(head, tail, _data.updated(head.y, _data(head.y).updated(head.x, moveSymbol)))
      }

    (Matrix(withPath(Node(source._1, source._2, "S"), pathAndCost._1, data), source, destination), pathAndCost._2)
  }
}
