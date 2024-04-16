package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.controller.enumerations.CommandResult;
import it.polimi.ingsw.gc07.controller.enumerations.GameState;
import it.polimi.ingsw.gc07.model.GameField;
import it.polimi.ingsw.gc07.model.Player;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Concrete command to add a new player to the game.
 */
public class AddPlayerCommand extends GameCommand {
    /**
     * Player to add.
     */
    private final Player newPlayer;

    /**
     * Constructor of the concrete command.
     * This constructor takes parameter game, used by the server.
     * @param game game
     * @param newPlayer player to add
     */
    public AddPlayerCommand(Game game, Player newPlayer) {
        setGame(game);
        this.newPlayer = newPlayer;
    }

    /**
     * Override of method execute to add a new player to the game.
     */
    @Override
    public void execute() {
        Game game = getGame();

        if(!game.getState().equals(GameState.GAME_STARTING)) {
            game.getCommandResultManager().setCommandResult(CommandResult.WRONG_STATE);
            return;
        }

        // draw card can't return null, since the game hasn't already started
        newPlayer.addCardHand(game.getResourceCardsDeck().drawCard());
        newPlayer.addCardHand(game.getResourceCardsDeck().drawCard());
        newPlayer.addCardHand(game.getGoldCardsDeck().drawCard());
        newPlayer.setSecretObjective(game.getObjectiveCardsDeck().drawCard());

        PlaceableCard starterCard = game.getStarterCardsDeck().drawCard();
        GameField gameField = new GameField(starterCard);
        game.getPlayers().add(newPlayer);
        game.getPlayersGameField().put(newPlayer.getNickname(), gameField);
        game.getScoreTrackBoard().addPlayer(newPlayer.getNickname());
        if (isFull()) {
            setup();
            game.setState(GameState.PLAYING);
        }
        game.getCommandResultManager().setCommandResult(CommandResult.SUCCESS);
    }

    /**
     * Method telling if there are available places in the game.
     * @return true if no other player can connect to the game
     */
    private boolean isFull(){
        return getGame().getPlayers().size() == getGame().getPlayersNumber();
    }

    /**
     * Method to set up the game: the first player is chosen and 4 cards (2 gold and 2 resource) are revealed.
     */
    private void setup() {
        Game game = getGame();

        assert(game.getState().equals(GameState.GAME_STARTING)): "The state is not WAITING_PLAYERS";
        // choose randomly the first player
        Random random= new Random();
        game.setCurrPlayer(random.nextInt(game.getPlayersNumber()));
        game.getPlayers().get(game.getCurrPlayer()).setFirst();

        // draw card can't return null, since the game hasn't already started

        //place 2 gold cards
        List<GoldCard> setUpGoldCardsFaceUp = new ArrayList<>();
        setUpGoldCardsFaceUp.add(game.getGoldCardsDeck().drawCard());
        setUpGoldCardsFaceUp.add(game.getGoldCardsDeck().drawCard());
        game.getGoldCardsDeck().setFaceUpCards(setUpGoldCardsFaceUp);

        //place 2 resource card
        List<DrawableCard> setUpResourceCardsFaceUp = new ArrayList<>();
        setUpResourceCardsFaceUp.add(game.getResourceCardsDeck().drawCard());
        setUpResourceCardsFaceUp.add(game.getResourceCardsDeck().drawCard());
        game.getResourceCardsDeck().setFaceUpCards(setUpResourceCardsFaceUp);

        // place common objective cards
        List<ObjectiveCard> setUpObjectiveCardsFaceUp = new ArrayList<>();
        setUpObjectiveCardsFaceUp.add(game.getObjectiveCardsDeck().drawCard());
        setUpObjectiveCardsFaceUp.add(game.getObjectiveCardsDeck().drawCard());
        game.getObjectiveCardsDeck().setFaceUpCards(setUpObjectiveCardsFaceUp);
    }
}
