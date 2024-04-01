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
import java.util.*;
import java.util.Random;

public class Game {
    /**
     * ID of the game.
     */
    private final int id;
    /**
     * State of the game.
     */
    private GameState state;
    /**
     * Number of players in the game, chose by the first player.
     */
    private final int playersNumber;
    /**
     * Map of players and their game field.
     */
    private final Map<String, GameField> playersGameField;
    /**
     * List of players and an integer for their order.
     */
    private final List<Player> players;
    /**
     * Integer value representing the position of the current player.
     */
    private int currPlayer;
    /**
     * Score track board of the game.
     */
    private final ScoreTrackBoard scoreTrackBoard;
    /**
     * Deck of resource cards.
     */
    private final ResourceCardsDeck resourceCardsDeck;
    /**
     * Deck of gold cards.
     */
    private final GoldCardsDeck goldCardsDeck;
    /**
     * Deck of objective cards.
     */
    private final PlayingDeck<ObjectiveCard> objectiveCardsDeck;
    /**
     * Deck of starter cards.
     */
    private final Deck<PlaceableCard> starterCardsDeck;
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
    private final Chat chat;

    /** Constructor of a Game with only the first player.
     *
     * @param playersNumber number of players
     * @param resourceCardsDeck deck of resource cards
     * @param goldCardsDeck deck of gold cards
     * @param objectiveCardsDeck deck of objective cards
     * @param starterCardsDeck deck of starter cards
     * @throws WrongNumberOfPlayersException exception thrown when the number of players is wrong
     */
    public Game(int id, int playersNumber, ResourceCardsDeck resourceCardsDeck,
                GoldCardsDeck goldCardsDeck, PlayingDeck<ObjectiveCard> objectiveCardsDeck,
                Deck<PlaceableCard> starterCardsDeck) throws WrongNumberOfPlayersException {
        this.id = id;
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
        this.chat = new Chat();
    }


    // ------------------------------
    // getters
    // ------------------------------

    /**
     * Getter for the game id.
     * @return game id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the state of the game.
     * @return the state of the game
     */
    public GameState getState() {
        return state;
    }

    /**
     * Method telling if a player is in a game.
     * @param nickname nickname of the player
     * @return true if the player is in the game
     */
    public boolean hasPlayer(String nickname) {
        boolean found = false;
        for(Player p: players){
            if(p.getNickname().equals(nickname)){
                found = true;
            }
        }
        return found;
    }


    // ------------------------------------
    // command pattern
    // ------------------------------------

    // getter e metodi da aggiungere per realizzare il command pattern
    // poi basta creare per ognuno dei metodi pubblici una classe e spostare il corpo del metodo
    // nel metodo execute
    // tutti i metodi privati verranno spostati in uno dei concrete command, mentre i metodi pubblici
    // verranno eliminati

    // questione da discutere: questi metodi per ora sono void, vorremo ritornare questa famosa
    // bandierina? in quel caso dovremo creare una classe enum che contiene tutti i possibili esiti
    // di fallimento

    int getPlayersNumber() {
        return playersNumber;
    }

    Map<String, GameField> getPlayersGameField() {
        return playersGameField;
    }

    List<Player> getPlayers() {
        return players;
    }

    int getCurrPlayer() {
        return currPlayer;
    }

    ScoreTrackBoard getScoreTrackBoard() {
        return scoreTrackBoard;
    }

    ResourceCardsDeck getResourceCardsDeck() {
        return resourceCardsDeck;
    }

    GoldCardsDeck getGoldCardsDeck() {
        return goldCardsDeck;
    }

    PlayingDeck<ObjectiveCard> getObjectiveCardsDeck() {
        return objectiveCardsDeck;
    }

    Deck<PlaceableCard> getStarterCardsDeck() {
        return starterCardsDeck;
    }

    boolean getTwentyPointsReached() {
        return twentyPointsReached;
    }

    boolean getAdditionalRound() {
        return additionalRound;
    }

    Chat getChat() {
        return chat;
    }

    //public void setCommand(GameCommand gameCommand) {
    // TODO
    //}

    public void execute() {
        // TODO
    }


    // -----------------------------------
    // public methods - called by users
    // -----------------------------------

    /**
     * Method to add a new player.
     * @param newPlayer player to add
     * @throws WrongStateException if the state of the game is wrong
     */
    public void addPlayer(Player newPlayer) throws WrongStateException{
        try{
            if(!state.equals(GameState.WAITING_PLAYERS)) {
                throw new WrongStateException();
            }

            newPlayer.addCardHand(resourceCardsDeck.drawCard());
            newPlayer.addCardHand(resourceCardsDeck.drawCard());
            newPlayer.addCardHand(goldCardsDeck.drawCard());
            newPlayer.setSecretObjective(objectiveCardsDeck.drawCard());

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
            // TODO no printStackTrace
            // non si può verificare perchè ho sicuramente abbastanza carte per 4 giocatori
            // e il gioco è appena iniziato
        }
    }

    /**
     * Method to place the starter card in a certain way.
     * @param nickname nickname of the player
     * @param way way of the starter card
     */
    public void placeStarterCard(String nickname, boolean way) {
        assert(playersGameField.containsKey(nickname)): "The player is not in the game";
        PlacementResult placementResult = playersGameField.get(nickname).placeCard(playersGameField.get(nickname).getStarterCard(), (GameField.getDim()-1)/2, (GameField.getDim()-1)/2, way);
        // TODO: bandierina per placementResult?
        // ritorno placementResult, oppure lo salvo in attributo ??
    }

    /**
     * Method that disconnect a player from the game
     * @param nickname : nickname of the player
     */
    // TODO può scendere a 0, cosa succede?
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
     * Method that reconnect a player from the game
     * @param nickname : nickname of the player
     */
    public void reconnectPlayer(String nickname){
        try{
            int pos = getPlayerByNickname(nickname);
            players.get(pos).setIsConnected(true);
        } catch (PlayerNotPresentException e) {
            e.printStackTrace();
        }
        // TODO: controllare se lo stato torna a PLAYING, dipende se il numero di giocatori connessi può scendere a 0
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
    public void placeCard(String nickname, DrawableCard card, int x, int y, boolean way) throws WrongPlayerException, CardAlreadyPresentException, CardNotPresentException, WrongStateException {
        if(!players.get(currPlayer).getNickname().equals(nickname)) {
            throw new WrongPlayerException();
        }
        if(!(players.get(currPlayer).getCurrentHand()).contains(card)){
            throw new CardNotPresentException();
        }
        if(!state.equals(GameState.PLAYING)){
            throw new WrongStateException();
        }
        PlacementResult result = playersGameField.get(nickname).placeCard(card,x,y,way);
        // TODO cosa me ne faccio di result ???
        // ritorno e/o salvo in un attributo ???
        players.get(currPlayer).removeCardHand(card);
        try {
            addPoints(nickname,x,y);
        } catch (PlayerNotPresentException e) {
            // TODO ????
        }
        boolean isStalled = true;
        // check if a card is placeable
        for(int i = 0; i < GameField.getDim() && isStalled; i++) {
            for (int j = 0; j < GameField.getDim() && isStalled; j++) {
                // check if the firs card (a casual card), is placeable on the back,
                // i.e. check only the indexes
                try {
                    result = players.get(getPlayerByNickname(nickname)).getCurrentHand().getFirst()
                            .isPlaceable(new GameField(playersGameField.get(nickname)), i, j, true);
                    if (result.equals(PlacementResult.SUCCESS)) {
                        isStalled = false;
                    }
                } catch (PlayerNotPresentException e) {
                    // TODO ????
                }
            }
        }
        players.get(currPlayer).setIsStalled(isStalled);
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
        if(!players.get(currPlayer).getNickname().equals(nickname)){
            throw new WrongPlayerException();
        }
        if(type.equals(CardType.OBJECTIVE_CARD) || type.equals(CardType.STARTER_CARD)) {
            throw new WrongCardTypeException();
        }
        players.get(currPlayer).addCardHand(resourceCardsDeck.drawCard());
        //List<DrawableCard> newHand = new ArrayList<>();
        //newHand.addAll(this.players.get(this.currPlayer).getCurrentHand());
        if(type.equals(CardType.RESOURCE_CARD)){
            players.get(currPlayer).addCardHand(resourceCardsDeck.drawCard());
            //newHand.add(this.resourceCardsDeck.drawCard());
        }
        if(type.equals(CardType.GOLD_CARD)){

            players.get(currPlayer).addCardHand(goldCardsDeck.drawCard());
            //newHand.add(this.goldCardsDeck.drawCard());
        }
        //this.players.get(this.currPlayer).setCurrentHand(newHand);
        try {
            changeCurrPlayer();
        }
        catch (WrongStateException | PlayerNotPresentException e)
        {
            e.printStackTrace();
            // TODO noooo
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
        if(!players.get(currPlayer).getNickname().equals(nickname)){
            throw new WrongPlayerException();
        }
        if(type.equals(CardType.OBJECTIVE_CARD) || type.equals(CardType.STARTER_CARD)) {
            throw new WrongCardTypeException();
        }
        //List<DrawableCard> newHand = new ArrayList<>();
        //newHand.addAll(this.players.get(this.currPlayer).getCurrentHand());
        if(type.equals(CardType.RESOURCE_CARD)){
            players.get(currPlayer).addCardHand(resourceCardsDeck.drawFaceUpCard(pos));
            //newHand.add(this.resourceCardsDeck.drawFaceUpCard(pos));
        }
        if(type.equals(CardType.GOLD_CARD)){
            players.get(currPlayer).addCardHand(goldCardsDeck.drawFaceUpCard(pos));
            //newHand.add(this.goldCardsDeck.drawFaceUpCard(pos));
        }
        //this.players.get(this.currPlayer).setCurrentHand(newHand);
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
    public GameResource revealBackDeckCard(CardType type) throws WrongCardTypeException, CardNotPresentException {
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
     * Method that adds a private message to the chat.
     * @param content content of the message
     * @param sender sender of the message
     */
    public void addChatPublicMessage(String content, String sender) {
        // list of players' nicknames
        List<String> playersNicknames = players.stream().map(p -> p.getNickname()).toList();
        if(playersNicknames.contains(sender)) {
            // adds message to the chat
            chat.addPublicMessage(content, sender, playersNicknames);
        }
        else{
            // TODO
            // chi passa questi parametri? cosa faccio se sender sbagliato?
        }
    }

    public void addChatPrivateMessage(String content, String sender, String receiver) {
        // list of players' nicknames
        List<String> playersNicknames = players.stream().map(p -> p.getNickname()).toList();
        if(playersNicknames.contains(sender) && playersNicknames.contains(receiver)) {
            // adds message to the chat
            chat.addPrivateMessage(content, sender, receiver, playersNicknames);
        }
        else{
            // TODO
            // chi passa questi parametri?
            // cosa faccio se sender sbagliato?
            // cosa faccio se receiver sbagliato?
        }
    }

    /**
     * Returns the last message of the chat for a certain player.
     * @return the last message of the chat
     */
    public Message getLastChatMessage(String receiver)  {
        Message message = chat.getLastMessage(receiver);
        if(message == null) {
            // TODO no return null!
            return null;
        }
        else
            return message;
    }

    /**
     * Returns the content of the chat for a certain player.
     * @return the list of the message in the chat
     */
    public List<Message> getChatContent(String receiver) {
        return chat.getContent(receiver);
    }


    // -----------------------------------
    // private methods - used to break complexity
    // -----------------------------------

    /**
     * Method telling if there are available places in the game.
     * @return true if no other player can connect to the game
     */
    private boolean isFull(){
        return players.size() == playersNumber;
    }

    /**
     * Method to set up the game: the first player is chosen and 4 cards (2 gold and 2 resource) are revealed.
     * @throws WrongStateException if the state of the game is wrong
     */
    private void setup() {
        assert(state.equals(GameState.WAITING_PLAYERS)): "The state is not waiting players";
        // choose randomly the first player
        Random random= new Random();
        this.currPlayer=random.nextInt(playersNumber);
        players.get(currPlayer).setFirst();
        try {
            //place 2 gold cards
            List<GoldCard> setUpGoldCardsFaceUp = new ArrayList<>();
            setUpGoldCardsFaceUp.add(goldCardsDeck.drawCard());
            setUpGoldCardsFaceUp.add(goldCardsDeck.drawCard());
            goldCardsDeck.setFaceUpCards(setUpGoldCardsFaceUp);
        } catch (CardNotPresentException e) {
            e.printStackTrace();
        }
        try {
            //place 2 resource card
            List<DrawableCard> setUpResourceCardsFaceUp = new ArrayList<>();
            setUpResourceCardsFaceUp.add(resourceCardsDeck.drawCard());
            setUpResourceCardsFaceUp.add(resourceCardsDeck.drawCard());
            resourceCardsDeck.setFaceUpCards(setUpResourceCardsFaceUp);
        }
        catch (CardNotPresentException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * method that returns the position of the player in the List players.
     * @param nickname: is the nickname of the player whose position is being searched.
     * @return the position of the player searched in the List players.
     * @throws PlayerNotPresentException: if the nickname is not present in the list players.
     */
    private int getPlayerByNickname(String nickname) throws PlayerNotPresentException {
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
    private void changeCurrPlayer () throws WrongStateException, CardNotPresentException, PlayerNotPresentException {
        if(!state.equals(GameState.PLAYING)) {
            throw new WrongStateException();
        }
        if(this.currPlayer==this.players.size()-1)
            this.currPlayer=0;
        else
            this.currPlayer++;
        if(this.twentyPointsReached)
        {
            if(players.get(currPlayer).isFirst()&&this.additionalRound)
            {
                this.state = GameState.GAME_ENDED;
                List<Player> winners = new ArrayList<>(computeWinner());
                //TODO: fare qualcosa con questo winner
            }
            else if(players.get(currPlayer).isFirst())
            {
                this.additionalRound=true;
            }
        }
        if(!players.get(currPlayer).isConnected())
        {
            changeCurrPlayer();
        }
        if(players.get(currPlayer).getIsStalled())
        {
            changeCurrPlayer();
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
        if(!players.get(currPlayer).getNickname().equals(nickname))
        {
            throw new WrongPlayerException();
        }
        assert (playersGameField.get(nickname).isCardPresent(x, y)) : "there isn't a Card in the x,y position";
        deltaPoints = playersGameField.get(nickname).getPlacedCard(x, y).getPlacementScore(playersGameField.get(nickname), x, y);
        if(deltaPoints + scoreTrackBoard.getScore(nickname) >= 20){
            twentyPointsReached = true;
            if((deltaPoints + scoreTrackBoard.getScore(nickname)) > 29){
                scoreTrackBoard.setScore(nickname, 29);
            }
            else{
                scoreTrackBoard.incrementScore(nickname, deltaPoints);
            }
        }
    }

    /**
     * method that compute the winner/s of the game.
     * @return the list of players who won the game.
     * @throws CardNotPresentException : if there isn't an objective faceUpCard on the board.
     * @throws PlayerNotPresentException : if the player is not present in the List players.
     */
    // TODO RIVEDERE
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
            realizedObjectives = objectiveCardsDeck.revealFaceUpCard(0).numTimesScoringConditionMet(playersGameField.get(players.get(i).getNickname()));
            //points counter for the 1st common objective
            deltapoints = objectiveCardsDeck.revealFaceUpCard(0).getObjectiveScore(playersGameField.get(players.get(i).getNickname()));
            realizedObjectives += objectiveCardsDeck.revealFaceUpCard(1).numTimesScoringConditionMet(playersGameField.get(players.get(i).getNickname()));
            //points counter for the 2nd common objective
            deltapoints += objectiveCardsDeck.revealFaceUpCard(1).getObjectiveScore(playersGameField.get(players.get(i).getNickname()));
            realizedObjectives += players.get(i).getSecretObjective().numTimesScoringConditionMet(playersGameField.get(players.get(i).getNickname()));
            //points counter for the secret objective
            deltapoints += players.get(i).getSecretObjective().getObjectiveScore(playersGameField.get(players.get(i).getNickname()));
            scoreTrackBoard.incrementScore(players.get(i).getNickname(), deltapoints);
            List<Player> playersCopy = new ArrayList<>(players);
            if (max <= scoreTrackBoard.getScore(playersCopy.get(i).getNickname())){
                max = scoreTrackBoard.getScore(playersCopy.get(i).getNickname());
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
}