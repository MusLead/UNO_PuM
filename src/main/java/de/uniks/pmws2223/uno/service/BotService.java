package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Player;

import static de.uniks.pmws2223.uno.Constants.*;

public class BotService {
    public static String botDrawCard( Card card, Player bot, GameService gameService ) throws GameServiceException {
        if(!bot.getTypePlayer().equals(BOT)){
            throw new GameServiceException("YOU ARE NOT A ROBOT!");
        }
        String status = gameService.drawCard(card,bot);
        if(status.equals(NOT_VALID)){
            for (Card botCard : bot.getCards()) { //find another card!
                status = gameService.drawCard(botCard,bot);
                if(!status.equals(NOT_VALID)){
                    return status;
                }
            }
            // if the robot does not have the card at all, take a random card from GameService
            // and set a next current player to the gameService
            gameService.addRandomCard(bot);
            gameService.nextCurrentPlayer();
            return WITHDRAW;
        }
        return status;
    }
}
