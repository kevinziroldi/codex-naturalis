package it.polimi.ingsw.gc07.model;

import it.polimi.ingsw.gc07.listeners.ScoreTrackBoardListener;
import it.polimi.ingsw.gc07.updates.ScoreUpdate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing the ScoreTrackBoard.
 */
public class ScoreTrackBoard {
    /**
     * Map that link each player with his personal score.
     */
    private final Map<String, Integer> playersScore;
    /**
     * List of score track board listeners.
     */
    private final List<ScoreTrackBoardListener> scoreTrackBoardListeners;

    /**
     * Constructor method for an empty ScoreTrackBoard.
     */
    public ScoreTrackBoard() {
        this.playersScore = new HashMap<>();
        this.scoreTrackBoardListeners = new ArrayList<>();
    }

    /**
     * Method to add a new score track board listener.
     * @param scoreTrackBoardListener new score track board listener
     */
    public void addListener(ScoreTrackBoardListener scoreTrackBoardListener) {
        scoreTrackBoardListeners.add(scoreTrackBoardListener);
    }

    public void removeListener(ScoreTrackBoardListener scoreTrackBoardListener) {
        scoreTrackBoardListeners.remove(scoreTrackBoardListener);
    }

    /**
     * Method that allows to insert a new Player to the ScoreTrackBoard,
     * initializing it's score to 0.
     * @param nickname player to add
     */
    public void addPlayer(String nickname) {
        assert(!playersScore.containsKey(nickname)): "The player is already present";
        playersScore.put(nickname, 0);
    }

    /**
     * Getter method for the score, allows to know the current score of a player.
     * @param nickname player
     * @return current score for the player
     */
    public int getScore(String nickname) {
        assert(playersScore.containsKey(nickname)): "The player is not present";
        return playersScore.get(nickname);
    }

    /**
     * Setter method, allows to set a certain score for a player.
     * @param nickname player
     * @param newScore new score to set
     */
    public void setScore(String nickname, int newScore) {
        assert(playersScore.containsKey(nickname)): "The player is not present";
        playersScore.put(nickname, newScore);

        // send update
        ScoreUpdate update = new ScoreUpdate(nickname, newScore);
        for(ScoreTrackBoardListener l: scoreTrackBoardListeners) {
            try {
                l.receiveScoreUpdate(update);
            }catch(RemoteException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

    /**
     * Method that allows to increment a player's score of deltaScore points.
     * @param nickname nickname of the player
     * @param deltaScore points to add
     */
    public void incrementScore(String nickname, int deltaScore) {
        assert(playersScore.containsKey(nickname)): "The player is not present";
        setScore(nickname, getScore(nickname) + deltaScore);
    }
}
