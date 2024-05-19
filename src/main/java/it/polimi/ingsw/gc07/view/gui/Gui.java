package it.polimi.ingsw.gc07.view.gui;

import it.polimi.ingsw.gc07.controller.GameState;
import it.polimi.ingsw.gc07.enumerations.CommandResult;
import it.polimi.ingsw.gc07.model.cards.DrawableCard;
import it.polimi.ingsw.gc07.model.cards.GoldCard;
import it.polimi.ingsw.gc07.model.cards.ObjectiveCard;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.chat.ChatMessage;
import it.polimi.ingsw.gc07.enumerations.TokenColor;
import it.polimi.ingsw.gc07.view.Ui;
import it.polimi.ingsw.gc07.view.gui.gui_controllers.StageController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public  class Gui extends Application implements Ui {

    @Override
    public void runCliJoinGame() {
        // TODO
        System.out.println("Loading gui ...");
        Application.launch(Gui.class);
    }

    @Override
    public void runCliGame() {
        // TODO
        //  sarà così?
    }

    @Override
    public void receiveScoreUpdate(Map<String, Integer> playerScores, Map<String, TokenColor> playerTokenColors) {
        // TODO
    }

    @Override
    public void receiveMessageUpdate(ChatMessage chatMessage) {
        // TODO
    }

    @Override
    public void receiveDecksUpdate(DrawableCard topResourceDeck, GoldCard topGoldDeck, List<DrawableCard> faceUpResourceCard, List<GoldCard> faceUpGoldCard, List<ObjectiveCard> commonObjective) {
        // TODO
    }

    @Override
    public void receiveGameFieldUpdate(PlaceableCard[][] cardsContent, Boolean[][] cardsFace, int[][] cardsOrder) {
        // TODO
    }

    @Override
    public void receiveStarterCardUpdate(PlaceableCard starterCard) {
        // TODO
    }

    @Override
    public void receiveCardHandUpdate(List<DrawableCard> hand, ObjectiveCard personalObjective) {
        // TODO
    }

    @Override
    public void receiveGeneralModelUpdate(GameState gameState, String currPlayer) {
        // TODO
    }

    @Override
    public void receivePenultimateRoundUpdate() {
        // TODO
    }

    @Override
    public void receiveAdditionalRoundUpdate() {
        // TODO
    }

    @Override
    public void receiveCommandResultUpdate(CommandResult commandResult) {
        // TODO
    }

    @Override
    public void receiveExistingGamesUpdate(Map<Integer, Integer> existingGames) {
        // TODO
    }

    @Override
    public void receiveWinnersUpdate(List<String> winners) {
        // TODO
    }

    @Override
    public void start(Stage stage) throws Exception {
        StageController.setup (stage, "/it/polimi/ingsw/gc07/fxml/lobby.fxml");
        stage.show();
    }

    public static void main (String[] args){
        launch();
    }
}
