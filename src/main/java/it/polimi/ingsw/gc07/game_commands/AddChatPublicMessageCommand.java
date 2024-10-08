package it.polimi.ingsw.gc07.game_commands;

import it.polimi.ingsw.gc07.controller.GameController;

/**
 * Concrete command to add a public message to the chat.
 */
public class AddChatPublicMessageCommand implements GameControllerCommand {
    /**
     * Content of the public message.
     */
    private final String content;
    /**
     * Sender of the public message.
     */
    private final String sender;

    /**
     * Constructor of the concrete command AddChatPublicMessageCommand.
     * @param content content of the message
     * @param sender sender of the message
     */
    public AddChatPublicMessageCommand(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }

    /**
     * Method to execute the concrete command AddChatPublicMessageCommand.
     */
    @Override
    public void execute(GameController gameController) {
        gameController.addChatPublicMessage(content, sender);
    }
}
