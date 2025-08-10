/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package snakesandladders;

/**
 *
 * @author Pc
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SnakesAndLadders extends JFrame {

    private JPanel boardPanel;
    private JButton rollDiceButton;
    private JLabel statusLabel;

    private final int BOARD_SIZE = 10;
    private final int CELL_SIZE = 60;

    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    private java.util.List<Player> players;
    private int currentPlayerIndex = 0;
    private final Random rand = new Random();

    public SnakesAndLadders() {
        setTitle("Snakes and Ladders");
        setSize(CELL_SIZE * BOARD_SIZE + 200, CELL_SIZE * BOARD_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initBoard();
        initGameLogic();

        setVisible(true);
    }

    private void initBoard() {
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw grid and cell numbers
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        int x = col * CELL_SIZE;
                        int y = (BOARD_SIZE - 1 - row) * CELL_SIZE;
                        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                        int cellNum = getCellNumber(row, col);
                        g.drawString(String.valueOf(cellNum), x + 5, y + 15);
                    }
                }

                // Draw ladders
                g.setColor(Color.BLACK);
                for (Map.Entry<Integer, Integer> ladder : ladders.entrySet()) {
                    int[] start = getCoordinates(ladder.getKey());
                    int[] end = getCoordinates(ladder.getValue());
                    g.drawLine(start[0] + CELL_SIZE / 2, start[1] + CELL_SIZE / 2,
                               end[0] + CELL_SIZE / 2, end[1] + CELL_SIZE / 2);
                    g.drawString("🪜", start[0] + CELL_SIZE / 3, start[1] + CELL_SIZE / 2);
                }

                // Draw snakes
                g.setColor(Color.BLUE);
                for (Map.Entry<Integer, Integer> snake : snakes.entrySet()) {
                    int[] start = getCoordinates(snake.getKey());
                    int[] end = getCoordinates(snake.getValue());
                    g.drawLine(start[0] + CELL_SIZE / 2, start[1] + CELL_SIZE / 2,
                               end[0] + CELL_SIZE / 2, end[1] + CELL_SIZE / 2);
                    g.drawString("🐍", start[0] + CELL_SIZE / 3, start[1] + CELL_SIZE / 2);
                }

                // Draw players
                for (int i = 0; i < players.size(); i++) {
                    Player p = players.get(i);
                    int[] coords = getCoordinates(p.position);
                    g.setColor(p.color);
                    g.fillOval(coords[0] + i * 15, coords[1] + 20, 15, 15);
                }
            }
        };

        boardPanel.setPreferredSize(new Dimension(CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE));

        JPanel controlPanel = new JPanel();
        rollDiceButton = new JButton("Roll Dice");
        statusLabel = new JLabel("Start Game");
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(rollDiceButton, BorderLayout.NORTH);
        controlPanel.add(statusLabel, BorderLayout.SOUTH);

        rollDiceButton.addActionListener(e -> playTurn());

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }

    private void initGameLogic() {
        // Define snakes and ladders
        snakes = new HashMap<>();
        snakes.put(99, 54);
        snakes.put(70, 55);
        snakes.put(52, 42);
        snakes.put(25, 2);

        ladders = new HashMap<>();
        ladders.put(6, 25);
        ladders.put(11, 40);
        ladders.put(17, 69);
        ladders.put(60, 89);

        // Initialize players
        players = new ArrayList<>();
        players.add(new Player("Player 1", Color.RED));
        players.add(new Player("Player 2", Color.BLUE));
    }

    private void playTurn() {
        Player current = players.get(currentPlayerIndex);
        int diceRoll = rand.nextInt(6) + 1;

        statusLabel.setText(current.name + " rolled a " + diceRoll);

        int newPosition = current.position + diceRoll;

        if (newPosition > 100) {
            statusLabel.setText(current.name + " rolled too high!");
        } else {
            if (snakes.containsKey(newPosition)) {
                newPosition = snakes.get(newPosition);
                statusLabel.setText(current.name + " got bitten by a snake!");
            } else if (ladders.containsKey(newPosition)) {
                newPosition = ladders.get(newPosition);
                statusLabel.setText(current.name + " climbed a ladder!");
            }

            current.position = newPosition;

            if (newPosition == 100) {
                statusLabel.setText(current.name + " wins!");
                rollDiceButton.setEnabled(false);
            }
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        boardPanel.repaint();
    }

    private int getCellNumber(int row, int col) {
        int base = row * BOARD_SIZE;
        if (row % 2 == 0) {
            return base + col + 1;
        } else {
            return base + (BOARD_SIZE - col);
        }
    }

    private int[] getCoordinates(int position) {
        if (position < 1) position = 1;
        if (position > 100) position = 100;

        int row = (position - 1) / BOARD_SIZE;
        int col = (position - 1) % BOARD_SIZE;

        if (row % 2 == 1) {
            col = BOARD_SIZE - 1 - col;
        }

        int x = col * CELL_SIZE;
        int y = (BOARD_SIZE - 1 - row) * CELL_SIZE;
        return new int[]{x, y};
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakesAndLadders::new);
    }

    // Inner class for players
    class Player {
        String name;
        int position = 1;
        Color color;

        Player(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }
}