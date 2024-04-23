package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.DecksBuilder;
import it.polimi.ingsw.gc07.model.CommandResult;
import it.polimi.ingsw.gc07.model.Player;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.decks.Deck;
import it.polimi.ingsw.gc07.model.decks.GoldCardsDeck;
import it.polimi.ingsw.gc07.model.decks.PlayingDeck;
import it.polimi.ingsw.gc07.model.decks.ResourceCardsDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddPlayerCommandTest {
    private Game game;
    private Player newPlayer;

    @BeforeEach
    void setUp() {
        // create a game
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

        // create a new player
        String nickname = "New player";
        boolean connectionType = true;
        boolean interfaceType = true;
        newPlayer = new Player(nickname, connectionType, interfaceType);
    }

    @Test
    void addPlayerSuccess() {
        game.addPlayer(newPlayer);
        CommandResult result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.SUCCESS, result);
    }

    @Test
    void addPlayerWrongState() {
        Player firstPlayer = new Player("Player1", true, false);
        game.addPlayer(firstPlayer);
        CommandResult result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.SUCCESS, result);
        Player secondPlayer = new Player("Player2", false, false);
        game.addPlayer(secondPlayer);
        result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.SUCCESS, result);
        game.addPlayer(new Player("Player3", false, false));
        result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.WRONG_STATE, result);
    }
}