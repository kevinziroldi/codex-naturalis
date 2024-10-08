package it.polimi.ingsw.gc07.model_view;

import it.polimi.ingsw.gc07.model.TokenColor;
import it.polimi.ingsw.gc07.model_view_listeners.BoardViewListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client's copy of model Board.
 */
public class BoardView {
    /**
     * Map containing players' scores.
     * Key: nickname
     * Value: score
     */
    private final Map<String, Integer> playerScores;
    /**
     * Map containing players' token colors.
     * Key: nickname
     * Value: token color
     */
    private final Map<String, TokenColor> playerTokenColors;
    /**
     * List of score track board view listeners.
     */
    private final List<BoardViewListener> boardViewListeners;

    /**
     * Constructor method for an empty Board.
     */
    public BoardView() {
        this.playerScores = new HashMap<>();
        this.playerTokenColors = new HashMap<>();
        this.boardViewListeners = new ArrayList<>();
    }

    /**
     * Method used to register a new listener.
     * @param boardViewListener new listener
     */
    public void addListener(BoardViewListener boardViewListener) {
        boardViewListeners.add(boardViewListener);
    }

    /**
     * Method that allows to insert a new Player to the Board,
     * initializing it's score to 0.
     * @param nickname player to add
     */
    public void addPlayerToBoard(String nickname, TokenColor tokenColor) {
        assert(!playerScores.containsKey(nickname)): "The player is already present";
        assert(!playerTokenColors.containsKey(nickname));
        if (nickname == null) {
            return;
        }
        playerScores.put(nickname, 0);
        playerTokenColors.put(nickname, tokenColor);
    }

    /**
     * Setter method, allows to set a certain score for a player.
     * @param nickname player
     * @param newScore new score to set
     */
    public void setNewScore(String nickname, int newScore) {
        assert(playerScores.containsKey(nickname)): "The player is not present";
        assert(playerTokenColors.containsKey(nickname));
        if (nickname == null) {
            return;
        }
        playerScores.put(nickname, newScore);

        updateListeners();
    }

    /**
     * Private method used to send updates to listeners.
     */
    public void updateListeners() {
        Map<String, Integer> playerScoresCopy = new HashMap<>(playerScores);
        Map<String, TokenColor> playerTokenColorsCopy = new HashMap<>(playerTokenColors);
        for(BoardViewListener l: boardViewListeners) {
            l.receiveScoreUpdate(playerScoresCopy, playerTokenColorsCopy);
        }
    }
}
