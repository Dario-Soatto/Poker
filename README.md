# Poker
This repository is my attempt to create an AI capable of playing poker through imitation of Pluribus, the first AI to achieve professional-level
performance in 6-player Texas Holdem poker, and the professionals against which Pluribus has played. The data containing the rounds of poker played
by Pluribus can be found in this article on Science (https://www.science.org/doi/10.1126/science.aay2400), and the data were reformated by Kevin Wang
here: http://kevinwang.us/lets-analyze-pluribuss-hands/.

The program manages to correctly predict the  actions taken by Pluribus and its professional opponents with approximately 75% accuracy.

At a very coarse level, this program sifts through data containing the rounds of poker played by Pluribus in order to gather training data.
Each training sample consists of one action by a player (note that there may be multiple actions in each hand and even in each round of betting),
with the input being some information that the player has before acting and the output being the action taken by the player.
The training samples are split into a a training set and a cross-validation set, exported as csv files, 
and used to train a simple connected feed-forward neural network. Below are explanations of the function of each file.

pluribus_30.txt
This text file includes the hands of poker used as training data for the model. Each round is formatted as follows:

PokerStars Hand #117001: Hold'em No Limit (50/100) - 2019/07/12 08:30:01 ET
Table 'Pluribus Session 117' 6-max (Play Money) Seat #6 is the button
Seat 1: MrPink (10000 in chips)
Seat 2: MrBlue (10000 in chips)
Seat 3: Joe (10000 in chips)
Seat 4: Bill (10000 in chips)
Seat 5: Pluribus (10000 in chips)
Seat 6: MrOrange (10000 in chips)
MrPink: posts small blind 50
MrBlue: posts big blind 100
*** HOLE CARDS ***
Dealt to MrPink [Th Tc]
Dealt to MrBlue [5s 6h]
Dealt to Joe [7c Jc]
Dealt to Bill [4s 4h]
Dealt to Pluribus [Jh 7h]
Dealt to MrOrange [Ks As]
Joe: folds
Bill: raises 120 to 220
Pluribus: folds
MrOrange: raises 500 to 720
MrPink: folds
MrBlue: folds
Bill: folds
Uncalled bet (500) returned to MrOrange
MrOrange collected 590.0 from pot
*** SUMMARY ***
Total pot 590 | Rake 0

The file contains 9827 hands of Poker.

pluribus_118.txt
This text file includes the hands of poker used as cross-validation data for the model formatted in the same fashion. There are 100 hands in the file.

Poker.java
This java program sifts through pluribus_30.txt and/or pluribus_118.txt to extract the actions taken by Pluribus and export as csv files the
training data or 

The inputs of each training sample are:
Pluribus's position relative to the button
The number of players left when the action is taken
The most recent bet
Pluribus's hole cards
The community cards

All of the inputs are formatted as a 2D array (a matrix) where each row contains the inputs for one action.
All of the cards are encoded as one-hot vectors with length 52, so each row has 367 elements.

The output of each training sample is a one-hot vector of length four representing the action Pluribus took:
folding [1, 0, 0, 0], checking [0, 1, 0, 0], calling [0, 0, 1, 0], and raising [0, 0, 0, 1]. 

All of the outputs are formatted as a 2D array where each row contains the output for one action.

The inputs and outputs are exported as separate csv files.

Poker2.java
This program functions similarly to Poker.java. However, rather than sifting through only the actions taken by Pluribus, this program sifts through the actions
taken by every player, thereby multiplying the amount of data by six.

Poker.ipynb
The python file imports the csv files exported by Poker.java as pandas dataframes, converts the dataframes to numpy arrays, and uses the numpy arrays
to train a feed-forward connected neural network. The file also contains a function that allows you to encode a new input for the neural network,
a program to test the accuracy of the neural network with the cross-validation data, a program to test a specific action in the cross-validation data, and
a program for the user to use an input and print out the output.

Poker2.ipynb
This python file functions similarly to Poker.ipynb but instead functions with the csv files exported by Poker2.java.




