package it.polimi.ingsw.gc07.model_view;
import it.polimi.ingsw.gc07.controller.Game;
import it.polimi.ingsw.gc07.model.cards.PlaceableCard;
import it.polimi.ingsw.gc07.model.conditions.CornerCoverageCondition;
import it.polimi.ingsw.gc07.model.conditions.ItemsCondition;
import it.polimi.ingsw.gc07.model.enumerations.CardType;
import it.polimi.ingsw.gc07.model.enumerations.GameObject;
import it.polimi.ingsw.gc07.model.enumerations.GameResource;

import java.util.List;

public class GameFieldView {
    /**
     * Player's starter card.
     */
    private PlaceableCard starterCard;

    /**
     * Each cell of the matrix contains a placeable card, or null if the place is empty.
     */
    private PlaceableCard[][] cardsContent;
    /**
     * Each cell of the matrix contains a boolean value:
     * false: the card has been placed face up
     * true: the card has been placed face down
     */
    private Boolean[][] cardsFace;
    /**
     * Matrix representing the placement order of cards.
     */
    private int [][] cardsOrder;
    /**
     * Constant value representing the max dimension of a player's game field.
     */
    private static final int dim = 81;
    public final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    /**
     * Constructor of the game field view.
     */
    public GameFieldView() {
        this.starterCard = null;
        this.cardsContent = null;
        this.cardsFace = null;
        this.cardsOrder = null;
    }

    /**
     * Setter method for the starter card.
     * @param starterCard starter card
     */
    public void setStarterCard(PlaceableCard starterCard) {
        this.starterCard = starterCard;
    }

    /**
     * Method to add a new card to the game field view.
     * @param x x
     * @param y y
     * @param card new card
     * @param way way
     * @param orderPosition order position
     */
    public void addCard(int x, int y, PlaceableCard card, boolean way, int orderPosition) {
        cardsContent[x][y] = card;
        cardsFace[x][y] = way;
        cardsOrder[x][y] = orderPosition;
    }

    public void printGameField(Game game) {
        //ricerca inizio righe/fine righe
        int startr = 0, endr = 80;
        for (int r = 0; r < 81; r++) {
            for (int c = 0; c < 81; c++) {
                if (cardsContent[r][c] != null) {
                    startr = r;
                    r = 81;
                    c = 81;
                }
            }
        }
        for (int r = 80; r > -1; r--) {
            for (int c = 0; c < 81; c++) {
                if (cardsContent[r][c] != null) {
                    endr = r;
                    r = -1;
                    c = 81;
                }
            }
        }
        //ricerca inizio colonna/fine colonna
        int startc = 0, endc = 80;
        for (int c = 0; c < 81; c++) {
            for (int r = 0; r < 81; r++) {
                if (cardsContent[r][c] != null) {
                    startc = c;
                    c = 81;
                    r = 81;
                }
            }
        }
        for (int c = 80; c > -1; c--) {
            for (int r = 0; r < 81; r++) {
                if (cardsContent[r][c] != null) {
                    endc = c;
                    c = -1;
                    r = 81;
                }
            }
        }
        printIndexColumn(startc, endc);
        for (int r = startr; r < endr + 1; r++) {
            for (int r1 = 0; r1 < 5; r1++) {
                for (int c = startc; c < endc + 1; c++) {
                    if (r1 == 0 || r1 == 4) {
                        printFirstLastRow(c, startc, game, r);
                    } else if (r1 == 1) {
                        printSecondRow(c, startc, game, r);
                    } else if (r1 == 2) {
                        printThirdRow(c, startc, game, r);
                    } else {
                        printFourthRow(c, startc, game, r);
                    }
                }
                System.out.print("\n");
            }
        }
    }

    public void printIndexColumn(int startc, int endc) {
        //stampa colonne
        for (int r = 0; r < 3; r++) {
            for (int c = startc; c < endc + 1; c++) {
                if (c == startc) {
                    if (r == 0) {
                        System.out.print(ANSI_BLACK_BACKGROUND + "+--+");
                    } else if (r == 1) {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|  |");
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "+--+");
                    }
                }
                if (r == 0) {
                    System.out.print(ANSI_BLACK_BACKGROUND + "+---------+");
                } else if (r == 1) {
                    if (c > 9) {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|   " + c + "    |");
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|   " + c + "     |");
                    }
                } else {
                    System.out.print(ANSI_BLACK_BACKGROUND + "+---------+");
                }
            }
            System.out.println();
        }
    }

    public void printFirstLastRow(int c, int startc, Game game, int r) {
        if (c == startc) {
            System.out.print(ANSI_BLACK_BACKGROUND + "+--+");
        }
        if ((r % 2 == 0 && c % 2 == 0) || (r % 2 != 0 && c % 2 != 0)) {
            if (cardsContent[r][c] != null) {
                System.out.print(ANSI_BLACK_BACKGROUND + "+---------+");
            } else {
                System.out.print(ANSI_BLACK_BACKGROUND + "           ");
            }
        } else {
            System.out.print(ANSI_BLACK_BACKGROUND + "           ");
        }
    }

    public void printSecondRow(int c, int startc, Game game, int r) {
        if (c == startc) {
            System.out.print(ANSI_BLACK_BACKGROUND + "|  |");
        }
        if ((r % 2 == 0 && c % 2 == 0) || (r % 2 != 0 && c % 2 != 0)) {
            PlaceableCard card = cardsContent[r][c];
            if (card != null) {
                int points = 0;
                if (!card.getType().equals(CardType.STARTER_CARD)) {
                    points = card.getPlacementScore(game.getPlayersGameField().get(game.getPlayers().get(game.getCurrPlayer()).getNickname()), r, c);
                }
                boolean cardway = cardsFace[r][c];
                if (cardway) {
                    if (card.getBackCorners()[0]) {
                        boolean print = false;
                        if (r > 0 && c > 0) {
                            int rapp = r - 1;
                            int capp = c - 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp]) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + "|        ");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getBackCornersContent()[0] == null) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[0].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "       ");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|        ");
                    }
                    if (card.getBackCorners()[1]) {
                        boolean print = false;
                        if (r > 0 && c < 80) {
                            int rapp = r - 1;
                            int capp = c + 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp] ) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + " |");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getBackCornersContent()[1] == null) {
                                System.out.print(ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[1].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "|");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + " |");
                    }
                } else {
                    if (card.getFrontCorners()[0]) {
                        boolean print = false;
                        if (r > 0 && c > 0) {
                            int rapp = r - 1;
                            int capp = c - 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp] ) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + "|   ");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getFrontCornersContent()[0] == null) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "  ");
                            } else if (card.getFrontCornersContent()[0].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "  ");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_BLACK_BACKGROUND + "   ");
                    }
                    //aggiunta punti e condizioni//
                    if (points > 0) {
                        System.out.print(ANSI_BLACK_BACKGROUND + points);
                        if (card.getScoringCondition() == null) {
                            System.out.print(ANSI_BLACK_BACKGROUND + " ");
                        } else if (card.getScoringCondition() instanceof CornerCoverageCondition) {
                            System.out.print(ANSI_BLACK_BACKGROUND + "C");
                        } else if (card.getScoringCondition() instanceof ItemsCondition) {
                            if (((ItemsCondition) card.getScoringCondition()).getNeededItems().getFirst().equals(GameObject.QUILL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "Q");
                            } else if (((ItemsCondition) card.getScoringCondition()).getNeededItems().getFirst().equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "I");
                            } else if (((ItemsCondition) card.getScoringCondition()).getNeededItems().getFirst().equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "M");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "  ");
                    }
                    System.out.print(ANSI_BLACK_BACKGROUND + "   ");
                    if (card.getFrontCorners()[1]) {
                        boolean print = false;
                        if (r > 0 && c < 80) {
                            int rapp = r - 1;
                            int capp = c + 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp] ) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + " |");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getFrontCornersContent()[1] == null) {
                                System.out.print(ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[1].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "|");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + " |");
                    }
                }
            } else {
                System.out.print(ANSI_BLACK_BACKGROUND + "           ");
            }
        } else {
            System.out.print(ANSI_BLACK_BACKGROUND + "           ");
        }
    }

    public void printThirdRow(int c, int startc, Game game, int r) {
        if (c == startc) {
            if (r > 9) {
                System.out.print(ANSI_BLACK_BACKGROUND + "|" + r + "|");
            } else {
                System.out.print(ANSI_BLACK_BACKGROUND + "|" + r + " |");
            }

        }
        if ((r % 2 == 0 && c % 2 == 0) || r % 2 != 0 && c % 2 != 0) {
            PlaceableCard card = cardsContent[r][c];
            if (card != null) {
                boolean cardway = cardsFace[r][c];
                if (cardway) {
                    List<GameResource> permanentResources = cardsContent[r][c].getPermanentResources();
                    if (permanentResources == null) {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|         |");
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|   ");
                        for (GameResource permanentResource : permanentResources) {
                            if (permanentResource.equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_RED_BACKGROUND + "F");
                            } else if (permanentResource.equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLUE_BACKGROUND + "A");
                            } else if (permanentResource.equals(GameResource.PLANT)) {
                                System.out.print(ANSI_GREEN_BACKGROUND + "P");
                            } else if (permanentResource.equals(GameResource.INSECT)) {
                                System.out.print(ANSI_PURPLE_BACKGROUND + "I");
                            }
                        }
                        for (int i = permanentResources.size(); i < 3; i++) {
                            System.out.print(ANSI_BLACK_BACKGROUND + " ");
                        }
                        System.out.print(ANSI_BLACK_BACKGROUND + "   |");
                    }
                } else {
                    System.out.print(ANSI_BLACK_BACKGROUND + "|         |");
                }
            } else {
                System.out.print(ANSI_BLACK_BACKGROUND + "           ");
            }
        } else {
            System.out.print(ANSI_BLACK_BACKGROUND + "           ");
        }
    }

    public void printFourthRow(int c, int startc, Game game, int r) {
        if (c == startc) {
            System.out.print(ANSI_BLACK_BACKGROUND + "|  |");
        }
        if ((r % 2 == 0 && c % 2 == 0) || (r % 2 != 0 && c % 2 != 0)) {
            PlaceableCard card = cardsContent[r][c];
            if (card != null) {
                boolean cardway = cardsFace[r][c];
                if (cardway) {
                    if (card.getBackCorners()[3]) {
                        boolean print = false;
                        if (r < 80 && c > 0) {
                            int rapp = r + 1;
                            int capp = c - 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp]) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + "|        ");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getBackCornersContent()[3] == null) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_WHITE_BACKGROUND + "|" + ANSI_RED_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "       ");
                            } else if (card.getBackCornersContent()[3].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "       ");
                            }
                        }

                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|        ");
                    }
                    if (card.getBackCorners()[2]) {
                        boolean print = false;
                        if (r < 80 && c < 80) {
                            int rapp = r + 1;
                            int capp = c + 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp]) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + " |");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getBackCornersContent()[2] == null) {
                                System.out.print(ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getBackCornersContent()[2].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "|");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + " |");
                    }
                } else {
                    if (card.getFrontCorners()[3]) {
                        boolean print = false;
                        if (r < 80 && c > 0) {
                            int rapp = r + 1;
                            int capp = c - 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp]) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + "|  ");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getFrontCornersContent()[3] == null) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + " ");
                            } else if (card.getFrontCornersContent()[3].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + " ");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + "|" + ANSI_BLACK_BACKGROUND + "  ");
                    }
                    if (card.getPlacementCondition() == null) {
                        System.out.print(ANSI_BLACK_BACKGROUND + "     ");
                    } else if (card.getPlacementCondition() instanceof ItemsCondition) {
                        for (int i = 0; i < 5; i++) {
                            if (i < ((ItemsCondition) card.getPlacementCondition()).getNeededItems().size()) {
                                if (((ItemsCondition) card.getPlacementCondition()).getNeededItems().get(i).equals(GameResource.PLANT)) {
                                    System.out.print(ANSI_GREEN_BACKGROUND + "P");
                                } else if (((ItemsCondition) card.getPlacementCondition()).getNeededItems().get(i).equals(GameResource.ANIMAL)) {
                                    System.out.print(ANSI_BLUE_BACKGROUND + "A");
                                } else if (((ItemsCondition) card.getPlacementCondition()).getNeededItems().get(i).equals(GameResource.INSECT)) {
                                    System.out.print(ANSI_PURPLE_BACKGROUND + "I");
                                } else if (((ItemsCondition) card.getPlacementCondition()).getNeededItems().get(i).equals(GameResource.FUNGI)) {
                                    System.out.print(ANSI_RED_BACKGROUND + "F");
                                }
                            } else {
                                System.out.print(ANSI_BLACK_BACKGROUND + " ");
                            }

                        }
                    }
                    System.out.print(ANSI_BLACK_BACKGROUND + " ");
                    if (card.getFrontCorners()[2]) {
                        boolean print = false;
                        if (r < 80 && c < 80) {
                            int rapp = r + 1;
                            int capp = c + 1;
                            if (cardsContent[rapp][capp] != null) {
                                if (cardsOrder[r][c] < cardsOrder[rapp][capp]) {
                                    System.out.print(ANSI_BLACK_BACKGROUND + " |");
                                    print = true;
                                }
                            }
                        }
                        if (!print) {
                            if (card.getFrontCornersContent()[2] == null) {
                                System.out.print(ANSI_WHITE_BACKGROUND + " " + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameResource.FUNGI)) {
                                System.out.print(ANSI_RED_BACKGROUND + "F" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameResource.ANIMAL)) {
                                System.out.print(ANSI_BLUE_BACKGROUND + "A" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameResource.PLANT)) {
                                System.out.print(ANSI_GREEN_BACKGROUND + "P" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameResource.INSECT)) {
                                System.out.print(ANSI_PURPLE_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameObject.QUILL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "Q" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameObject.INKWELL)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "I" + ANSI_BLACK_BACKGROUND + "|");
                            } else if (card.getFrontCornersContent()[2].equals(GameObject.MANUSCRIPT)) {
                                System.out.print(ANSI_YELLOW_BACKGROUND + "M" + ANSI_BLACK_BACKGROUND + "|");
                            }
                        }
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND + " |");
                    }
                }
            } else {
                System.out.print(ANSI_BLACK_BACKGROUND + "           ");
            }
        } else {
            System.out.print(ANSI_BLACK_BACKGROUND + "           ");
        }
    }
}
