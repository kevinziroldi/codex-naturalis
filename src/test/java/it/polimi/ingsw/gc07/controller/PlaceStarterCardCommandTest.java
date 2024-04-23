package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.DecksBuilder;
import it.polimi.ingsw.gc07.game_commands.PlaceStarterCardCommand;
import it.polimi.ingsw.gc07.model.enumerations.CommandResult;
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

import static org.junit.jupiter.api.Assertions.*;

class PlaceStarterCardCommandTest {

    GameController gameController;

    @BeforeEach
    void setUp(){
        int id = 0;
        int playersNumber = 2;
        ResourceCardsDeck resourceCardsDeck = DecksBuilder.buildResourceCardsDeck();
        GoldCardsDeck goldCardsDeck = DecksBuilder.buildGoldCardsDeck();
        PlayingDeck<ObjectiveCard> objectiveCardsDeck = DecksBuilder.buildObjectiveCardsDeck();
        Deck<PlaceableCard> starterCardsDecks = DecksBuilder.buildStarterCardsDeck();
        gameController = new GameController(id, playersNumber, resourceCardsDeck, goldCardsDeck, objectiveCardsDeck, starterCardsDecks);

        Player p1 = new Player("P1", true, true);
        p1.setTokenColor(TokenColor.GREEN);
        gameController.addPlayer(p1);
        CommandResult result = gameController.getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
        // add second player
        Player p2 = new Player("P2", true, true);
        p2.setTokenColor(TokenColor.BLUE);
        gameController.addPlayer(p2);
        result = gameController.getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
        gameController.setCurrentPlayer(0);
        gameController.getPlayers().get(1).setIsStalled(true);
    }

    @Test
    void PlaceStarterCardSuccess() {
        gameController.setAndExecuteCommand(new PlaceStarterCardCommand(gameController.getPlayers().get(gameController.getCurrPlayer()).getNickname(), true));
        CommandResult result = gameController.getCommandResult();
        assertEquals(CommandResult.SUCCESS, result);
    }
}