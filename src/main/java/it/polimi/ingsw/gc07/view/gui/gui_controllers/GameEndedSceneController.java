package it.polimi.ingsw.gc07.view.gui.gui_controllers;

import it.polimi.ingsw.gc07.controller.GameState;
import it.polimi.ingsw.gc07.controller.CommandResult;
import it.polimi.ingsw.gc07.model.TokenColor;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.chat.ChatMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

/**
 * Controller of GAME_ENDED_SCENE.
 */
public class GameEndedSceneController implements GuiController {
    /**
     * Attribute that represents the general text.
     */
    @FXML
    protected Text generalText;
    /**
     * Attribute that represents the first winner.
     */
    @FXML
    protected Text winner1;
    /**
     * Attribute that represents the second winner.
     */
    @FXML
    protected Text winner2;
    /**
     * Attribute that represents the third winner.
     */
    @FXML
    protected Text winner3;
    /**
     * Attribute that represents the fourth winner.
     */
    @FXML
    protected Text winner4;
    /**
     * Attribute that represents the first winner's box.
     */
    @FXML
    protected HBox winnerBox1;
    /**
     * Attribute that represents the second winner's box.
     */
    @FXML
    protected HBox winnerBox2;
    /**
     * Attribute that represents the third winner's box.
     */
    @FXML
    protected HBox winnerBox3;
    /**
     * Attribute that represents the fourth winner's box.
     */
    @FXML
    protected HBox winnerBox4;
    /**
     * Attribute that exit button.
     */
    @FXML
    protected Button exitGame;

    /**
     * Method used to redirect the client.
     */
    @FXML
    protected void endGame() {
        Platform.runLater(() -> {
            StageController.getClient().setClientAlive(false);
            Platform.exit();
        });
    }

    /**
     * Method used to display a score update.
     * @param playerScore map containing players' scores
     * @param playerTokenColor map containing players' token colors
     */
    @Override
    public void updateScore(Map<String, Integer> playerScore, Map<String, TokenColor> playerTokenColor) {}

    /**
     * Method used to display a new chat message.
     * @param chat new chat message
     */
    @Override
    public void addMessage(ChatMessage chat) {}

    /**
     * Method used to display a deck update, containing cards a player can draw or see.
     * @param topResourceDeck top resource deck
     * @param topGoldDeck top gold deck
     * @param faceUpResourceCard face up cards resource
     * @param faceUpGoldCard face up cards gold
     * @param commonObjective common objective
     */
    @Override
    public void updateDecks(DrawableCard topResourceDeck, GoldCard topGoldDeck, List<DrawableCard> faceUpResourceCard, List<GoldCard> faceUpGoldCard, List<ObjectiveCard> commonObjective) {}

    /**
     * Method used to display a new game field update.
     * @param nickname nickname
     * @param cardsContent cards content
     * @param cardsFace cards face
     * @param cardsOrder cards order
     */
    @Override
    public void updateGameField(String nickname, PlaceableCard[][] cardsContent, Boolean[][] cardsFace, int[][] cardsOrder) {}

    /**
     * Method used to display the starter card.
     * @param starterCard starter card
     */
    @Override
    public void updateStarterCard(PlaceableCard starterCard) {}

    /**
     * Method used to display the new card hand.
     * @param hand new card hand
     * @param personalObjective personal objective
     */
    @Override
    public void updateCardHand(List<DrawableCard> hand, List<ObjectiveCard> personalObjective) {}

    /**
     * Method used to display updated game infos.
     * @param gameState game state
     * @param currPlayer current player
     */
    @Override
    public void updateGameInfo(GameState gameState, String currPlayer) {}

    /**
     * Method used to display a penultimate round update.
     */
    @Override
    public void setPenultimateRound() {}

    /**
     * Method used to display an additional round update.
     */
    @Override
    public void setAdditionalRound() {}

    /**
     * Method used to display the last command result.
     * @param commandResult command result
     */
    @Override
    public void updateCommandResult(CommandResult commandResult) {}

    /**
     * Method used to display a user existing and free games.
     * @param existingGamesPlayerNumber players number in existing games
     * @param existingGamesTokenColor take token colors in existing games
     */
    @Override
    public void displayExistingGames(Map<Integer, Integer> existingGamesPlayerNumber, Map<Integer, List<TokenColor>> existingGamesTokenColor) {}

    /**
     * Method used to display an update, containing winners.
     * @param winners winners
     */
    @Override
    public void displayWinners(List<String> winners) {
        // already on Platform.runLater
        if(winners.size()==1){
            generalText.setText("AND THE WINNER IS...");
        }
        else {
            generalText.setText("AND THE WINNERS ARE...");
        }
        int size = winners.size();
        if(size==1){
            winner1.setText(winners.getFirst());
            winnerBox1.setVisible(true);
            winnerBox2.setVisible(false);
            winnerBox3.setVisible(false);
            winnerBox4.setVisible(false);
        }
        else if(size==2){
            winner1.setText(winners.get(0));
            winnerBox1.setVisible(true);
            winner2.setText(winners.get(1));
            winnerBox2.setVisible(true);
            winnerBox3.setVisible(false);
            winnerBox4.setVisible(false);
        }
        else if(size==3){
            winner1.setText(winners.get(0));
            winnerBox1.setVisible(true);
            winner2.setText(winners.get(1));
            winnerBox2.setVisible(true);
            winner3.setText(winners.get(2));
            winnerBox3.setVisible(true);
            winnerBox4.setVisible(false);
        }
        else if(size==4){
            winner1.setText(winners.get(0));
            winnerBox1.setVisible(true);
            winner2.setText(winners.get(1));
            winnerBox2.setVisible(true);
            winner3.setText(winners.get(2));
            winnerBox3.setVisible(true);
            winner4.setText(winners.get(3));
            winnerBox4.setVisible(true);
        }
    }

    /**
     * Method used to set the nickname.
     * @param nickname nickname
     */
    @Override
    public void setNickname(String nickname) {}

    /**
     * Method used to set the full chat content.
     * @param chatMessages full chat content
     */
    @Override
    public void setFullChat(List<ChatMessage> chatMessages) {}

    /**
     * Method used to set the game id.
     * @param gameId game id
     */
    @Override
    public void setGameId(int gameId) {}

    /**
     * Method used to display a new connection value.
     * @param nickname nickname
     * @param value    new connection value
     */
    @Override
    public void receiveConnectionUpdate(String nickname, boolean value) {}

    /**
     * Method used to display a new stall value.
     * @param nickname nickname
     * @param value    new stall value
     */
    @Override
    public void receiveStallUpdate(String nickname, boolean value) {}

    /**
     * Method used to display players in the game.
     * @param nicknames nicknames
     * @param connectionValues connection values
     * @param stallValues stall values
     */
    @Override
    public void receivePlayersUpdate(Map<String, TokenColor> nicknames, Map<String, Boolean> connectionValues, Map<String, Boolean> stallValues) {}

    /**
     * Method used to display that a disconnection occurred and the Ui has to stop.
     */
    @Override
    public void displayDisconnection() {}
}
