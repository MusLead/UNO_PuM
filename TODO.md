# Rule
1. The first player who received cards from gameService.createPlayer(), is the first player.
Regardless of the type (robot or human)

# TODO
- clean up the code
- think again about the necessary test!
- add some explanation every method including Ingame!
- change object and class diagram into currentDiscardPile!
- change draw a card to place a card, because they have different definiton!

# Journal
Kassel, 18.02.2023
it took me 5 days to make sure everything goes well.
I made a test where the play could be played 1000 times to make sure nothing wrong with my logic,
and then it took long time to realize that I had some mistake within the GenModel concept 
the Link() between cards and player. I thought everyone could have just the same cards.
but it will give an error, the card could be not drawn by the player
and that really took a lot of time

another thing is that every card's in Constants I set to static. this will lead
to an error within the program, especially in the test.
I have to reset all the value everytime the game plays. it is very tricky
and the card may behave not correctly, like the colour could change unexpectedly

When I tried to program the test, I notice something that even I set a specific seed
the circulation between debug and normal run are difference. it took me
a lot of time to grasp that problem.

Kassel, 19.02.2023
Today i just design the Maschine better in some way.
what took me a lot of time is to think how the cards can hover and how
can i simulate the stack of the pile from the robot.
That was a real challenge.

I have some questions regardles to the test. I have some randomized card.
do i have to test all action ability with in a method ( i make a lot of methods,
where i test only one action behaviour.)?