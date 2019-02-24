package com.skedulo.challenge.service

import com.skedulo.challenge.model.{Edge, Node, ValueNode}

import scala.annotation.tailrec
import scala.collection.immutable.TreeSet
import scala.collection.mutable
import scala.util.control.Breaks._

case class Graph(adjacencyList: Map[Node, Vector[Edge]]) {
  def getShortestPathAndCost(source: Node, destination: Node): (List[Node], Option[Double]) = {
    implicit val ordering = new Ordering[ValueNode] {
      override def compare(a: ValueNode, b: ValueNode): Int =
        Ordering.Double.compare(b.value, a.value)
    }

    val costMap = mutable.Map((source, 0.0))

    val pq = mutable.PriorityQueue.empty[ValueNode]
    pq.enqueue(ValueNode(source, 0))

    val spt     = mutable.Set.empty[Node]
    val prevMap = mutable.Map.empty[Node, Node]

    breakable {
      while (!pq.isEmpty) {
        val p = pq.dequeue()
        if (p.node == destination) break
        spt.add(p.node)
        adjacencyList
          .get(p.node)
          .toList
          .flatten
          .filter { edge =>
            !spt.contains(edge.destination)
          }
          .foreach { edge =>
            val dest            = edge.destination
            val newCost: Double = p.value + edge.weight
            if (newCost < costMap.get(dest).getOrElse(Double.PositiveInfinity)) {
              costMap.put(dest, newCost)
              pq.enqueue(ValueNode(dest, newCost))
              prevMap.put(dest, p.node)
            }
          }
      }
    }

    @tailrec
    def buildPath(path: List[Node], currentNode: Node): List[Node] =
      prevMap.get(currentNode) match {
        case None                   => List.empty
        case Some(n) if n == source => path
        case Some(n)                => buildPath(n +: path, n)
      }

    (buildPath(List[Node](destination), destination), costMap.get(destination))
  }
}
