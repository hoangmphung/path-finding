package com.skedulo.challenge.model

case class Node(x: Int, y: Int, symbol: String)
case class ValueNode(node: Node, value: Double)

case class Edge(souce: Node, destination: Node, weight: Double)
