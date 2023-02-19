package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static java.lang.Math.*;

public class GameService {
    private final Random random;
    private final Constants constants = new Constants();
    private final Encounter encounter = new Encounter();

    public Constants getConstants() {
        return constants;
    }

    private String direction = RIGHT_WAY;

    public String getDirection() {
        return direction;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public GameService(){
        this.random = new Random();
        int randomIndex = abs(random.nextInt() % constants.ALL_CARDS.length);
        this.encounter.setCurrentCard(constants.ALL_CARDS[randomIndex]);
        if(this.encounter.getCurrentCard().equals(constants.WILDCARD)){
            this.encounter.getCurrentCard().setColour(BLUE);
        }
    }

    /**
     * This constructor should be used if the random has seed
     * @param random with seed
     */
    public GameService( Random random ){
        this.random = random;
        int randomIndex = abs(random.nextInt() % constants.ALL_CARDS.length);
        this.encounter.setCurrentCard(constants.ALL_CARDS[randomIndex]);

        if(this.encounter.getCurrentCard().equals(constants.WILDCARD)){
            this.encounter.getCurrentCard().setColour(BLUE);
        }
    }

    /**
     * This function gives 7-randomized card to the player.
     * it also gives a permission who plays first. the player who has been created first
     * they can draw a card drawCard() later on. if the player a robot, they can use
     * BotService.botDrawCard()
     * @param typePlayer type of the player BOT or HUMAN
     * @param name name of the player
     * @return the created player with a type, name, and 7-cards
     */
    public Player createPlayer( TypePlayer typePlayer, String name ) {

        Player player = new Player().setTypePlayer(typePlayer).setName(name).setEncounter(this.encounter);
        if(encounter.getCurrentPlayer() == null) {
            /*
             * it means, at start of the play there is no Player who draws the card.
             * By default, set the (FIRST) player as the current Player
             */
            encounter.setCurrentPlayer(player);
        }
        for (int i = 0; i < 7; i++) {
            try{
                addRandomCard(player);
            } catch (StackOverflowError se){
                System.err.println("(GameService.java:77) ");
//                reset_ALL_CARD(); //THE PLAY IS GAME OVER!
                throw new StackOverflowError(se.getLocalizedMessage());
            } catch (GameServiceException e) {
                System.err.println("(GameService.java:77) " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        System.out.println(player.getName() + " : " +player.getCards()); //DEBUG
        return player;
    }

    /**
     * This function add a card tp the player. it will look through
     * the ALL_CARD constant, until it found one
     * @param player the player who will get a card
     * @throws StackOverflowError the card might not be found
     */
    protected void addRandomCard( Player player ) throws StackOverflowError, GameServiceException {
        int randomIndex = abs(random.nextInt() % constants.ALL_CARDS.length);
        if(!player.getCards().contains(constants.ALL_CARDS[randomIndex]) && constants.ALL_CARDS[randomIndex].getPlayer() == null) {
            player.withCards(constants.ALL_CARDS[randomIndex]);
        } else {
            // SIMPLE OPTION, BUT RISKY FOR THE TEST!
            // if here player has the same card, try to find another card, recursively !
            // until the player has one!
            // addRandomCard(player);

            // MORE SECURE OPTION, MORE WRITING
            // if here player has the same card, try to find another card!
            boolean isFound = false;
            List<Card> takenCard = new ArrayList<>();
            while(!isFound){
                randomIndex = abs(random.nextInt() % constants.ALL_CARDS.length);
                if(!player.getCards().contains(constants.ALL_CARDS[randomIndex]) && constants.ALL_CARDS[randomIndex].getPlayer() == null) {
                    player.withCards(constants.ALL_CARDS[randomIndex]);
                    isFound = true;
                }
                if(constants.ALL_CARDS[randomIndex].getPlayer() != null && !takenCard.contains(constants.ALL_CARDS[randomIndex])) {
                    takenCard.add(constants.ALL_CARDS[randomIndex]);
                }
                if(takenCard.size() == constants.ALL_CARDS.length){
                    // if every card has been checked and there is no card left then there might be error
                    throw new GameServiceException("All card have been taken!");
                }
            }
        }
//        System.out.println(constants.ALL_CARDS[randomIndex]); //DEBUG
    }

    /**
     * This function gives a possibility for the player to draw a card to
     * deck pile. The player who draws the card must be the currentPlayer
     * in deck pile, otherwise it will throw an Exception
     * @param card that will be put on the deck pile
     * @param player who draws the card
     * @return  the cards that has been drawn, otherwise NOT_VALID
     * @throws GameServiceException throw am exception for this specific class
     */
    public String drawCard(Card card, Player player) throws GameServiceException {

        for (Player encounterPlayer : encounter.getPlayers()) {
            if(encounterPlayer.getCards().size() == 0){
                return SUCCESS;
            }
        }

        /*
         * it means, at start of the play there is no Player who draws the card.
         * By default, set the (FIRST) player who draws the card as the current Player
         */
        if(encounter.getCurrentPlayer() == null) {
            encounter.setCurrentPlayer(player);
        }
        /*
         * if the currentPlayer is not null, check if the currentPlayer is the same as the
         * player who draws the card, otherwise decline the player!
         */
        else if(!encounter.getCurrentPlayer().getName().equals(player.getName())) {
            throw new GameServiceException("NOT THE RIGHT PLAYER, Current Player: " + encounter.getCurrentPlayer());
        }

        Card deckPile = encounter.getCurrentCard();

        // HERE PLAYER TRY TO DRAW THE CARD
        try {
            String returnValue = null;
            if(isConditionTrue(card, deckPile)) {

                // if successful then draw a card
                encounter.setCurrentCard(card);
                int sizeBefore = player.getCards().size();
                player.withoutCards(card);
                assert player.getCards().size() != sizeBefore : new GameServiceException("the card is not being removed");
                deckPile = encounter.getCurrentCard();

                if(deckPile.getName().contains(SKIP)){
                    nextCurrentPlayer();
                    nextCurrentPlayer();
                } else if(deckPile.getName().contains(REVERSE)){
                    direction = direction.equals(RIGHT_WAY) ? LEFT_WAY : RIGHT_WAY;
                    if(encounter.getPlayers().size() != 2){
                        nextCurrentPlayer();
                    }
                } else if(deckPile.getName().contains(DRAW_TWO)) {
                    nextCurrentPlayer();
                    try{
                        addRandomCard(encounter.getCurrentPlayer());
                        addRandomCard(encounter.getCurrentPlayer());
                    } catch (StackOverflowError se){ // TODO clean up stackoverflow catches!
                        System.err.println("(GameService.java:194) or 195" + se.getLocalizedMessage());
                        throw new StackOverflowError(se.getLocalizedMessage());
                    } catch (GameServiceException e) {
                        System.err.println("(GameService.java:194) or 195" + e.getMessage());
                        throw new RuntimeException(e.getMessage());
                    }
                } else {
                    //only normal number has been drawn to the deck pile;
                    nextCurrentPlayer(); //to the nextPlayer
                }
                returnValue = deckPile.getName() + "," + deckPile.getColour();
                
            } else if (card.getName().equals(WILDCARD_STRING)) {
                nextCurrentPlayer(); //to the nextPlayer
                encounter.setCurrentCard(card);
                deckPile = encounter.getCurrentCard();
                player.withoutCards(card);
                returnValue = deckPile.getName() + "," + deckPile.getColour();
                
            }

//            if(player.getCards() == null) {
//                reset_ALL_CARD();
//            }
            return returnValue == null ? NOT_VALID : returnValue;
            /*
            if there is no if statements above true, there is some possibility like:
            - the current deck pile is WILDCARD with specific colour and the other cards is not wildcard
                -> the colour of the card is not the same as the chosen WILDCARD's colour. therefore it
                should be not valid
             */
        } catch (NullPointerException e) {
        // if the card is wrong or the pointer is wrong!
            System.err.println(e.getMessage());
            System.err.println(deckPile + " colour: " + deckPile.getColour());
            return NOT_VALID;
        }

    }

    public static boolean isConditionTrue( Card card, Card deckPile) {
        boolean isSameColour = deckPile.getColour().getName().equals(card.getColour().getName());
        return deckPile.getName().equals(card.getName())
                || isSameColour
                || (deckPile.getNumber() != 0 && card.getNumber() != 0 &&
                deckPile.getNumber() == card.getNumber());
    }

    /**
     * This method choose a next player to draw a card.
     * if the direction is not recognizable (RIGHT_WAY) or (LEFT_WAY)
     * then it will throw an Error
     * @throws GameServiceException if there is a case where the direction is not LEFT or RIGHT
     */
    protected void nextCurrentPlayer() throws GameServiceException {
        int index = getIndexCurrentPlayer(encounter.getCurrentPlayer());

        if(direction.equals(RIGHT_WAY)) {
            int nextIndex = (index + 1) % encounter.getPlayers().size();
            Player nextPlayer = encounter.getPlayers().get(nextIndex);
            encounter.setCurrentPlayer(nextPlayer);
        } else if(direction.equals(LEFT_WAY)) {
            int nextIndex = (index - 1) % encounter.getPlayers().size();
            if(nextIndex < 0){
                nextIndex = encounter.getPlayers().size() - 1;
            }
            Player nextPlayer = encounter.getPlayers().get(nextIndex);
            encounter.setCurrentPlayer(nextPlayer);
        } else {
            throw new GameServiceException("INVALID DIRECTION");
        }
    }

    /**
     * This function return am index where of a certain Player
     * @param player a certain player
     * @return an index of the player in the list of encounter
     */
    private int getIndexCurrentPlayer( Player player ) {
        int index = 0;
        for (Player deckpilePlayer : encounter.getPlayers()) {
            if(deckpilePlayer.getName().equals(player.getName())){
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * This function return an Index of a certain card that belongs to
     * the player. If the card is not being found, return -1
     *
     * @param card   a certain of card
     * @param colour a certain of colour
     * @param player a certain of player
     * @return the index of a card, where is being saved in a list
     */
    public int findIndexCards( String card, Colour colour, Player player) {
        int index = 0;
        for (Card playerCard : player.getCards()) {
            if(card.contains(playerCard.getName()) && playerCard.getColour().equals(colour)) {
                return index;
            }
            index++;
        }
        return -1; // the card is not found!
    }

    /**
     * This function let the Player withdraw the card and set another player
     * to play the card
     *
     * @param player the player who wants to withdraw
     * @throws GameServiceException If it is a wrong player who draws the card
     */
    public void withdraw( Player player) throws GameServiceException {
        if(!encounter.getCurrentPlayer().getTypePlayer().equals(BOT)) {
            try{
                addRandomCard(player);
            } catch (StackOverflowError se) {
                System.err.println("(GameService.java:318) ");
                throw new StackOverflowError(se.getLocalizedMessage());
            } catch (GameServiceException e) {
                System.err.println("(GameService.java:318) " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
            nextCurrentPlayer();
        } else {
            throw new GameServiceException("Not your turn yet!");
        }
    }

}
