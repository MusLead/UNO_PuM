package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.model.*;

import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static java.lang.Math.*;

public class GameService {
    private final Random random;
    private DrawPile drawPile = new DrawPile();

    private String direction = RIGHT_WAY;

    public GameService(){
        this.random = new Random();
        int randomIndex = abs(random.nextInt() % ALL_CARDS.length);
        this.drawPile.setCurrentCard(ALL_CARDS[randomIndex]);
    }

    /**
     * this constructor should be used if the random has seed
     * @param random with seed
     */
    public GameService( Random random ){
        this.random = random;
        int randomIndex = abs(random.nextInt() % ALL_CARDS.length);
        this.drawPile.setCurrentCard(ALL_CARDS[randomIndex]);
    }

    /**
     * this constructor should be used if random has seed and
     * a spesific card on a drawPile on start
     * @param random with seed for spreading 7-randomize card to the player on start
     * @param drawPile a specific card on the draw pile on start
     */
    public GameService( Random random, DrawPile drawPile){
        this.random = random;
        this.drawPile = drawPile;
    }

    public Player createPlayer( TypePlayer typePlayer, String name ) {
        Player player = new Player().setTypePlayer(typePlayer).setName(name).setDrawPile(this.drawPile);
        for (int i = 0; i < 7; i++) {
            addRandomCard(player);
        }
        System.out.println(player.getName() + " : " +player.getCards());
        return player;
    }

    protected void addRandomCard( Player player ) {
        int randomIndex = abs(random.nextInt() % ALL_CARDS.length);
        player.withCards(ALL_CARDS[randomIndex]);
    }

    /**
     * this function gives a possibility for the player to draw a card to
     * drawPile. the player who draws the card must be the currentPlayer
     * in drawPile, otherwise it will throw an Exception
     * @param card that will be put on the drawPile
     * @param player who draws the card
     * @return  the cards that has been drawn, otherwise NOT_VALID
     * @throws GameServiceException
     */
    public String drawCard(Card card, Player player) throws GameServiceException {

        if(drawPile.getCurrentPlayer() == null) {
            /*
             * it means, at start of the play there is no Player who draws the card.
             * By default, set the player as the current Player
             */
            drawPile.setCurrentPlayer(player);
        } else if(!drawPile.getCurrentPlayer().getName().equals(player.getName())) {
            /*
             * if the currentPlayer is not null, check if the currentPlayer is the same as the
             * player who draws the card, otherwise decline the player!
             */
            throw new GameServiceException("NOT THE RIGHT PLAYER");
        }

        Card drawPileCard = drawPile.getCurrentCard();

        if(drawPileCard.getName().equals(card.getName())
            || drawPileCard.getColour().getName().equals(card.getColour().getName())
                || (drawPileCard.getNumber() != 0 && card.getNumber() != 0 &&
                drawPileCard.getNumber() == card.getNumber())){
            // if successful then draw a card
            drawPile.setCurrentCard(card);
            player.withoutCards(card);

            if(drawPileCard.getName().contains(SKIP)){
                nextCurrentPlayer();
                nextCurrentPlayer();
                return SKIP;
            } else if(drawPileCard.getName().contains(REVERSE)){
                direction = direction.equals(RIGHT_WAY) ? LEFT_WAY : RIGHT_WAY;
                nextCurrentPlayer();
                return REVERSE;
            } else if(drawPileCard.getName().contains(DRAW_TWO)) {
                nextCurrentPlayer();
                addRandomCard(drawPile.getCurrentPlayer());
                addRandomCard(drawPile.getCurrentPlayer());
                return DRAW_TWO;
            }

            return drawPileCard.getName();
        }

        // if the card is wrong
        System.err.println("NOT THE RIGHT CARD! drawPile: " + drawPileCard +", "+ drawPileCard.getColour()
                                               + " | "+ player.getName() + " draws: " + card);
        return NOT_VALID;
    }

    protected void nextCurrentPlayer() throws GameServiceException {
        int index = getIndexCurrentPlayer(drawPile.getCurrentPlayer());

        if(direction.equals(RIGHT_WAY)){
            int nextIndex = (index + 1) % drawPile.getPlayers().size();
            Player nextPlayer = drawPile.getPlayers().get(nextIndex);
            drawPile.setCurrentPlayer(nextPlayer);
        } else if(direction.equals(LEFT_WAY)){
            int nextIndex = (index - 1) % drawPile.getPlayers().size();
            Player nextPlayer = drawPile.getPlayers().get(nextIndex);
            drawPile.setCurrentPlayer(nextPlayer);
        } else {
            throw new GameServiceException("INVALID DIRECTION");
        }
    }

    private int getIndexCurrentPlayer( Player currentPlayer ) {
        int index = 0;
        for (Player drawPilePlayer : drawPile.getPlayers()) {
            if(drawPilePlayer.getName().equals(currentPlayer.getName())){
                break;
            }
            index++;
        }
        return index;
    }

    public int findIndexCards(String card, Player player){
        int index = 0;
        for (Card playerCard : player.getCards()) {
            if(playerCard.getName().equals(card)) {
                return index;
            }
            index++;
        }
        return -1; // the card is not found!
    }

}
