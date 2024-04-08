package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.DecksBuilder;
import it.polimi.ingsw.gc07.controller.enumerations.CommandResult;
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

class AddChatPrivateMessageCommandTest {
    Game game;

    @BeforeEach
    void setUp() {
        // create a game
        int id = 0;
        int playersNumber = 3;
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
        game.setCommand(new AddPlayerCommand(game, firstPlayer));
        game.execute();
        CommandResult result = game.getCommandResultManager().getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
        // add second player
        Player secondPlayer = new Player("Player2", false, false);
        game.setCommand(new AddPlayerCommand(game, secondPlayer));
        game.execute();
        result = game.getCommandResultManager().getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
        // add third player
        Player thirdPlayer = new Player("Player3", false, false);
        game.setCommand(new AddPlayerCommand(game, thirdPlayer));
        game.execute();
        result = game.getCommandResultManager().getCommandResult();
        if(!result.equals(CommandResult.SUCCESS))
            throw new RuntimeException();
    }

    @Test
    void addMessageSuccess() {
        game.setCommand(new AddChatPrivateMessageCommand(game, "My content...", "Player1", "Player3"));
        game.execute();
        CommandResult result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.SUCCESS, result);
    }

    @Test
    void addMessageWrongSender() {
        game.setCommand(new AddChatPrivateMessageCommand(game, "My content...", "WrongSender", "Player1"));
        game.execute();
        CommandResult result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.WRONG_SENDER, result);
    }

    @Test
    void addMessageWrongReceiver() {
        game.setCommand(new AddChatPrivateMessageCommand(game, "My content...", "Player3", "WrongReceiver"));
        game.execute();
        CommandResult result = game.getCommandResultManager().getCommandResult();
        assertEquals(CommandResult.WRONG_RECEIVER, result);
    }
}