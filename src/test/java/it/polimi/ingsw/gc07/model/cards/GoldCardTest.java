package it.polimi.ingsw.gc07.model.cards;

import it.polimi.ingsw.gc07.DecksBuilder;
import it.polimi.ingsw.gc07.model.CommandResult;
import it.polimi.ingsw.gc07.model.GameField;
import it.polimi.ingsw.gc07.model.decks.Deck;
import it.polimi.ingsw.gc07.model.decks.GoldCardsDeck;
import it.polimi.ingsw.gc07.model.decks.ResourceCardsDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoldCardTest {
    private Deck<PlaceableCard> starterCardsDeck;
    private ResourceCardsDeck resourceCardsDeck;
    private GoldCardsDeck goldCardsDeck;
    PlaceableCard myStarterCard;
    DrawableCard myResourceCard;
    DrawableCard myGoldCard;
    @BeforeEach
    void setUp() {
        resourceCardsDeck = DecksBuilder.buildResourceCardsDeck();
        goldCardsDeck = DecksBuilder.buildGoldCardsDeck();
        starterCardsDeck = DecksBuilder.buildStarterCardsDeck();
    }

    @Test
    void isGoldPlaceableNoLegitCorner() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 49){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, false);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, false);
            }
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, false);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, false);
            }
        }
        assertEquals(CommandResult.NOT_LEGIT_CORNER, myGoldCard.isPlaceable(gameField, 42, 40, false));
    }

    @Test
    void isGoldPlaceableNoCoveredCorner() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 58){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, true);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
        }
        assertEquals(CommandResult.NO_COVERED_CORNER, myGoldCard.isPlaceable(gameField, 45, 45, true));
    }

    @Test
    void isGoldPlaceableMultipleCorner() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 58){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, true);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
        }
        assertEquals(CommandResult.MULTIPLE_CORNERS_COVERED, myGoldCard.isPlaceable(gameField, 42, 41, true));
    }

    @Test
    void isGoldPlaceableIndexOut() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 58){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, true);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
        }
        assertEquals(CommandResult.INDEXES_OUT_OF_GAME_FIELD, myGoldCard.isPlaceable(gameField, 89, 10, true));
    }

    @Test
    void isGoldPlaceableCardAlreadyPresent() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 58){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, true);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
        }
        assertEquals(CommandResult.CARD_ALREADY_PRESENT, myGoldCard.isPlaceable(gameField, 41, 41, true));
    }
    @Test
    void isGoldPlaceableCondition() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 58){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, true);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
        }
        assertEquals(CommandResult.PLACING_CONDITION_NOT_MET, myGoldCard.isPlaceable(gameField, 42, 40, false));

    }

    @Test
    void getGoldPlacementScore() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 49){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, true);
            }
            if (c.getId() == 4){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
        }
        gameField.placeCard(myGoldCard, 38, 38, false);
        assertEquals(3, myGoldCard.getPlacementScore(gameField, 38, 38));
    }

    @Test
    void getGoldCornerPlacementScore() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 45){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 1){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, false);
            }
            if (c.getId() == 2){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, true);
            }
            if (c.getId() == 3){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, true);
            }
            if (c.getId() == 11){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, true);
            }
        }
        gameField.placeCard(myGoldCard, 38, 40, false);
        assertEquals(4, myGoldCard.getPlacementScore(gameField, 38, 40));
    }
    @Test
    void getGoldItemsPlacementScore() {
        myStarterCard = starterCardsDeck.drawCard();
        GameField gameField = new GameField(myStarterCard);
        gameField.placeCard(myStarterCard, 40, 40, false);
        for(DrawableCard c: goldCardsDeck.getContent()){
            if(c.getId() == 42){
                myGoldCard = c;
            }
        }
        for(DrawableCard c: resourceCardsDeck.getContent()){
            if (c.getId() == 37){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 39, false);
            }
            if (c.getId() == 6){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 39, false);
            }
            if (c.getId() == 16){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 39, 41, false);
            }
            if (c.getId() == 25){
                myResourceCard = c;
                gameField.placeCard(myResourceCard, 41, 41, false);
            }
        }
        assertEquals(CommandResult.SUCCESS, myGoldCard.isPlaceable(gameField, 42, 42, false));
        gameField.placeCard(myGoldCard, 42, 42, false);
        assertEquals(5, myGoldCard.getPlacementScore(gameField, 42, 42));
    }
}