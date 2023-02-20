# Rule
1. The first player who received cards from gameService.createPlayer(), is the first player.
Regardless of the type (robot or human)

# TODO
- FOR THE PRESENTATION, between Ingame and UNOcard there should be some
explanation a connection between them. Make a diagram of how it is actually connected each other 


# Journal
Kassel, 18.02.2023
it took me 5 days to make sure everything goes well.
I made a test where the play could be played 1000 times to make sure nothing wrong with my logic,
and then it took long time to realize that I had some mistake within the GenModel concept 
the Link() between cards and player. I thought every player could have just the same cards.
but it will give an error, the card could be not drawn by the player
and that really took a lot of time

another thing is that every card's in Constants I set to static. this will lead
to an error within the program, especially in the test.
I have to reset all the value everytime the game plays. it is very tricky
and the card may behave not correctly, like the colour could change unexpectedly

When I tried to program the test, I notice something that even I set a specific seed
the circulation between debug and normal run are difference. it took me
a lot of time to grasp that problem.

Recursion for this project?
another problem was, i easily implement recursion
to my programm, that leads to memory leak (possibly)

Kassel, 19.02.2023
Today I just design the Maschine better in some way.
what took me a lot of time is to think how the cards can hover and how
can I simulate the stack of the pile from the robot.
That was a real challenge.

you know, I think I realised something that people who programming and
has a lot of debug like unexpected errors and catch because of test, let them think if
there is possibility that this program could be wrong. I am glad
that I have that time to think if there are mistakes in the program or not.

Test: does the class Colour worth it?
YOu know I began to believe that my Colour class should be just abolished. The reason 
is that every time I tried to test it , there might be a case that the colour cannot be 
detected. But you know what, I think the problem goes back again to the Link()
it is not being 2 ways associated. let me try to change it.

so I found that it is not really the colour class (even though yes the class is 
somewhat fragile, because it takes some memory, can lead to memory leak) but 
it was not really of that, because the test has busy waiting, that eats a lot of memory!
so, i set a Thread.sleep to make sure that FXRobot does not run simultaneously with 
JavaFX source code (espicially when the robot runs the program )



# Question
I have some questions regardless to the test. I have some randomized card.
do I have to test all action ability with in a method ( I make a lot of methods,
where I test only one action behaviour.)?

