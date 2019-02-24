# Programming Challenge
This is a solution to a programming challenge: [https://docs.google.com/document/d/1O8OpciNPNJ0MAWyUWAu55W1irLTNSd_9Vs974Wcg0hc/edit?usp=sharing]() It is written in Scala.

To run this, start sbt in the base directory and type `run COMMAND`

Supported commands:
```
* generate --size SIZE --density DENSITY
Generate a random map with the given size and obstacle density. Source and destination are also randomly placed.

  SIZE: Size of the map (Integer, no smaller than 4")
  DENSITY: Obstacle density (Decimal between 0 and 1, not including 0 or 1)

* solve --file PATH
Find the shortest path and cost between the source and destination in the map in the given file.

  PATH: File path
```

Sample input:
```
X X X X X X X X X X
X       X X   X   X
X   X     X       X
X S X X       X   X
X   X     X       X
X       X X   X   X
X   X     X   X   X
X   X X       X   X
X     O   X       X
X X X X X X X X X X
```

Sample output:
```
X X X X X X X X X X
X       X X   X   X
X   X     X       X
X S X X       X   X
X V X     X       X
X V     X X   X   X
X V X     X   X   X
X V X X       X   X
X   \ >   X       X
X X X X X X X X X X
Cost: 6.414213562373095
```