package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Player;

import static de.uniks.pmws2223.uno.Constants.*;

public class BotService {
    /**
     * This function let the robot draw a first card that the robot has.
     * Otherwise, it will try to find another suitable card in the list.
     * if there is no card at all, then withdraw from the game.
     *
     * in case of the robot wants to draw a wildcard, it will always try
     * to draw a blue wildcard.
     * @param bot the bot who plays the game
     * @param gameService current game service that is being used
     * @return the drawn card
     * @throws GameServiceException if the bot is actually not a robot, a human!
     */
    public static String botDrawCard( Player bot, GameService gameService ) throws GameServiceException {
        Card card = bot.getCards().get(0);
        if(!bot.getTypePlayer().equals(BOT)){
            throw new GameServiceException("YOU ARE NOT A ROBOT!");
        }
        String status = gameService.drawCard(card,bot);
        if(status.equals(NOT_VALID)){
            /*
             the card that is being drawn is not valid, might be because the card is
             not suitable for the deck pile (wrong colour, symbol, or even number)
             therefore, find another card, and try to draw the card. */

            for (Card botCard : bot.getCards()) { //goes through all the card to find another card!
                if(botCard.getName().contains(gameService.getConstants().WILDCARD.getName())){
                    botCard.setColour(BLUE);
                }
                status = gameService.drawCard(botCard,bot);
                if(!status.equals(NOT_VALID)){ //found a card!
                    return status;
                }
            }
            // if the robot does not have the card at all, take a random card from GameService
            // and set a next current player to the gameService
            try {
                gameService.addRandomCard(bot);
            } catch (StackOverflowError se){
                System.err.println("(BotService.java:45) " + se.getLocalizedMessage());
                throw new StackOverflowError(se.getLocalizedMessage());
            } catch (GameServiceException e) {
                System.err.println("(GameService.java:45) " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
            gameService.nextCurrentPlayer();
            return WITHDRAW;
        }
        // By the first time the robot draw the card, and found a suitable card for the deck pile
        // return the drawn card!
        return status;
    }
}
