package de.uniks.pmws2223.uno.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

@SuppressWarnings("unused")
public class GenModel implements ClassModelDecorator {

    public class Player {
        String name;
        @Link()
        TypePlayer typePlayer;
        /*
         * LOGIC FAULTY Link(): if I use Link() and if there
         * is a same object within the list, it will be rejected!
         *
         * 16.02.2023  Is that true?
         * 17.06.2023 Not really, you should have used the Link() to the other too!
         */
        @Link("player")
        List<Card> cards;
        @Link("currentPlayer")
        Encounter currentDrawPile;
        @Link("players")
        Encounter encounter;

    }

    public class Card {
        String name;
        @Link()
        Colour colour;
        int Number;
        @Link("cards")
        Player player;
    }

    public class Encounter {
        @Link()
        Card currentCard;
        @Link("currentDrawPile")
        Player currentPlayer;
        @Link("encounter")
        List<Player> players;
    }

    public class Colour {
        String name;
    }

    public class TypePlayer {
        String type;
    }

    @Override
    public void decorate(ClassModelManager mm) {
        mm.haveNestedClasses(GenModel.class);
    }
}
