package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.controller.enumerations.CommandResult;
import it.polimi.ingsw.gc07.controller.enumerations.GameState;
import it.polimi.ingsw.gc07.exceptions.*;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.enumerations.CardType;

/**
 * Concrete command to draw one of two faceUp cards of a given type.
 */
public class DrawFaceUpCardCommand implements GameCommand{
    /**
     * Game in which the command has to be executed.
     */
    private final Game game;

    /**
     * Nickname of the player.
     */
    private final String nickname;

    /**
     * Type of the card a user wants to draw.
     */
    private final CardType type;

    /**
     * Position in the List of faceUpCards.
     */
    private final int pos;

    /**
     * Constructor of the concrete command DrawFaceUpCardCommand.
     * @param nickname nickname
     * @param type type
     * @param pos pos
     */
    public DrawFaceUpCardCommand(Game game, String nickname, CardType type, int pos) {
        this.game = game;
        this.nickname = nickname;
        this.type = type;
        this.pos = pos;
    }

    /**
     * Method that allows a player to draw one of two faceUp cards of a given type.
     */
    @Override
    public void execute() {
        if(!game.getState().equals(GameState.PLAYING)) {
            game.getCommandResultManager().setCommandResult(CommandResult.WRONG_STATE);
            return;
        }
        // if the state is PLAYING ...
        if(!game.getPlayers().get(game.getCurrPlayer()).getNickname().equals(nickname)){
            game.getCommandResultManager().setCommandResult(CommandResult.WRONG_PLAYER);
            return;
        }
        if(type.equals(CardType.OBJECTIVE_CARD) || type.equals(CardType.STARTER_CARD)) {
            game.getCommandResultManager().setCommandResult(CommandResult.WRONG_CARD_TYPE);
            return;
        }
        DrawableCard card = null;
        if(type.equals(CardType.RESOURCE_CARD)) {
            card = game.getResourceCardsDeck().drawFaceUpCard(pos);
            if(card == null) {
                game.getCommandResultManager().setCommandResult(CommandResult.CARD_NOT_PRESENT);
                return;
            }
            game.getPlayers().get(game.getCurrPlayer()).addCardHand(card);
        }
        if(type.equals(CardType.GOLD_CARD)) {
            card = game.getGoldCardsDeck().drawFaceUpCard(pos);
            if(card == null) {
                game.getCommandResultManager().setCommandResult(CommandResult.CARD_NOT_PRESENT);
                return;
            }
            game.getPlayers().get(game.getCurrPlayer()).addCardHand(card);
        }
        game.changeCurrPlayer();
        game.getCommandResultManager().setCommandResult(CommandResult.SUCCESS);
    }
}
