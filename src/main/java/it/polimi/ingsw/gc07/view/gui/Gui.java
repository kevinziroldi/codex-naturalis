package it.polimi.ingsw.gc07.view.gui;

import it.polimi.ingsw.gc07.controller.GameState;
import it.polimi.ingsw.gc07.controller.CommandResult;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.chat.ChatMessage;
import it.polimi.ingsw.gc07.model.TokenColor;
import it.polimi.ingsw.gc07.network.Client;
import it.polimi.ingsw.gc07.view.Ui;
import it.polimi.ingsw.gc07.view.gui.gui_controllers.StageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public  class Gui extends Application implements Ui {
    /**
     * Gui instance, automatically created al application launch.
     */
    private static Gui guiInstance = null;
    /**
     * Nickname of Gui's owner.
     */
    private String nickname;
    /**
     * Starting phase timeout.
     */
    private Timer timeout;

    /**
     * Init method, called at application launch.
     * Sets the value of gui instance, created ad launch.
     */
    @Override
    public void init() {
        synchronized(Gui.class) {
            guiInstance = this;
            Gui.class.notifyAll();
        }
    }

    /**
     * Start method, called at application launch.
     * Start the Gui interface, displaying the lobby.
     */
    @Override
    public void start(Stage stage) {
        timeout = new Timer();
        new Thread(()-> {
            // timer of 5 minutes
            timeout.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 5 * 60 * 1000);
        }).start();
        StageController.setup(stage);
    }

    /**
     * Getter method for the Gui instance, automatically created at launch.
     * @return gui instance
     */
    public synchronized static Gui getGuiInstance() {
        while(guiInstance == null) {
            try {
                Gui.class.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
        return guiInstance;
    }

    /**
     * Setter for client's nickname.
     * @param nickname nickname
     */
    public void setNickname(String nickname) {
        Platform.runLater(() -> {
            this.nickname = nickname;
            StageController.setNickname(nickname);
        });
    }

    /**
     * Setter for client.
     * @param client client
     */
    public void setClient(Client client) {
        Platform.runLater(() -> StageController.setClient(client));
    }

    /**
     * Method used to run the join game interface, i.e. the lobby scene.
     */
    @Override
    public void runJoinGameInterface() {
        // launch has already started interface
        Platform.runLater(() -> StageController.setScene(SceneType.LOBBY_SCENE));
    }

    /**
     * Method used to run the game interface, i.e. player scene.
     */
    @Override
    public void runGameInterface() {
        Platform.runLater(() -> {
            synchronized (this) {
                timeout.cancel();
                timeout.purge();
            }
            // change scene to PlayerScene
            StageController.setScene(SceneType.PLAYER_SCENE);
        });
    }

    /**
     * Method used to receive a score update.
     * @param playerScores players' scores
     * @param playerTokenColors players' token colors
     */
    @Override
    public void receiveScoreUpdate(Map<String, Integer> playerScores, Map<String, TokenColor> playerTokenColors) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().updateScore(playerScores, playerTokenColors);
            }
        });
    }

    /**
     * Method used to receive a chat message.
     * @param chatMessage chat message
     */
    @Override
    public void receiveMessageUpdate(ChatMessage chatMessage) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE)) {
                StageController.getController().addMessage(chatMessage);
            }
        });
    }

    /**
     * Method used to receive a deck update.
     * @param topResourceDeck top resource deck
     * @param topGoldDeck top gold deck
     * @param faceUpResourceCard face up resource
     * @param faceUpGoldCard face up gold
     * @param commonObjective common objective
     */
    @Override
    public void receiveDecksUpdate(DrawableCard topResourceDeck, GoldCard topGoldDeck, List<DrawableCard> faceUpResourceCard, List<GoldCard> faceUpGoldCard, List<ObjectiveCard> commonObjective) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().updateDecks(topResourceDeck, topGoldDeck, faceUpResourceCard, faceUpGoldCard, commonObjective);
            }
        });
    }

    /**
     * Method used to receive a game field update.
     * @param nickname nickname
     * @param cardsContent cards content
     * @param cardsFace cards face
     * @param cardsOrder cards order
     */
    @Override
    public void receiveGameFieldUpdate(String nickname, PlaceableCard[][] cardsContent, Boolean[][] cardsFace, int[][] cardsOrder) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(nickname.equals(this.nickname) && StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE)) {
                // player's game field update, will be sent to PlayerSceneController
                StageController.getController().updateGameField(nickname, cardsContent, cardsFace, cardsOrder);
            }else if(!nickname.equals(this.nickname) && StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                // other player's game field update, will be sent to OtherPlayerSceneController
                StageController.getController().updateGameField(nickname, cardsContent, cardsFace, cardsOrder);
                // the scene will check if the nickname is the one of the "other player"
            }
        });
    }

    /**
     * Method used to receive a starter card update.
     * @param starterCard starter card update
     */
    @Override
    public void receiveStarterCardUpdate(String nickname, PlaceableCard starterCard) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(!nickname.equals(this.nickname)) {
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE)) {
                StageController.getController().updateStarterCard(starterCard);
            }
        });
    }

    /**
     * Method used to receive a card hand update.
     * @param nickname nickname
     * @param hand card hand
     * @param personalObjective persona objective
     */
    @Override
    public void receiveCardHandUpdate(String nickname, List<DrawableCard> hand, List<ObjectiveCard> personalObjective) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(!nickname.equals(this.nickname)) {
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE)) {
                StageController.getController().updateCardHand(hand, personalObjective);
            }
        });
    }

    /**
     * Method used to receive a general model update.
     * @param gameState game state
     * @param currPlayer current player
     */
    @Override
    public void receiveGeneralModelUpdate(GameState gameState, String currPlayer) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().updateGameInfo(gameState, currPlayer);
            }
        });
    }

    /**
     * Method used to receive penultimate round update.
     */
    @Override
    public void receivePenultimateRoundUpdate() {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().setPenultimateRound();
            }
        });
    }

    /**
     * Method used to receive additional round update.
     */
    @Override
    public void receiveAdditionalRoundUpdate() {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().setAdditionalRound();
            }
        });
    }

    /**
     * Method used to receive a command result update.
     * @param commandResult command result
     */
    @Override
    public void receiveCommandResultUpdate(CommandResult commandResult) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().updateCommandResult(commandResult);
            }
        });
    }

    /**
     * Method used to receive an existing games update.
     * @param existingGamesPlayerNumber existing games player number
     * @param existingGamesTokenColor  existing games token color
     */
    @Override
    public void receiveExistingGamesUpdate(Map<Integer, Integer> existingGamesPlayerNumber, Map<Integer, List<TokenColor>> existingGamesTokenColor) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.LOBBY_SCENE)) {
                StageController.getController().displayExistingGames(existingGamesPlayerNumber, existingGamesTokenColor);
            }

        });
    }

    /**
     * Method used to receive winners update.
     * @param winners winners
     */
    @Override
    public void receiveWinnersUpdate(List<String> winners) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            StageController.setScene(SceneType.GAME_ENDED_SCENE);
            StageController.getController().displayWinners(winners);
        });
    }

    /**
     * Method used to notify the player he has received a full chat update.
     * @param chatMessages full chat update
     */
    @Override
    public void receiveFullChatUpdate(List<ChatMessage> chatMessages) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE)) {
                StageController.getController().setFullChat(chatMessages);
            }
        });
    }

    /**
     * Method used to show the game id.
     * @param gameId game id
     */
    @Override
    public void receiveGameIdUpdate(int gameId) {
        Platform.runLater(() -> {
            if(StageController.getController() == null) {
                // starting phase
                return;
            }
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().setGameId(gameId);
            }
        });
    }

    /**
     * Method used to show a connection update.
     * @param nickname   nickname
     * @param connection true if the player is connected, false otherwise
     */
    @Override
    public void receiveConnectionUpdate(String nickname, boolean connection) {
        Platform.runLater(() -> {
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().receiveConnectionUpdate(nickname, connection);
            }
        });
    }

    /**
     * Method used to show a stall update.
     * @param nickname nickname
     * @param stall true if the player is stalled, false otherwise
     */
    @Override
    public void receiveStallUpdate(String nickname, boolean stall) {
        Platform.runLater(() -> {
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().receiveStallUpdate(nickname, stall);
            }
        });
    }

    /**
     * Method used to display game players.
     * @param nicknames map containing players and their token colors
     * @param connectionValues map containing players and their connection values
     * @param stallValues map containing players and their stall values
     */
    @Override
    public void receivePlayersUpdate(Map<String, TokenColor> nicknames, Map<String, Boolean> connectionValues, Map<String, Boolean> stallValues) {
        Platform.runLater(() -> {
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().receivePlayersUpdate(nicknames, connectionValues, stallValues);
            }
        });
    }

    /**
     * Method used to show secrete objectives.
     * @param nickname nickname
     * @param secretObjective secreteObjective
     */
    @Override
    public void receiveSecretObjectives(String nickname, List<ObjectiveCard> secretObjective) {
        // don't send to anyone
    }

    /**
     * Method used to stop the ui when a disconnection occurred.
     */
    @Override
    public void stopUi() {
        Platform.runLater(() -> {
            if(StageController.getCurrentSceneType().equals(SceneType.PLAYER_SCENE) ||
                    StageController.getCurrentSceneType().equals(SceneType.OTHER_PLAYER_SCENE)) {
                StageController.getController().displayDisconnection();
            }
        });
    }
}
