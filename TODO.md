# Rule
1. The first player who received cards from gameService.createPlayer(), is the first player.
Regardless of the type (robot or human) 

# TODO
If I draw a blue card, there could be a mistake. the card might not be deleted
Problem: in SetupController
suggestion: delete the card from player.withoutcards using the index of the atomic integer. it will be more precise

If i tried to play almost 100 times, the card could have wrong colour?
suggestion: change the Colour type into only strings!