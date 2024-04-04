package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.DecksBuilder;
import it.polimi.ingsw.gc07.controller.enumerations.CommandResult;
import it.polimi.ingsw.gc07.controller.enumerations.GameState;
import it.polimi.ingsw.gc07.exceptions.CardNotPresentException;
import it.polimi.ingsw.gc07.exceptions.WrongNumberOfPlayersException;
import it.polimi.ingsw.gc07.model.Player;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.decks.Deck;
import it.polimi.ingsw.gc07.model.decks.GoldCardsDeck;
import it.polimi.ingsw.gc07.model.decks.PlayingDeck;
import it.polimi.ingsw.gc07.model.decks.ResourceCardsDeck;
import it.polimi.ingsw.gc07.model.enumerations.TokenColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game game;
    @BeforeEach
    void setUp() {
        int id = 0;
        int playersNumber = 2;
        ResourceCardsDeck resourceCardsDeck = DecksBuilder.buildResourceCardsDeck();
        resourceCardsDeck.shuffle();
        GoldCardsDeck goldCardsDeck = DecksBuilder.buildGoldCardsDeck();
        goldCardsDeck.shuffle();
        PlayingDeck<ObjectiveCard> objectiveCardsDeck = DecksBuilder.buildObjectiveCardsDeck();
        objectiveCardsDeck.shuffle();
        Deck<PlaceableCard> starterCardsDecks = DecksBuilder.buildStarterCardsDeck();
        starterCardsDecks.shuffle();

        game = new Game(id, playersNumber, resourceCardsDeck, goldCardsDeck, objectiveCardsDeck, starterCardsDecks);

        // add first player
        Player firstPlayer = new Player("Player1", true, false);
        firstPlayer.setTokenColor(TokenColor.BLUE);
        game.setCommand(new AddPlayerCommand(game, firstPlayer));
        game.execute();
        CommandResult result = game.getCommandResultManager().getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
        // add second player
        Player secondPlayer = new Player("Player2", false, false);
        secondPlayer.setTokenColor(TokenColor.GREEN);
        game.setCommand(new AddPlayerCommand(game, secondPlayer));
        game.execute();
        result = game.getCommandResultManager().getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
        try {
            game.getPlayers().get(0).setSecretObjective(game.getObjectiveCardsDeck().drawCard());
            game.getPlayers().get(1).setSecretObjective(game.getObjectiveCardsDeck().drawCard());
        }catch (CardNotPresentException e){
            throw new RuntimeException();
        }
        game.setCurrentPlayer(0);
        List<ObjectiveCard> faceUpCards= new ArrayList<>();
        try {
            faceUpCards.add(game.getObjectiveCardsDeck().drawCard());
            faceUpCards.add(game.getObjectiveCardsDeck().drawCard());
        }catch (CardNotPresentException e){
            throw new RuntimeException();
        }
        game.getObjectiveCardsDeck().setFaceUpCards(faceUpCards);
    }

    @Test
    void computeWinnerOneWinner() {
        // TODO
    }

    @Test
    void computeWinnerDraw() {
        // TODO
    }

    @Test
    void changeCurrPlayer() {
        game.setState(GameState.WAITING_PLAYERS);
        game.changeCurrPlayer();
        assertEquals(game.getCommandResultManager().getCommandResult(), CommandResult.WRONG_STATE);
        game.setState(GameState.PLAYING);
        game.changeCurrPlayer();
        assertEquals(1, game.getCurrPlayer());
        game.changeCurrPlayer();
        assertEquals(0, game.getCurrPlayer());
        //Testing disconnection
        game.getPlayers().get(1).setIsConnected(false);
        game.changeCurrPlayer();
        assertEquals(0, game.getCurrPlayer());
        game.getPlayers().get(1).setIsConnected(true);
        //Testing player stalled
        game.getPlayers().get(1).setIsStalled(true);
        game.changeCurrPlayer();
        assertEquals(0, game.getCurrPlayer());
        //Testing the final phase
        game.setTwentyPointsReached(true);
        game.getPlayers().get(1).setIsStalled(false);
        game.changeCurrPlayer();
        game.changeCurrPlayer();
        game.changeCurrPlayer();
    }
}