# Neuroevolution Flappy Bird
Author: Harry He

## Project Description

https://user-images.githubusercontent.com/69978107/211216311-062381c6-caed-487e-93df-576954ced4d6.mp4

This application is a use of the Template Neural Network library for training neural networks to play the game Flappy Bird. By using a genetic algorithm to undergo neuroevolution, the birds will gradually improve by having more successful fitness scores be able to reproduce more. The applications supports the saving of neural networks, loading neural networks, and being able to have the player play against neural networks they train. Modifications to source code can also be made to change game properties themselves as well as customizations of bird neural networks.

Previously trained birds are provided as well, which a user can load in be adding paths in the "BirdAIGame" class in the Flappy Bird Loader similarly to as shown in comments. Training from previously trained birds is also possible by altering the file loaded in the constructor of the "FlappyBird" class in the Flappy Bird package, i.e. Change it from "AllTimeBest.txt" to another serialized neural network text file.

## Implementation Details

The application uses a neural networks with a variety of hidden layers, but is currently set to 3 hidden layers, in a genetic algorithm to select birds.

### Genetic Algorithm
1. After all birds die in a round, half of the bird population is removed as they are those that often die very early and are unfavourable to select to reproduce.
2. The score of the birds is collected which is the number of ticks that the bird survived and transformed to their fitness.
3. Birds are randomly selected based on fitness (fitness chosen is random variable), where the sum of fitness of the population is 1 and birds that contribute more fitness to the total sum will get a greater interval that can be chosen.
4. Birds repopulate based on step 3, where birds will have weights nudged by a gaussian distribution with a standard deviation of 0.1 and mean of 0. The standard deviation can be altered be adjusting the mutation rate in the call of the mutateBird function.

### Fitness Function
There are 3 fitness functions, where the fitness calculated squared is the default.
1. Regular, the calculateFitness function, calculates fitness purely based on score (time alive) given a linear advantage to higher scores.
2. Squared, the calculateFitnessSquare function, calculates fitness with the score squared given a quadratic advantage to higher scores.
3. Exponential, the calculateFitnessNatural, calculates fitness with euler's number to the score divided by 100, given exponential advantage to higher scores.

## Using The Project

### Flappy Bird (Trainer) Key Binds
Key | Function
--- | ---
X | Regular Speed
1 | 10x Speed
2 | 25x Speed
3 | 100x Speed
4 | 500x Speed
S | Save champ bird as `FirstBird.txt`
C | Print highest score and current score
I | Print number of birds alive

### Flappy Bird Loader (Game) Key Binds
Key | Function
--- | ---
X | Regular Speed
1 | 10x Speed
2 | 25x Speed
3 | 100x Speed
4 | 500x Speed
5 | 1000x Speed
6 | 10000x Speed
SPACE | Player jump
0 | Toggle AI bird immunity
MINUS | Toggle player immunity
R | Reset birds
G | List numbers of birds alive
P | Toggle view only player

## License

MIT License

Copyright (c) 2023 Harry He

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
