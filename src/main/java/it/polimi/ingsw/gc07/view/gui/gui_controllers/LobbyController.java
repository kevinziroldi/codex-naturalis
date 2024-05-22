package it.polimi.ingsw.gc07.view.gui.gui_controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class LobbyController extends GuiController implements Initializable {
    @FXML
    protected TextField numPlayers;
    @FXML
    protected RadioButton newGame;
    @FXML
    protected RadioButton joinGame;
    @FXML
    protected AnchorPane joinPane;
    @FXML
    protected Button next;
    @FXML
    protected Button back;
    /**
     * list of string composed with: gameId +++ numOfPlayers
     */
    @FXML
    protected ListView<String> gameList;

    @FXML
    protected TextField gameId;
    @FXML
    protected Text errGameId;

    protected Map<Integer,Integer> games;

    @FXML
    protected TextField tokenColor;
    @FXML
    protected Text errTokenColor;
    @FXML
    protected Text errNumPlayer;

    @FXML
    protected Text textTokenColor;

    @FXML
    protected AnchorPane newPane;

    @FXML
    protected void onContinueButtonClick() {
        if(newGame.isSelected()){
            try {
                if(parseInt(numPlayers.getText())>0&&parseInt(numPlayers.getText())<5)
                {
                    if(tokenColor.getText().equalsIgnoreCase("RED")||tokenColor.getText().equalsIgnoreCase("BLUE")||tokenColor.getText().equalsIgnoreCase("GREEN")||tokenColor.getText().equalsIgnoreCase("YELLOW"))
                    {
                        Platform.runLater(() -> {
                            try {
                                StageController.setScene("/it/polimi/ingsw/gc07/fxml/waitingRoom.fxml", "Waiting room");
                            } catch (IOException e) {
                                // TODO
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    else {
                        numPlayers.clear();
                        tokenColor.clear();
                        errTokenColor.setVisible(true);
                    }
                }
                else {
                    numPlayers.clear();
                    tokenColor.clear();
                    errNumPlayer.setVisible(true);
                }
            }
            catch (NumberFormatException e) {
                numPlayers.clear();
                tokenColor.clear();
                errNumPlayer.setVisible(true);
            }
        }
        else if(joinGame.isSelected()) {
            try {
                if(games.containsKey(parseInt(gameId.getText())))
                {
                    if(tokenColor.getText().equalsIgnoreCase("RED")||tokenColor.getText().equalsIgnoreCase("BLUE")||tokenColor.getText().equalsIgnoreCase("GREEN")||tokenColor.getText().equalsIgnoreCase("YELLOW"))
                    {
                        //TODO: join del player al game
                        Platform.runLater(() -> {
                            try {
                                StageController.setScene("/it/polimi/ingsw/gc07/fxml/waitingRoom.fxml", "Waiting room");
                            } catch (IOException e) {
                                // TODO
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    else{
                        gameId.clear();
                        tokenColor.clear();
                        errTokenColor.setVisible(true);
                    }
                }
                else{
                    gameId.clear();
                    tokenColor.clear();
                    errGameId.setVisible(true);
                }
            }catch (NumberFormatException e) {
                gameId.clear();
                tokenColor.clear();
                errGameId.setVisible(true);
            }
        }
    }

    @FXML
    protected void clickedNew() {
        tokenColor.clear();
        gameId.clear();
        numPlayers.clear();

        textTokenColor.setVisible(true);
        tokenColor.setVisible(true);

        newPane.setVisible(true);

        newGame.setVisible(false);
        joinGame.setVisible(false);
        next.setVisible(true);
        back.setVisible(true);
    }

    @FXML
    protected void clickedJoin() {
        tokenColor.clear();
        gameId.clear();
        numPlayers.clear();

        textTokenColor.setVisible(true);
        tokenColor.setVisible(true);

        joinGame.setVisible(false);
        newGame.setVisible(false);

        joinPane.setVisible(true);
        gameId.clear();

        next.setVisible(true);
        back.setVisible(true);
    }

    @FXML
    protected void cleanErrGameId(KeyEvent actionEvent) {
        errGameId.setVisible(false);

    }

    @FXML
    protected void cleanErrTokenColor(KeyEvent actionEvent) {
        errTokenColor.setVisible(false);
    }

    @FXML
    protected void cleanErrNumPlayer(KeyEvent actionEvent) {
        errNumPlayer.setVisible(false);
    }



    @FXML
    protected void onBackButtonClick() {
        errTokenColor.setVisible(false);
        errNumPlayer.setVisible(false);
        errGameId.setVisible(false);

        textTokenColor.setVisible(false);
        tokenColor.setVisible(false);

        joinGame.setVisible(true);
        newGame.setVisible(true);
        joinGame.setSelected(false);
        newGame.setSelected(false);

        joinPane.setVisible(false);
        errGameId.setVisible(false);
        errTokenColor.setVisible(false);

        newPane.setVisible(false);

        next.setVisible(false);
        back.setVisible(false);
    }


        @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        games = new HashMap<Integer,Integer>();
        games.put(0,3);
    }
}
