# Corgi Chess
 + Corgi Chess is a whimsical adaptation of the traditional chess game, featuring delightful cute-themed pieces and a charming 8x8 board.The objective is to checkmate the opponent's corgi king, securing victory in the realm of canine strategy. <br>

### Use of MCTS in Corgi Chess: 
While traditional alpha-beta pruning algorithms have long been the cornerstone of chess-playing programs, Corgi Chess uses Monte Carlo Tree Search (MCTS) algorithm: <br>
+ **Selective Exploration:** Corgi Chess has an incredibly large and complex game tree, making exhaustive search infeasible. MCTS selectively explores promising areas of the tree, focusing on moves that have the potential to lead to favorable outcomes. <br>
+ **Adaptability:** MCTS adapts well to the dynamic nature of chess, where the optimal strategy may change based on the current board position and opponent's moves. It can adjust its search based on the evolving state of the game. <br>
+ **Creative Exploration:** MCTS can discover and evaluate novel moves that may not be immediately apparent to traditional algorithms. This creative exploration can lead to unexpected strategies and gameplay, challenging opponents to adapt to unconventional tactics. <br>
+ **Exploration of Complex Positions:** In positions where the optimal move is not immediately obvious and requires deep strategic understanding, MCTS can excel by exploring the search space intelligently rather than relying solely on brute force.
