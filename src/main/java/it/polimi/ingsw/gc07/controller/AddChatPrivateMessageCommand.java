package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.controller.enumerations.CommandResult;
import it.polimi.ingsw.gc07.model.Player;

import java.util.List;

/**
 * Concrete command to add a private message to the chat.
 */
public class AddChatPrivateMessageCommand implements GameCommand {
    /**
     * Game in which the command has to be executed.
     */
    private final Game game;
    /**
     * Content of the private message.
     */
    private final String content;
    /**
     * Sender of the private message.
     */
    private final String sender;
    /**
     * Receiver of the private message.
     */
    private final String receiver;

    /**
     * Constructor of the concrete command AddChatMessageCommand.
     */
    public AddChatPrivateMessageCommand(Game game, String content, String sender, String receiver) {
        this.game = game;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Method to execute the concrete command AddChatPrivateMessageCommand.
     */
    @Override
    public void execute() {
        List<String> playersNicknames = game.getPlayers().stream().map(Player::getNickname).toList();
        // check valid sender
        if(!playersNicknames.contains(sender)){
            game.getCommandResultManager().setCommandResult(CommandResult.WRONG_SENDER);
            return;
        }
        // check valid receiver
        if(!playersNicknames.contains(receiver)){
            game.getCommandResultManager().setCommandResult(CommandResult.WRONG_RECEIVER);
            return;
        }
        // adds message to the chat
        game.getChat().addPrivateMessage(content, sender, receiver, playersNicknames);
        game.getCommandResultManager().setCommandResult(CommandResult.SUCCESS);
    }
}
