# Signal-Flow-Graph-Solver
Signal Flow Graph Solver is a GUI program that represents signal flow graph of different control systems, calculates the overall transfer function and shows it's steps through an interactive user interface.

##### Abstract
- A signal-flow graph or signal-flowgraph (SFG) is a specialized flow graph, a directed graph in which nodes represent system variables, and branches (edges, arcs, or arrows) represent functional connections between pairs of nodes. Thus, signal-flow graph theory builds on that of directed graphs, which includes as well that of oriented graphs.
Mason’s gain formula is `T = C(s)/R(s) = SIGMA(Pi * Δi)/Δ`
`C(s)` is the output node
`R(s)` is the input node
`T` is the transfer function or gain between `R(s)` and `C(s)`
`Pi` is the ith forward path gain
`Δ=1−(sum of all individual loop gains) +(sum of gain products of all possible two non-touching loops) −(sum of gain products of all possible three non-touching loops)+... `
`Δi` is obtained from `Δ` by removing the loops which are touching the ith forward path.

##### Main Features of The Program
- Interactive GUI 
- Complete analysis of the system includes the following procedures:
    * Detecting all forward paths, with the corresponding gains.
    * Detecting all loops, with the corresponding gains.
    * Detecting and Grouping all non-touching loops.
    * Detecting and Grouping all non-touching loops with all forward paths.
    * Calculating `𝜟`.
    * Calculating `𝜟i`, where i = 1, 2, ...m, as mentioned above.
    * Calculating the overall system transfer function `T`.
##### Screenshots
![alt text](https://github.com/AhmedZahRan7/Signal-Flow-Graph-Solver/blob/master/Screenshot1.jpg?raw=true)
![alt text](https://github.com/AhmedZahRan7/Signal-Flow-Graph-Solver/blob/master/Screenshot2.jpg?raw=true)
![alt text](https://github.com/AhmedZahRan7/Signal-Flow-Graph-Solver/blob/master/Screenshot3.jpg?raw=true)
![alt text](https://github.com/AhmedZahRan7/Signal-Flow-Graph-Solver/blob/master/Screenshot4.jpg?raw=true)
