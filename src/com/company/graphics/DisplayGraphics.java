package com.company.graphics;

import com.company.game.GameManager;
import com.company.game.Player;
import com.company.game.board.Board;
import com.company.game.board.Coordinate;
import com.company.game.board.Hex;
import com.company.game.board.LocationTile;
import com.company.game.types.LocationType;
import com.company.game.util.Logger;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DisplayGraphics {
    private static JFrame frame;
    private static JPanel panel;
    private static JButton[][] buttons;
    private static GameManager gm = null;
    private static JPanel locationTileButtonGroup;
    private static ArrayList<JButton> locationTileButtons;

    public DisplayGraphics(GameManager gameManager) {
        gm = gameManager;
        Hex[][] hexes = Board.hexes;

        // Create the frame and panel
        frame = new JFrame("Kingdom Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(2000, 600)); // Set preferred size to enable horizontal scrolling

        // Create button group in the north of the panel. The first row is the Extra Action button and the second row is the terrain cards
        JPanel group = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel extraActionLabel = new JLabel("Extra Actions");
        extraActionLabel.setHorizontalAlignment(JLabel.CENTER);

        group.add(extraActionLabel);

        // Unknown number of columns, so use a JPanel with a GridLayout
        locationTileButtonGroup = new JPanel(new GridLayout(1, 0, 5, 5));
        group.add(locationTileButtonGroup);

        // Add the button group to the panel
        panel.add(group, BorderLayout.NORTH);

        // Create the buttons for the game board
        JPanel gameBoardPanel = new JPanel(new GridLayout(20, 20, 5, 5));
        buttons = new JButton[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                buttons[i][j] = new JButton();
                // Set text of button to the coordinate and terrain type
                buttons[i][j].setText("(" + i + ", " + j + ") " + hexes[i][j].getTerrainType());
                gameBoardPanel.add(buttons[i][j]);
                // Add event listener to the button
                int finalJ = j;
                int finalI = i;
                buttons[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gm.isMovingSettlement()) {
                            // If the old coordinate is null and this button contains a player's settlement, set the old coordinate to this button's coordinate
                            if (gm.getOldCoordinate() == null && hexes[finalI][finalJ].getSettlement() != null && hexes[finalI][finalJ].getSettlement().getPlayer().equals(gm.getCurrentPlayer())) {
                                gm.setOldCoordinate(new Coordinate(finalI, finalJ));
                                buttons[finalI][finalJ].setText("Old Coordinate");
                            } else if (gm.getOldCoordinate() != null && gm.getNewCoordinate() == null && hexes[finalI][finalJ].getSettlement() == null) {
                                gm.setNewCoordinate(new Coordinate(finalI, finalJ));

                                if (!gm.moveSettlement()) {
                                    buttons[gm.getOldCoordinate().getX()][gm.getOldCoordinate().getY()].setText("(" + gm.getOldCoordinate().getX() + ", " + gm.getOldCoordinate().getY() + ") " + hexes[gm.getOldCoordinate().getX()][gm.getOldCoordinate().getY()].getTerrainType());
                                    JOptionPane.showMessageDialog(frame, "Invalid move");
                                } else {
                                    buttons[finalI][finalJ].setText("New Coordinate");
                                }

                                // updateBoard();
                                gm.showSettlements();
                                gm.setOldCoordinate(null);
                                gm.setNewCoordinate(null);
                            }
                            return;
                        }

                        gm.buildSettlement(new Coordinate(finalI, finalJ));
                        gm.showSettlements();
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
                });
            }
        }

        // Add the game board panel to the center of the panel
        panel.add(gameBoardPanel, BorderLayout.CENTER);

        // Wrap the panel in a scroll pane and add it to the frame
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);

        // Set the size of the frame and display the window
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gm.showSettlements();

        locationTileButtons = new ArrayList<>();
    }


    public static void updateLocationTileButtonGroup() {
        // Get all location tiles from current player
        ArrayList<LocationTile> locationTiles = gm.getCurrentPlayer().getLocationTiles();

        // Remove all buttons from the panel
        locationTileButtonGroup.removeAll();

        // Add all location tiles to the panel
        locationTileButtonGroup.setLayout(new GridLayout(1, locationTiles.size() + 1, 5, 5));

//        JButton moveSettlement = new JButton("Move Settlement");
//        moveSettlement.addMouseListener(new MouseListener() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                Logger.log("Clicked on Move Settlement");
//                gm.useExtraAction(LocationType.BARN);
//
//                // Disable the button
//                moveSettlement.setEnabled(false);
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                // Do nothing
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                // Do nothing
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                // Do nothing
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                // Do nothing
//            }
//        });
//
//        locationTileButtonGroup.add(moveSettlement);

        // Create a button for each location tile
        for (LocationTile locationTile: locationTiles) {
            if (locationTile.getType().equals(LocationType.CASTLE)) continue;

            JButton button = new JButton(locationTile.getType().toString());
            // Location tile event listener
            button.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Logger.log("Clicked on " + locationTile.getType().toString());
                    // gm.useExtraAction(locationTile.getType());

                    // Disable the button
                    button.setEnabled(false);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // Do nothing
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // Do nothing
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // Do nothing
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Do nothing
                }
            });

            locationTileButtonGroup.add(button);
        }

        // Repaint the panel
        locationTileButtonGroup.revalidate();
        locationTileButtonGroup.repaint();
    }

    public static void showAvailableButtons(HashSet<Coordinate> coordinates) {
        // Loop through all the buttons and set the background color to green if the coordinate is in the HashSet
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                if (Board.getTile(new Coordinate(row, col)).hasLocationTile()) {
                    buttons[row][col].setBackground(Color.RED);
                } else if (coordinates.contains(new Coordinate(row, col))) {
                    buttons[row][col].setBackground(Color.GREEN);
                } else if (Board.getTile(new Coordinate(row, col)).getSettlement() != null && Board.getTile(new Coordinate(row, col)).getSettlement().getPlayer().equals(gm.getCurrentPlayer())) {
                    if (gm.isMovingSettlement()) {
                        buttons[row][col].setBackground(Color.BLUE);
                    } else {
                        buttons[row][col].setBackground(Color.YELLOW);
                    }
                } else {
                    buttons[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }

    public static void updateBoard() {
        // Loop through all the buttons and update their text. If the hex has a settlement, set the text to "S"
        Hex[][] hexes = Board.hexes;

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                if (hexes[row][col].getSettlement() != null) {
                    buttons[row][col].setText("S-" + hexes[row][col].getSettlement().getPlayer().getColor());
                } else {
                    buttons[row][col].setText("(" + row + ", " + col + ") " + hexes[row][col].getTerrainType());
                }
            }
        }

        frame.repaint();
    }

    public static void displayWinners(HashSet<Player> players) {
        // do a popup window with the winners
        String winners = "";
        for (Player player : players) {
            winners += player.getColor() + " ";
        }

        JOptionPane.showMessageDialog(frame, "The winners are: " + winners);
    }

}
