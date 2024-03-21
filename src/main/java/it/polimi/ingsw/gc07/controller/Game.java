package it.polimi.ingsw.gc07.controller;

import it.polimi.ingsw.gc07.exceptions.*;
import it.polimi.ingsw.gc07.model.GameField;
import it.polimi.ingsw.gc07.model.Player;
import it.polimi.ingsw.gc07.model.ScoreTrackBoard;
import it.polimi.ingsw.gc07.model.cards.*;
import it.polimi.ingsw.gc07.model.chat.Chat;
import it.polimi.ingsw.gc07.model.chat.Message;
import it.polimi.ingsw.gc07.model.decks.*;
import it.polimi.ingsw.gc07.model.enumerations.CardType;
import it.polimi.ingsw.gc07.model.enumerations.GameResource;
import it.polimi.ingsw.gc07.model.enumerations.GameState;
import it.polimi.ingsw.gc07.model.enumerations.TokenColor;
import java.util.*;
import java.util.Random;

public class Game {
    /**
     * State of the game.
     */
    private GameState state;
    /**
     * Number of players in the game, chose by the first player.
     */
    private int playersNumber;
    /**
     * Map of players and their game field.
     */
    private Map<String, GameField> playersGameField;
    /**
     * List of players and an integer for their order.
     */
    private List<Player> players;
    /**
     * Integer value representing the position of the current player.
     */
    private int currPlayer;
    /**
     * Score track board of the game.
     */
    private ScoreTrackBoard scoreTrackBoard;
    /**
     * Deck of resource cards.
     */
    private ResourceCardsDeck resourceCardsDeck;
    /**
     * Deck of gold cards.
     */
    private GoldCardsDeck goldCardsDeck;
    /**
     * Deck of objective cards.
     */
    private PlayingDeck<ObjectiveCard> objectiveCardsDeck;
    /**
     * Deck of starter cards.
     */
    private Deck<PlaceableCard> starterCardsDeck;
    /**
     * Boolean attribute, true if a player has reached 20 points.
     */
    private boolean twentyPointsReached;
    /**
     * Boolean attribute, if it is the additional round of the game.
     */
    private boolean additionalRound;
    /**
     * Chat of the game.
     */
    private Chat chat;

    /** Constructor of a Game with only the first player.
     *
     * @param playersNumber number of players
     * @param resourceCardsDeck deck of resource cards
     * @param goldCardsDeck deck of gold cards
     * @param objectiveCardsDeck deck of objective cards
     * @param starterCardsDeck deck of starter cards
     * @param nickname player to add to the game
     * @param tokenColor color of player's token
     * @param connectionType type of connection
     * @param interfaceType type of the interface
     * @param starterCardWay way of the starter card
     * @throws WrongNumberOfPlayersException exception thrown when the number of players is wrong
     */
    public Game(int playersNumber, ResourceCardsDeck resourceCardsDeck,
                GoldCardsDeck goldCardsDeck, PlayingDeck<ObjectiveCard> objectiveCardsDeck, Deck<PlaceableCard> starterCardsDeck,
                String nickname, TokenColor tokenColor, boolean connectionType, boolean interfaceType, boolean starterCardWay) throws WrongNumberOfPlayersException
    {
        this.state = GameState.WAITING_PLAYERS;
        if (playersNumber<2 || playersNumber>4)  {
            throw new WrongNumberOfPlayersException();
        }
        else{
            this.playersNumber = playersNumber;
        }
        this.players = new ArrayList<>();
        this.playersGameField = new HashMap<>();
        this.scoreTrackBoard = new ScoreTrackBoard();
        this.resourceCardsDeck = new ResourceCardsDeck(resourceCardsDeck);
        this.goldCardsDeck = new GoldCardsDeck(goldCardsDeck);
        this.objectiveCardsDeck = new PlayingDeck<>(objectiveCardsDeck);
        this.starterCardsDeck = new Deck<>(starterCardsDeck);
        this.currPlayer = 0;
        this.twentyPointsReached = false;
        this.additionalRound = false;
        try {
            addPlayer(nickname, tokenColor, connectionType, interfaceType);
        } catch (WrongStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the state of the game
     * @return the state of the game
     */
    public GameState getState() {
        return this.state;
    }

    /**
     * get the list of players
     * @return the players in the game
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    /**
     * get the current player
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return new Player(this.players.get(this.currPlayer));
    }

    /**
     * get the game field of the player
     * @param nickname : nickname of the player
     * @return the game field of the player
     * @throws PlayerNotPresentException exception thrown when a player is not present in the game
     */
    public GameField getGameField(String nickname) throws PlayerNotPresentException {
        if(!this.playersGameField.containsKey(nickname)) {
            throw new PlayerNotPresentException();
        }
        return new GameField(this.playersGameField.get(nickname));
    }

    /**
     * get the players' score
     * @param nickname : nickname of the player
     * @return the players' score
     * @throws PlayerNotPresentException exception thrown when a player is not present in the game
     */
    public int getScore(String nickname) throws PlayerNotPresentException {
        return this.scoreTrackBoard.getScore(nickname);
    }

    public void setStarterCardWay(String nickname, boolean way){
        try {
            placeCard(nickname, playersGameField.get(nickname).getStarterCard(), 40, 40, way);
        } catch (WrongPlayerException e) {
            throw new RuntimeException(e);
        } catch (CardAlreadyPresentException e) {
            throw new RuntimeException(e);
        } catch (CardNotPresentException e) {
            throw new RuntimeException(e);
        } catch (WrongStateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to add a new player.
     * @param nickname player to add to the game
     * @param tokenColor color of player's token
     * @param connectionType type of connection
     * @param interfaceType type of the interface
     * @throws WrongStateException if the state of the game is wrong
     */
    public void addPlayer(String nickname, TokenColor tokenColor, boolean connectionType, boolean interfaceType) throws WrongStateException{
        try{
            if(!state.equals(GameState.WAITING_PLAYERS)) {
                throw new WrongStateException();
            }
            List<DrawableCard> currentHand = new ArrayList<>();
            currentHand.add(this.resourceCardsDeck.drawCard());
            currentHand.add(this.resourceCardsDeck.drawCard());
            currentHand.add(this.goldCardsDeck.drawCard());
            ObjectiveCard secretObjective = this.objectiveCardsDeck.drawCard();
            Player newPlayer = new Player(nickname, tokenColor, connectionType, interfaceType, currentHand, secretObjective);
            PlaceableCard starterCard = starterCardsDeck.drawCard();
            GameField gameField = new GameField(starterCard);
            players.add(newPlayer);
            playersGameField.put(newPlayer.getNickname(), gameField);
            scoreTrackBoard.addPlayer(newPlayer.getNickname());
            if (isFull()) {
                setup();
                state = GameState.PLAYING;
            }
        } catch (CardNotPresentException e) {
            e.printStackTrace();
        } catch (PlayerAlreadyPresentException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to set up the game: the first player is chosen and 4 cards(2 gold and 2 resource) are revealed
     * @throws WrongStateException if the state of the game is wrong
     */
    private void setup() throws WrongStateException {
        if (!state.equals(GameState.WAITING_PLAYERS)) {
            throw new WrongStateException();
        }
        // chose randomly the first player
        Random random= new Random();
        this.currPlayer=random.nextInt(playersNumber);
        getCurrentPlayer().setFirst();
        try {
            //place 2 gold cards
            List<GoldCard> setUpGoldCardsFaceUp = new ArrayList<>();
            setUpGoldCardsFaceUp.add(this.goldCardsDeck.drawCard());
            setUpGoldCardsFaceUp.add(this.goldCardsDeck.drawCard());
            this.goldCardsDeck.setFaceUpCards(setUpGoldCardsFaceUp);
        } catch (CardNotPresentException e) {
            e.printStackTrace();
        }
        try {
            //place 2 resource card
            List<DrawableCard> setUpResourceCardsFaceUp = new ArrayList<>();
            setUpResourceCardsFaceUp.add(this.resourceCardsDeck.drawCard());
            setUpResourceCardsFaceUp.add(this.resourceCardsDeck.drawCard());
            this.resourceCardsDeck.setFaceUpCards(setUpResourceCardsFaceUp);
        }
        catch (CardNotPresentException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Method telling if there are available places in the game.
     * @return true if no other player can connect to the game
     */
    public boolean isFull(){
        return players.size() == playersNumber;
    }

    /**
     * method that disconnect a player from the game
     * @param nickname : nickname of the player
     */
    public void disconnectPlayer(String nickname){
        try{
            int pos = getPlayerByNickname(nickname);
            players.get(pos).setIsConnected(false);
            int numPlayersConnected = 0;
            for (Player p : players){
                if (p.isConnected()){
                    numPlayersConnected++;
                }
            }
            if (numPlayersConnected <= 1){
                state = GameState.WAITING_RECONNECTION;
                //TODO gestire attesa riconnessione/timeout
            }
        } catch (PlayerNotPresentException e) {
            e.printStackTrace();
        }
    }
    /**
     * method that reconnect a player from the game
     * @param nickname : nickname of the player
     */
    public void reconnectPlayer(String nickname){
        try{
            int pos = getPlayerByNickname(nickname);
            players.get(pos).setIsConnected(true);
        } catch (PlayerNotPresentException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that returns the position of the player in the List players.
     * @param nickname: is the nickname of the player whose position is being searched.
     * @return the position of the player searched in the List players.
     * @throws PlayerNotPresentException: if the nickname is not present in the list players.
     */
    public int getPlayerByNickname(String nickname) throws PlayerNotPresentException {
        for (int i = 0; i < playersNumber; i++){
            if(players.get(i).getNickname().equals(nickname)){
                return i;
            }
        }
        throw new PlayerNotPresentException();
    }

    /**
     * method that change the current player, if it's the last turn and all the players
     * played the same amount of turn it computes the winner
     * if a player is disconnect from the game he loose the turn
     * if a player is stalled he will be skipped
     * @throws WrongStateException if the state of the game is wrong
     */
    public void changeCurrPlayer () throws WrongStateException, CardNotPresentException, PlayerNotPresentException {
        if(!state.equals(GameState.PLAYING)) {
            throw new WrongStateException();
        }
        if(this.currPlayer==this.players.size()-1)
            this.currPlayer=0;
        else
            this.currPlayer++;
        if(this.twentyPointsReached)
        {
            if(getCurrentPlayer().isFirst()&&this.additionalRound)
            {
                this.state=GameState.GAME_ENDED;
                List<Player> winners = new ArrayList<>(computeWinner());
                //TODO: fare qualcosa con questo winner
            }
            else if(getCurrentPlayer().isFirst())
            {
                this.additionalRound=true;
            }
        }
        if(!getCurrentPlayer().isConnected())
        {
            changeCurrPlayer();
        }
        if(getCurrentPlayer().getIsStalled())
        {
            changeCurrPlayer();
        }
    }

    /**
     * method that place a card in the game field of the current player
     * this method also remove the card placed from the hand of the current player and calls the method that compute the points scored by placing the card
     * @param nickname : nickname of the player
     * @param card : card that the player wants to play
     * @param x : row of the matrix (gameField)
     * @param y: column of the matrix (gameField)
     * @param way : face of the card
     * @throws WrongPlayerException : if a player that is not the current player try to place a card
     * @throws CardAlreadyPresentException : if a player play a card that is already present in the gameField
     * @throws CardNotPresentException : if the card that the player wants to play is not in his hands
     */
    public void placeCard(String nickname, PlaceableCard card, int x, int y, boolean way) throws WrongPlayerException, CardAlreadyPresentException, CardNotPresentException, WrongStateException {
        if(!getCurrentPlayer().getNickname().equals(nickname)) {
            throw new WrongPlayerException();
        }
        if(!(getCurrentPlayer().getCurrentHand()).contains(card)){
            throw new WrongPlayerException();
        }
        if(!state.equals(GameState.PLAYING)){
            throw new WrongStateException();
        }
        try {
            getGameField(nickname).placeCard(card,x,y,way);
            List<DrawableCard> newHand = new ArrayList<>(getCurrentPlayer().getCurrentHand());
            newHand.remove(card);
            getCurrentPlayer().setCurrentHand(newHand);
            addPoints(nickname,x,y);
            boolean isStalled=true;
            for(int a=0;a<81&&isStalled;a++)
                for(int b=0;b<81&&isStalled;b++)
                {
                    PlacementResult result = getGameField(nickname).checkAvailability(a,b);
                    if(result.equals(PlacementResult.SUCCESS))
                    {
                        isStalled=false;
                    }
                }
            getCurrentPlayer().setIsStalled(isStalled);
        }catch (PlayerNotPresentException e)
        {
            e.printStackTrace();
        } catch (IndexesOutOfGameFieldException e) {
            throw new RuntimeException(e);
        } catch (PlacingConditionNotMetException e) {
            throw new RuntimeException(e);
        } catch (MultipleCornersCoveredException e) {
            throw new RuntimeException(e);
        } catch (NotLegitCornerException e) {
            throw new RuntimeException(e);
        } catch (NoCoveredCornerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * method that add points to a player and check if a player is reaching 20 points.
     * @param nickname: nickname of the player
     * @param x: where the card is placed in the matrix
     * @param y: where the card is placed in the matrix
     * @throws WrongPlayerException : if the player is not the current player.
     * @throws PlayerNotPresentException : if the player is not present in the List players.
     * @throws CardNotPresentException: if there isn't a card in the specified position of the game field.
     */
    private void addPoints(String nickname, int x, int y) throws WrongPlayerException, CardNotPresentException, PlayerNotPresentException, WrongStateException {
        if (!state.equals(GameState.PLAYING)){
            throw new WrongStateException();
        }
        int deltaPoints;
        if(!getCurrentPlayer().getNickname().equals(nickname))
        {
            throw new WrongPlayerException();
        }
        if (!playersGameField.get(nickname).isCardPresent(x, y)){
            throw new CardNotPresentException();
        }
        if(playersGameField.get(nickname).getCardWay(x, y)){
            return;
        }
        if(playersGameField.get(nickname).getPlacedCard(x, y).getScoringCondition() == null){
            deltaPoints = playersGameField.get(nickname).getPlacedCard(x, y).getScore();
            if(deltaPoints + getScore(nickname) >= 20){
                twentyPointsReached = true;
                if((deltaPoints + getScore(nickname)) > 29){
                    scoreTrackBoard.setScore(nickname, 29);
                }
                else{
                    scoreTrackBoard.incrementScore(nickname, deltaPoints);
                }
            }
        }
        else{
            deltaPoints = playersGameField.get(nickname).getPlacedCard(x, y).getScoringCondition().numTimesMet(this.playersGameField.get(nickname)) * this.playersGameField.get(nickname).getPlacedCard(x, y).getScore();
            if(deltaPoints + getScore(nickname) >= 20){
                twentyPointsReached = true;
                if((deltaPoints + getScore(nickname)) > 29){
                    scoreTrackBoard.setScore(nickname, 29);
                }
                else{
                    scoreTrackBoard.incrementScore(nickname, deltaPoints);
                }
            }
        }
    }

    /**
     * method that compute the winner/s of the game.
     * @return the list of players who won the game.
     * @throws CardNotPresentException : if there isn't an objective faceUpCard on the board.
     * @throws PlayerNotPresentException : if the player is not present in the List players.
     */
    private List<Player> computeWinner() throws CardNotPresentException, PlayerNotPresentException, WrongStateException {
        if (state.equals(GameState.GAME_ENDED)){
            throw new WrongStateException();
        }
        List<Player> winners = new ArrayList<>();
        int deltapoints;
        int max = 0;
        int realizedObjectives;
        int maxRealizedObjective = 0;
        for (int i=0; i>=0 && i< players.size(); i++){
            realizedObjectives = objectiveCardsDeck.revealFaceUpCard(0).getScoringCondition().numTimesMet(playersGameField.get(players.get(i).getNickname()));
            //points counter for the 1st common objective
            deltapoints = objectiveCardsDeck.revealFaceUpCard(0).getScoringCondition().numTimesMet(playersGameField.get(players.get(i).getNickname())) * objectiveCardsDeck.revealFaceUpCard(0).getScore();
            realizedObjectives += objectiveCardsDeck.revealFaceUpCard(1).getScoringCondition().numTimesMet(playersGameField.get(players.get(i).getNickname()));
            //points counter for the 2nd common objective
            deltapoints += objectiveCardsDeck.revealFaceUpCard(1).getScoringCondition().numTimesMet(playersGameField.get(players.get(i).getNickname())) * objectiveCardsDeck.revealFaceUpCard(1).getScore();
            realizedObjectives += players.get(i).getSecretObjective().getScoringCondition().numTimesMet(playersGameField.get(players.get(i).getNickname()));
            //points counter for the secret objective
            deltapoints += players.get(i).getSecretObjective().getScoringCondition().numTimesMet(playersGameField.get(players.get(i).getNickname())) * players.get(i).getSecretObjective().getScore();
            scoreTrackBoard.incrementScore(players.get(i).getNickname(), deltapoints);
            List<Player> playersCopy = getPlayers();
            if (max <= getScore(playersCopy.get(i).getNickname())){
                max = getScore(playersCopy.get(i).getNickname());
                if (realizedObjectives >= maxRealizedObjective){
                    if (realizedObjectives == maxRealizedObjective){
                        winners.add(playersCopy.get(i));
                    }
                    else{
                        winners.clear();
                        winners.add(playersCopy.get(i));
                        maxRealizedObjective = realizedObjectives;
                    }
                }
            }
        }
        return winners;
    }

    /**
     * method that allows a player to draw one card from a GoldCardDeck or a ResourceCardDeck.
     * @param nickname: nickname of a player.
     * @param type: type of the card a user wants to draw.
     * @throws WrongCardTypeException: if the Type given is not correct
     * @throws CardNotPresentException: if the List of faceUpCards doesn't have a card in the given position
     * @throws WrongPlayerException: if the player is not the current player
     */
    public void drawDeckCard(String nickname, CardType type) throws WrongCardTypeException, CardNotPresentException, WrongPlayerException {
        if(!getCurrentPlayer().getNickname().equals(nickname)){
            throw new WrongPlayerException();
        }
        if(type.equals(CardType.OBJECTIVE_CARD) || type.equals(CardType.STARTER_CARD)) {
            throw new WrongCardTypeException();
        }
        List<DrawableCard> newHand = new ArrayList<>();
        newHand.addAll(this.players.get(this.currPlayer).getCurrentHand());
        if(type.equals(CardType.RESOURCE_CARD)){
            newHand.add(this.resourceCardsDeck.drawCard());
        }
        if(type.equals(CardType.RESOURCE_CARD)){
            newHand.add(this.goldCardsDeck.drawCard());
        }
        this.players.get(this.currPlayer).setCurrentHand(newHand);
        try {
            changeCurrPlayer();
        }
        catch (WrongStateException | PlayerNotPresentException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * method that allows a player to draw one of two faceUp cards of a given type.
     * @param nickname: nickname of a player.
     * @param type: type of the card a user wants to draw.
     * @param pos: position in the List of faceUpCards
     * @throws WrongCardTypeException: if the Type given is not correct
     * @throws CardNotPresentException: if the List of faceUpCards doesn't have a card in the given position
     * @throws WrongPlayerException: if the player is not the current player
     */
    public void drawFaceUpCard(String nickname, CardType type, int pos) throws WrongCardTypeException, CardNotPresentException, WrongPlayerException, PlayerNotPresentException {
        if(!getCurrentPlayer().getNickname().equals(nickname)){
            throw new WrongPlayerException();
        }
        if(type.equals(CardType.OBJECTIVE_CARD) || type.equals(CardType.STARTER_CARD)) {
            throw new WrongCardTypeException();
        }
        List<DrawableCard> newHand = new ArrayList<>();
        newHand.addAll(this.players.get(this.currPlayer).getCurrentHand());
        if(type.equals(CardType.RESOURCE_CARD)){
            newHand.add(this.resourceCardsDeck.drawFaceUpCard(pos));
        }
        if(type.equals(CardType.GOLD_CARD)){
            newHand.add(this.goldCardsDeck.drawFaceUpCard(pos));
        }
        this.players.get(this.currPlayer).setCurrentHand(newHand);
        try {
            changeCurrPlayer();
        }
        catch (WrongStateException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * method that reveal the face up card in position pos
     * @param type type of the card
     * @param pos position of the card
     * @return the card that we want to reveal
     * @throws WrongCardTypeException if the card is a starter card
     * @throws CardNotPresentException if there aren't face up cards
     */
    public Card revealFaceUpCard(CardType type, int pos) throws WrongCardTypeException, CardNotPresentException {
        if(type.equals(CardType.STARTER_CARD))
        {
            throw new WrongCardTypeException();
        }
        if(type.equals(CardType.GOLD_CARD))
            return this.goldCardsDeck.revealFaceUpCard(pos);
        else if(type.equals(CardType.RESOURCE_CARD))
            return this.resourceCardsDeck.revealFaceUpCard(pos);
        else
            return this.objectiveCardsDeck.revealFaceUpCard(pos);
    }

    /**
     * method that reveal the back of the card on top of the deck
     * @param type : type of the card
     * @return the GameResource that represent the back of the card
     * @throws WrongCardTypeException   if we reveal the back of a starter card or the back of an objective card
     * @throws CardNotPresentException if the deck is empty
     */
    public GameResource revealBackDeckCard(CardType type) throws WrongCardTypeException, CardNotPresentException
    {
        if(type.equals(CardType.STARTER_CARD) || type.equals(CardType.OBJECTIVE_CARD))
        {
          throw new WrongCardTypeException();
        }
        if(type.equals(CardType.GOLD_CARD))
            return this.goldCardsDeck.revealBackDeckCard();
        else
            return this.resourceCardsDeck.revealBackDeckCard();
    }

    // metodi chat
    /**
     * Method that adds the message to the chat.
     * @param content
     * @param sender
     * @param isPublic
     * @param receiver
     * @throws InvalidReceiverException
     */
    public void addChatMessage(String content, String sender, boolean isPublic, String receiver) throws InvalidReceiverException {
        // list of players' nicknames
        List<String> playersNicknames = players.stream().map(p -> p.getNickname()).toList();
        // adds message to the chat
        chat.addMessage(content, sender, isPublic, receiver, playersNicknames);
    }

    /**
     * Returns the last message of the chat for a certain player.
     * @return the last message of the chat
     * @throws EmptyChatException if the chat is empty
     */
    public Message getLastChatMessage(String receiver) throws EmptyChatException {
        return chat.getLastMessage(receiver);
    }

    /**
     * Returns the content of the chat for a certain player.
     * @return the list of the message in the chat
     */
    public List<Message> getChatContent(String receiver) {
        return chat.getContent(receiver);
    }
}
