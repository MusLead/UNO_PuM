package de.uniks.pmws2223.uno;

import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Colour;
import de.uniks.pmws2223.uno.model.TypePlayer;



public class Constants {

    public final static Colour YELLOW = new Colour().setName("YELLOW");
    public final static Colour RED = new Colour().setName("RED");
    public final static Colour BLUE = new Colour().setName("BLUE");
    public final static Colour GREEN = new Colour().setName("GREEN");
    public final static Colour[] COLOURS = {YELLOW,RED,BLUE,GREEN};
    public final  Card RED_1 = new Card().setName("RED_1").setColour(RED).setNumber(1);
    public final  Card RED_2 = new Card().setName("RED_2").setColour(RED).setNumber(2);
    public final  Card RED_3 = new Card().setName("RED_3").setColour(RED).setNumber(3);
    public final  Card RED_4 = new Card().setName("RED_4").setColour(RED).setNumber(4);
    public final  Card RED_5 = new Card().setName("RED_5").setColour(RED).setNumber(5);
    public final  Card RED_6 = new Card().setName("RED_6").setColour(RED).setNumber(6);
    public final  Card RED_7 = new Card().setName("RED_7").setColour(RED).setNumber(7);
    public final  Card RED_8 = new Card().setName("RED_8").setColour(RED).setNumber(8);
    public final  Card RED_9 = new Card().setName("RED_9").setColour(RED).setNumber(9);

    public final  Card YELLOW_1 = new Card().setName("YELLOW_1").setColour(YELLOW).setNumber(1);
    public final  Card YELLOW_2 = new Card().setName("YELLOW_2").setColour(YELLOW).setNumber(2);
    public final  Card YELLOW_3 = new Card().setName("YELLOW_3").setColour(YELLOW).setNumber(3);
    public final  Card YELLOW_4 = new Card().setName("YELLOW_4").setColour(YELLOW).setNumber(4);
    public final  Card YELLOW_5 = new Card().setName("YELLOW_5").setColour(YELLOW).setNumber(5);
    public final  Card YELLOW_6 = new Card().setName("YELLOW_6").setColour(YELLOW).setNumber(6);
    public final  Card YELLOW_7 = new Card().setName("YELLOW_7").setColour(YELLOW).setNumber(7);
    public final  Card YELLOW_8 = new Card().setName("YELLOW_8").setColour(YELLOW).setNumber(8);
    public final  Card YELLOW_9 = new Card().setName("YELLOW_9").setColour(YELLOW).setNumber(9);

    public final  Card BLUE_1 = new Card().setName("BLUE_1").setColour(BLUE).setNumber(1);
    public final  Card BLUE_2 = new Card().setName("BLUE_2").setColour(BLUE).setNumber(2);
    public final  Card BLUE_3 = new Card().setName("BLUE_3").setColour(BLUE).setNumber(3);
    public final  Card BLUE_4 = new Card().setName("BLUE_4").setColour(BLUE).setNumber(4);
    public final  Card BLUE_5 = new Card().setName("BLUE_5").setColour(BLUE).setNumber(5);
    public final  Card BLUE_6 = new Card().setName("BLUE_6").setColour(BLUE).setNumber(6);
    public final  Card BLUE_7 = new Card().setName("BLUE_7").setColour(BLUE).setNumber(7);
    public final  Card BLUE_8 = new Card().setName("BLUE_8").setColour(BLUE).setNumber(8);
    public final  Card BLUE_9 = new Card().setName("BLUE_9").setColour(BLUE).setNumber(9);

    public final  Card GREEN_1 = new Card().setName("GREEN_1").setColour(GREEN).setNumber(1);
    public final  Card GREEN_2 = new Card().setName("GREEN_2").setColour(GREEN).setNumber(2);
    public final  Card GREEN_3 = new Card().setName("GREEN_3").setColour(GREEN).setNumber(3);
    public final  Card GREEN_4 = new Card().setName("GREEN_4").setColour(GREEN).setNumber(4);
    public final  Card GREEN_5 = new Card().setName("GREEN_5").setColour(GREEN).setNumber(5);
    public final  Card GREEN_6 = new Card().setName("GREEN_6").setColour(GREEN).setNumber(6);
    public final  Card GREEN_7 = new Card().setName("GREEN_7").setColour(GREEN).setNumber(7);
    public final  Card GREEN_8 = new Card().setName("GREEN_8").setColour(GREEN).setNumber(8);
    public final  Card GREEN_9 = new Card().setName("GREEN_9").setColour(GREEN).setNumber(9);

    public final static String WILDCARD_STRING = "WILDCARD";
    public final  Card WILDCARD = new Card().setName(WILDCARD_STRING);

    public final static String REVERSE = "REVERSE";
    public final  Card REVERSE_BLUE = new Card().setName(REVERSE).setColour(BLUE);
    public final  Card REVERSE_GREEN = new Card().setName(REVERSE).setColour(GREEN);
    public final  Card REVERSE_RED = new Card().setName(REVERSE).setColour(RED);
    public final  Card REVERSE_YELLOW = new Card().setName(REVERSE).setColour(YELLOW);

    public final static String SKIP = "SKIP";
    public final  Card SKIP_BLUE = new Card().setName(SKIP).setColour(BLUE);
    public final  Card SKIP_GREEN = new Card().setName(SKIP).setColour(GREEN);
    public final  Card SKIP_RED = new Card().setName(SKIP).setColour(RED);
    public final  Card SKIP_YELLOW = new Card().setName(SKIP).setColour(YELLOW);

    public final static String DRAW_TWO = "+2\nCards";
    public final  Card DRAW_TWO_BLUE = new Card().setName(DRAW_TWO).setColour(BLUE);
    public final  Card DRAW_TWO_GREEN = new Card().setName(DRAW_TWO).setColour(GREEN);
    public final  Card DRAW_TWO_RED = new Card().setName(DRAW_TWO).setColour(RED);
    public final  Card DRAW_TWO_YELLOW = new Card().setName(DRAW_TWO).setColour(YELLOW);

    public final  Card[] ALL_CARDS = {
            WILDCARD,
            SKIP_BLUE,SKIP_YELLOW,SKIP_GREEN,SKIP_RED,
            REVERSE_BLUE,REVERSE_GREEN,REVERSE_RED,REVERSE_YELLOW,
            DRAW_TWO_BLUE,DRAW_TWO_GREEN,DRAW_TWO_RED,DRAW_TWO_YELLOW,

            RED_1,RED_2,RED_3,RED_4,RED_5,RED_6,RED_7,RED_8,RED_9,
            BLUE_1,BLUE_2,BLUE_3,BLUE_4,BLUE_5,BLUE_6,BLUE_7,BLUE_8,BLUE_9,
            GREEN_1,GREEN_2,GREEN_3,GREEN_4,GREEN_5,GREEN_6,GREEN_7,GREEN_8,GREEN_9,
            YELLOW_1,YELLOW_2,YELLOW_3,YELLOW_4,YELLOW_5,YELLOW_6,YELLOW_7,YELLOW_8,YELLOW_9
    };

    public final static TypePlayer BOT = new TypePlayer().setType("bot");
    public final static TypePlayer HUMAN = new TypePlayer().setType("human");

    public final static String NOT_VALID = "NOT VALID";
    public final static String SUCCESS = "SUCCESS";
    public final static String RIGHT_WAY = "RIGHT";
    public final static String LEFT_WAY = "LEFT";
    public final static String WITHDRAW = "WITHDRAW";
    
}
