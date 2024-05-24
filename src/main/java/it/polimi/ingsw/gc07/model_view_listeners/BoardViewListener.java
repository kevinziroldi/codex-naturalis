package it.polimi.ingsw.gc07.model_view_listeners;

import it.polimi.ingsw.gc07.enumerations.TokenColor;

import java.util.Map;

public interface BoardViewListener {
    /**
     * Method used to show the client an updated score.
     * @param playerScores players' scores
     * @param playerTokenColors players' token colors
     */
    void receiveScoreUpdate(Map<String, Integer> playerScores, Map<String, TokenColor> playerTokenColors);
}
