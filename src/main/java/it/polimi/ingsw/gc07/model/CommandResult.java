package it.polimi.ingsw.gc07.model;

/**
 * Enumeration class representing all the possible results of an action performed by a player.
 */
public enum CommandResult {
    SUCCESS("Command successful"),
    WRONG_STATE("The game is in a wrong state"),
    WRONG_SENDER("The sender is wrong"),
    WRONG_RECEIVER("The receiver is wrong"),
    PLAYER_NOT_PRESENT("The player is not present"),
    PLAYER_ALREADY_CONNECTED("The player is already connected"),
    PLAYER_ALREADY_DISCONNECTED("The player is already disconnected"),
    DISCONNECTION_SUCCESSFUL("The player has been successfully disconnected"),
    WRONG_PLAYER("It's not your turn"),
    WRONG_CARD_TYPE("The card is of the wrong type"),
    CARD_NOT_PRESENT("The required card is not present"),
    NO_COVERED_CORNER("The placement is not valid: you must cover at least one corner"),
    NOT_LEGIT_CORNER("Trying to cover a not-corner"),
    MULTIPLE_CORNERS_COVERED("The placement is not valid: you can't cover multiple corners of the same card"),
    CARD_ALREADY_PRESENT("A card is already present"),
    INDEXES_OUT_OF_GAME_FIELD("Provided indexes exceed game field size"),
    PLACING_CONDITION_NOT_MET("The placing condition is not met"),
    PLAYER_ALREADY_PRESENT("This nickname is already taken"),
    GAME_FULL("The game is full, choose another game"),
    TOKEN_COLOR_ALREADY_TAKEN("The chosen token color is already taken in this game"),
    GAME_NOT_PRESENT("A game with the given id does not exist"),
    WRONG_PLAYERS_NUMBER("The provided number of players is not correct"),
    NOT_PLACED_YET("You have not placed a card yet"),
    CARD_ALREADY_PLACED("You have already placed a card in this turn"),
    ;

    /**
     * Result message associated to a result value, that will be displayed to the player.
     */
    private final String resultMessage;

    /**
     * Constructor of CommandResult
     * @param resultMessage message associated to a result
     */
    CommandResult(String resultMessage){
        this.resultMessage = resultMessage;
    }

    /**
     * Getter method for the message associated to a command result.
     * @return message associated to a command result
     */
    public String getResultMessage() {
        return resultMessage;
    }
}
