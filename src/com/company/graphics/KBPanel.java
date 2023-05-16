package com.company.graphics;

import com.company.game.util.ImageLoader;
import com.company.game.EndGame;
import com.company.game.GameManager;
import com.company.game.Player;
import com.company.game.Settlement;
import com.company.game.board.Board;
import com.company.game.board.Coordinate;
import com.company.game.board.Hex;
import com.company.game.board.LocationTile;
import com.company.game.decks.KBDeck;
import com.company.game.types.LocationType;
import com.company.game.util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;

public class KBPanel extends JPanel implements MouseMotionListener, MouseListener {
    private GameManager gm;
    private Board board;

    private BufferedImage background;
    private BufferedImage backOfCard;
    private BufferedImage[] playerSlots;
    private BufferedImage startPlayerToken;
    private BufferedImage turnIndicator;

    private Button endTurnButton;
    private Button useTileButton;

    private KBFrame frame;
    private KBScoring scoring;

    public KBPanel(GameManager gameManager, KBFrame frame) {
        addMouseListener(this);
        addMouseMotionListener(this);
        this.frame = frame;
        gm = gameManager;
        board = gm.getBoard();

        try {
            // The background image is src/com/company/images/Background.png
            background = ImageLoader.obj.loadImage("Background.png");
            backOfCard = ImageLoader.obj.loadImage("GreenBackOfCard.png");
            startPlayerToken = ImageLoader.obj.loadImage("start_token.png");
            turnIndicator = ImageLoader.obj.loadImage("NeonFrame.png");

            playerSlots = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                playerSlots[i] = ImageLoader.obj.loadImage("Player" + (i + 1) + "Slot.png");
            }

            endTurnButton = new Button(1600/2-100, 860, 200, 70, ImageLoader.obj.loadImage("EndTurnButtonEnabled.png"), ImageLoader.obj.loadImage("EndTurnButtonDisabled.png"));
            // useTileButton = new Button(571 + 200, 855, 200, 80, ImageIO.read(this.getClass().getResourceAsStream("../images/UseTileButtonEnabled.png")), ImageIO.read(this.getClass().getResourceAsStream("../images/UseTileButtonDisabled.png")));
        } catch (Exception e) {
            System.out.println("Error loading background image");
        }

        setHexCoords();
        repaint();

        // Open scoring screen
        scoring = new KBScoring(gm);
    }

    public void paint(Graphics g) {
        scoring.updateScores();
        g.drawImage(background, 0, 0, 2000, 1500, null);

        drawPlayerSlots(g);
        drawRemainingSettlements(g);
        drawKingdomBuilderCards(g);
        drawResourceCards(g);
        drawBoardPieces(g);
        drawHexes(g);
        drawButtons(g);
        drawTokens(g);
    }


    public void drawTokens(Graphics g) {
       // Draw hexagon
        Player[] players = gm.getPlayers();
        g.drawImage(startPlayerToken, 145, 105, (int)(86/1.7), (int) (83/1.7), null);

        g.setColor(Color.BLACK);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 18));

        int s = 40;
        int w = 40;
        int h = 50;

        for (Player player: players) {
            HashMap<Rectangle, LocationTile> locationTileMap = new HashMap<>();

            for (int i = 0; i < player.getLocationTiles().size(); i++) {
                Rectangle rect = null;
                LocationTile locationTile = player.getLocationTiles().get(i);
                BufferedImage image = locationTile.getImage();

                switch (player.getPlayerNumber()) {
                    case 0:
                        if (i < 4) {
                            rect = new Rectangle(60 + (s*i), 150, w, h);
                            g.drawImage(image, 60 + (s*i), 150, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 60 + (40*i) + 12, 150 + 30);
                        } else {
                            rect = new Rectangle(60 + (s*(i-4)), 193, w, h);
                            g.drawImage(image, 60 + (s*(i-4)), 193, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 60 + (40*(i-4)) + 12, 193 + 30);

                        }
                        break;
                    case 1:
                        if (i < 4) {
                            rect = new Rectangle(60 + (s*i), 650, w, h);
                            g.drawImage(image, 60 + (s*i), 650, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 60 + (40*i) + 12, 650 + 30);
                        } else {
                            rect = new Rectangle(60 + (s*(i-4)), 650+43, w, h);
                            g.drawImage(image, 60 + (s*(i-4)), 650+43, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 60 + (40*(i-4)) + 12, 650+43 + 30);
                        }
                        break;
                    case 2:
                        if (i < 4) {
                            rect = new Rectangle(1375 + (s*i), 150, w, h);
                            g.drawImage(image, 1375 + (s*(i)), 150, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 1375 + (40*(i)) + 12, 150 + 30);
                        } else {
                            rect = new Rectangle(1375 + (s*(i-4)), 193, w, h);
                            g.drawImage(image, 1375 + (s*(i-4)), 193, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 1375 + (40*(i-4)) + 12, 193 + 30);
                        }
                        break;
                    case 3:
                        if (i < 4) {
                            rect = new Rectangle(1375 + (s*i), 650, w, h);
                            g.drawImage(image, 1375 + (s*i), 650, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 1375 + (40*i) + 12, 650 + 30);
                        } else {
                            rect = new Rectangle(1375 + (s*(i-4)), 650+43, w, h);
                            g.drawImage(image, 1375 + (s*(i-4)), 650+43, w, h, null);
                            //g.drawString(locationTile.getType().toString().substring(0, 2), 1375 + (40*(i-4)) + 12, 650+43 + 30);
                        }
                        break;
                }

                locationTileMap.put(rect, locationTile);
            }

            player.setLocationTileButtonMap(locationTileMap);

        }

        HashMap<LocationType, Integer> locationTileCount = Board.locationTileCount;

        int count = 0;
        int x = 1255;
        int y = 350;
        for (LocationType locationType: locationTileCount.keySet()) {
            BufferedImage image = Board.locationTileImages.get(locationType);
            g.setColor(Color.WHITE);
            g.setFont(new Font("TimesRoman", Font.BOLD, 36));
            if (count < 2) {
                g.drawImage(image, (x+(count*160)), 350, (int) (w*1.5), (int) (h*1.5), null);
                g.drawString("x" + locationTileCount.get(locationType).toString(), (x+(count*160)) + 70, 350 + (int) (h*1.5/2+10));
            } else {
                g.drawImage(image, (x+((count-2)*160)), 350+120, (int) (w*1.5), (int) (h*1.5), null);
                g.drawString("x" + locationTileCount.get(locationType).toString(), (x+((count-2)*160)) + 70, 350+120 + (int) (h*1.5/2+10));
            }

            count++;
        }
    }

    public void drawResourceCards(Graphics g) {
        Player[] players = gm.getPlayers();
        int width = (int) (130 * 0.75);
        int height = (int) (200 * 0.75);

        for (int i = 0; i < 4; i++) {
            if (gm.getCurrentPlayer().getPlayerNumber() == i) {
                switch (i) {
                    case 0:
                        g.drawImage(players[i].getTerrainCardImage(), 240, 160, width, height, null);
                        break;
                    case 1:
                        g.drawImage(players[i].getTerrainCardImage(), 240, 650, width, height, null);
                        break;
                    case 2:
                        g.drawImage(players[i].getTerrainCardImage(), 1270, 160, width, height, null);
                        break;
                    case 3:
                        g.drawImage(players[i].getTerrainCardImage(), 1270, 650, width, height, null);
                        break;
                }
            } else {
                try {
                    switch (i) {
                        case 0:
                            g.drawImage(backOfCard, 240, 160, width, height, null);
                            break;
                        case 1:
                            g.drawImage(backOfCard, 240, 650, width, height, null);
                            break;
                        case 2:
                            g.drawImage(backOfCard, 1270, 160, width, height, null);
                            break;
                        case 3:
                            g.drawImage(backOfCard, 1270, 650, width, height, null);
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Error loading back of card image");
                }

            }
        }

    }

    public void drawButtons(Graphics g) {
        endTurnButton.draw(g);

        // useTileButton.draw(g);
    }

    public void drawKingdomBuilderCards(Graphics g) {
        KBDeck deck = gm.getKbDeck();
        BufferedImage[] cards = deck.getSelectedKBImages();

        for (int i = 0; i < 3; i++) {
            g.drawImage(cards[i], 10 + (i * 125), 350, 130, 200, null);
        }
    }

    public void drawRemainingSettlements(Graphics g) {
        int width = 45 + 25;
        int height = 48 + 15;

        Player[] players = gm.getPlayers();

        int numOfSettlementsLeft = 3 - gm.getPlacedSettlements();
        int player = gm.getTurn();

        BufferedImage settlementImage = players[player].getSettlementImage();
        switch (player) {
            case 0:
                for (int i = 0; i < numOfSettlementsLeft; i++) {
                    g.drawImage(settlementImage, 56 + (i * 52), 233, width, height, null);
                }
                break;
            case 1:
                for (int i = 0; i < numOfSettlementsLeft; i++) {
                    g.drawImage(settlementImage, 56 + (i * 52), 727, width, height, null);
                }
                break;
            case 2:
                for (int i = 0; i < numOfSettlementsLeft; i++) {
                    g.drawImage(settlementImage, 1400 + (i * 52), 233, width, height, null);
                }
                break;
            case 3:
                for (int i = 0; i < numOfSettlementsLeft; i++) {
                    g.drawImage(settlementImage, 1400 + (i * 52), 727, width, height, null);
                }
                break;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 23));

        for (Player p : players) {
            switch (p.getPlayerNumber()) {
                case 0:
                    g.drawString("Count: " + (40 - players[0].getSettlementsBuilt()), 78, 310);
                    break;
                case 1:
                    g.drawString("Count: " + (40 - players[1].getSettlementsBuilt()), 78, 801);
                    break;
                case 2:
                    g.drawString("Count: " + (40 - players[2].getSettlementsBuilt()), 1429, 310);
                    break;
                case 3:
                    g.drawString("Count: " + (40 - players[3].getSettlementsBuilt()), 1429, 810);
                    break;
            }
        }
    }

    public void drawPlayerSlots(Graphics g) {
        g.drawImage(playerSlots[0], 44, 98, 312, 238, null);
        g.drawImage(playerSlots[1], 44, 587, 312, 238, null);
        g.drawImage(playerSlots[2], 1255, 98, 312, 238, null);
        g.drawImage(playerSlots[3], 1255, 587, 312, 238, null);
        //System.out.println(gm.getCurrentPlayer().getPlayerNumber());
        switch (gm.getCurrentPlayer().getPlayerNumber()) {
            case 0:
                g.drawImage(turnIndicator, 24, 80, 350, 278, null);
                break;
            case 2:
                g.drawImage(turnIndicator, 1235, 80, 350, 278, null);
                break;
            case 1:
                g.drawImage(turnIndicator, 24, 569, 350, 278, null);
                break;
            case 3:
                g.drawImage(turnIndicator, 1235, 569, 350, 278, null);
                break;
        }



    }

    public void drawHexes(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                Hex hex = Board.getHexes()[row][col];

                if (hex.getSettlement() != null) {
                    BufferedImage settlementImage = hex.getSettlement().getPlayer().getSettlementImage();
                    Hex[][] hexes = Board.getHexes();

                    g.drawImage(settlementImage, hexes[row][col].getPolygon().xpoints[0] + 4, hexes[row][col].getPolygon().ypoints[0] - 5, 40, 40, null);
                }
            }
        }

        if (gm.isMovingSettlement() && gm.getOldCoordinate() == null) {
            g.setColor(Color.RED);
            g2.setStroke(new BasicStroke(5));

            // For each of the hexes with the current player's settlements, draw a red outline
            for (Settlement settlement : gm.getCurrentPlayer().getSettlements()) {
                if (settlement == null) continue;
                g.drawPolygon(settlement.getHex().getPolygon());
            }

            return;
        } else if (gm.isMovingSettlement() && gm.getOldCoordinate() != null && gm.getNewCoordinate() == null) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.BLACK);
        }

        HashSet<Coordinate> availablePlacements = board.getAvailableSettlementPlacements(gm.getCurrentPlayer(), gm.selectedTerrainType);

//        System.out.println(availablePlacements);

        if (gm.getPlacedSettlements() == 3 && !gm.isUsingExtraAction()) {
            return;
        }

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                Hex hex = Board.getHexes()[row][col];

                if (availablePlacements.contains(hex.getCoordinate())) {
                    g.drawPolygon(hex.getPolygon());
                }
            }
        }
    }


    public void setHexCoords() {
        int[] x = new int[6];
        int[] y = new int[6];

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                x[0] = 403 + col * 40 + (row % 2 * 20);
                x[1] = 423 + col * 40 + (row % 2 * 20);
                x[2] = 443 + col * 40 + (row % 2 * 20);
                x[3] = 443 + col * 40 + (row % 2 * 20);
                x[4] = 423 + col * 40 + (row % 2 * 20);
                x[5] = 403 + col * 40 + (row % 2 * 20);

                y[0] = 165 + row * 35;
                y[1] = 155 + row * 35;
                y[2] = 165 + row * 35;
                y[3] = 189 + row * 35;
                y[4] = 200 + row * 35;
                y[5] = 189 + row * 35;

                Board.getHexes()[row][col].setPolygon(new Polygon(x, y, 6));
            }
        }
    }

    private void drawBoardPieces(Graphics g) {
        BufferedImage[] boardPieces = board.getBoardImages();
        g.drawImage(boardPieces[0], 370, 120, 942, 567, null);
        g.drawImage(boardPieces[1], 770, 120, 942, 567, null);
        g.drawImage(boardPieces[2], 370, 470, 942, 567, null);
        g.drawImage(boardPieces[3], 770, 470, 942, 567, null);
    }

    private boolean detectHexClick(MouseEvent e) {
        Hex[][] hexes = Board.getHexes();
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                Hex hex = hexes[row][col];
                if (hex.getPolygon().contains(e.getPoint())) {
                    if (gm.isMovingSettlement()) {
                        // If the old coordinate is null and this button contains a player's settlement, set the old coordinate to this button's coordinate
                        if (gm.getOldCoordinate() == null && hex.getSettlement() != null && hex.getSettlement().getPlayer().equals(gm.getCurrentPlayer())) {
                            gm.setOldCoordinate(new Coordinate(row, col));
                        } else if (gm.getOldCoordinate() != null && gm.getNewCoordinate() == null && hex.getSettlement() == null) {
                            gm.setNewCoordinate(new Coordinate(row, col));

                            if (!gm.moveSettlement()) {
                                JOptionPane.showMessageDialog(frame, "Invalid move");
                            }

                            gm.setOldCoordinate(null);
                            gm.setNewCoordinate(null);
                        }

                    } else {
                        gm.buildSettlement(hex.getCoordinate());
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private boolean detectLocationHexClick(MouseEvent e) {
        Player player = gm.getCurrentPlayer();

        for (Rectangle rectangle: player.getLocationTileButtonMap().keySet()) {
            if (rectangle.contains(e.getPoint())) {
                LocationTile locationTile = player.getLocationTileButtonMap().get(rectangle);
                LocationType type = locationTile.getType();

                if (!gm.getUnusedLocationTiles().contains(locationTile)) {
                    JOptionPane.showMessageDialog(frame, "You cannot use this location tile at this time.");
                    return false;
                }

                if ((type == LocationType.BARN || type == LocationType.HARBOR || type == LocationType.PADDOCK) && gm.getCurrentPlayer().getSettlementsBuilt() == 0) {
                    JOptionPane.showMessageDialog(frame, "You must build a settlement before using this location tile");
                    return false;
                }

                Logger.log("Clicked on " + locationTile.getType().toString());

                gm.useExtraAction(locationTile.getType(), locationTile.getCoordinate());
                return true;
            }
        }

        return false;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (detectHexClick(e)) {
            repaint();
        } else if (detectLocationHexClick(e)) {
            if (!gm.isCanUseExtraAction()) {
                JOptionPane.showMessageDialog(frame, "You cannot use an extra action at this time.");
            } else {
                repaint();
            }
        } else if ((gm.getCurrentPlayer().getSettlementsBuilt() == 40 || gm.getPlacedSettlements() == 3) && endTurnButton.isClicked(e.getX(), e.getY())) {
            gm.nextTurn();
            repaint();
        }
    }

    public void displayWinners(HashSet<Player> winners) {
        KBScoringFrame frame = new KBScoringFrame(gm);
        frame.setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
