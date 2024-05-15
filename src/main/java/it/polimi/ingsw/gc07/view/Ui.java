package it.polimi.ingsw.gc07.view;

import it.polimi.ingsw.gc07.model_view_listeners.*;

public interface Ui extends GameViewListener, ChatViewListener, DeckViewListener, GameFieldViewListener, PlayerViewListener, BoardViewListener {
    void runCliJoinGame();
    void runCliGame();
}
