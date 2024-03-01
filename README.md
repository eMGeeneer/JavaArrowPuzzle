# Arrow Puzzle from Idle Exponential

## Instructions:

The game has two modes: one with a hexagonal grid, and one with a square grid.
Each mode will have a setting for the number of directions the arrows can have. This will be either 2 or the number of sides of the grid shape (4 for square grid and 6 for hexagonal grid).

Clicking a circle will rotate all adjacent circles as well as itself

![All 6 adjacent circles that share an edge on the hexagonal grid are rotated along with the circle clicked](HexAdjacent.PNG)

![All 8 adjacent circles on the square grid are rotated along with the circle clicked](SquareAdjacent.PNG)

A hint button is available at the bottom of the screen which will locate a circle and display the number of times it needs to be rotated.
Each hint adds 10 seconds to the timer.
Clicking the hint button while a hint is already being displayed will add 20 seconds to the timer. It will also create a pop-up that prevents interaction with the game until it is dismissed.

![7 circles in a hex grid are in the 8:00 position with a hint above the central circle displaying the number 2](Hint.PNG)

There is no button to turn the music off, it is meant to be an added challenge to deal with.

[Credits to Y8MD and DOVA-SYNDROME for the bgm](https://www.youtube.com/watch?v=bNBEgmsEXII)

![baqua](baqua.jpg)

## How the code works:

### The hexagonal grid

The hexagonal grid is stored as a 2d array of ints, similar to the square grid.
However, the length of each array in the 2d array is equal to $6 \times \text{index}$ with the exception of the array at the 0 index which has a length of 1.
The indices increment going around the hexagon in a clockwise fashion.
The size of a hexagonal grid is equal to the number of rings around the central circle. 

### The hint system

The hint system works by starting the the outer most rings and reading the values to the determine how many times the indices in the lower ring have been rotated. This is more memory efficient than storing the number of times each circle has been rotated as that would $\sim + O(3n^2)$ in the hexagonal grid while adding an extra ring is only $+ O(6n)$. For the square grid is a difference of $\sim + O(\frac{1}{2}n^2)$ to $+ O(4n - 4)$.
For the outermost ring, there is another invisible ring which is the cause of the extra memory. This ring can never be rotated directly so it can only be rotated by the ring directly below it.
Because of the design of this hint system, hints are only necessary to solve the outermost ring as the lower rings can be solved with the outermost ring as a hint. This means that only a maximum of a $60n$ second penalty is required to solve each hexagonal grid and a $40n - 40$ second penalty is required for square grids where $n$ is the grid size.

### JTextBoxes and JSpinners

When directly inputting into the JTextBoxes and JSpinners (not using the arrows) pressing the `enter` key is necessary to confirm the input.

![Neko](neko.jpg)
