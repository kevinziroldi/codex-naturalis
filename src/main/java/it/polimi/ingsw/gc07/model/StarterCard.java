package it.polimi.ingsw.gc07.model;

import java.util.List;
import java.util.ArrayList;

/**
 * class that represents the starter cards of the game
 */
public class StarterCard extends PlaceableCard{
    /**
     * attribute that shows which permanent game items the card has (on the back)
     */
    private final List<GameResource> permanentResources;
    /**
     * attribute that shows which corners the back of the card has
     */
    private final boolean[] backCorners;
    /**
     * attribute that shows which game items the back of the card has
     */
    private final GameItem[] backCornersContent;

    /**
     * constructor of the class StarterCard
     * @param cardID : id of the card
     * @param cardType : type of the card
     * @param frontCorners : corners that the front of the card has
     * @param frontCornersContent : game items that the front of the card has
     * @param permanentResources : permanent game items that the card has
     * @param backCorners : corners that the back of the card has
     * @param backCornersContent : game items that the back of the card has
     */
    public StarterCard(int cardID, CardType cardType, boolean[] frontCorners,
                       GameItem[] frontCornersContent, List<GameResource> permanentResources,
                       boolean[] backCorners, GameItem[] backCornersContent) {
        super(cardID, cardType, frontCorners, frontCornersContent);
        this.permanentResources = new ArrayList<>(permanentResources);
        boolean[] backCornersCopy = new boolean[4];
        for(int i = 0; i < 4; i++){
            backCornersCopy[i] = backCorners[i];
        }
        this.backCorners = backCornersCopy;
        GameItem[] backCornersContentCopy = new GameItem[4];
        for(int i = 0; i < 4; i++){
            backCornersContentCopy[i] = backCornersContent[i];
        }
        this.backCornersContent = backCornersContentCopy;
    }

    /**
     * getter method of the attribute permanentResources
     * @return an arrayList of GameResources
     */
    public  List<GameResource> getPermanentResources(){
        return new ArrayList<>(permanentResources);
    }

    /**
     * getter method of the attribute backCorners
     * @return an array of booleans
     */
    public boolean[] getBackCorners()
    {
        boolean[] backCornersCopy = new boolean[4];
        for(int i = 0; i < 4; i++){
            backCornersCopy[i] = backCorners[i];
        }
        return backCornersCopy;
    }

    /**
     * getter method of the attribute backCornersContent
     * @return an array of GameItems
     */
    public GameItem[] getBackCornersContent()
    {
        GameItem[] backCornersContentCopy = new GameItem[4];
        for(int i = 0; i < 4; i++){
            backCornersContentCopy[i] = backCornersContent[i];
        }
        return backCornersContentCopy;
    }
}
