package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class BlockGame extends JPanel implements KeyListener {
    private final int gridSize = 10;
    private final int cellSize = 40;
    private int[][] board = new int[gridSize][gridSize];
    private int score = 0;
    private int[][] currentBlock;
    private int blockX = 0, blockY = 0;
    private Random random = new Random();
    
    public BlockGame() {
        this.setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        this.setBackground(Color.WHITE);
        this.setFocusable(true);
        this.addKeyListener(this);
        currentBlock = getRandomBlock();
       
       
    }
    private int[][] getRandomBlock() {
        int[][][] blockShapes = {
            {{1, 1}, {1, 1}},  // Square
            {{1, 1, 1, 1}},    // Line (horizontal)
            {{1}, {1}, {1}, {1}},  // Line (vertical)
            {{1, 1, 0}, {0, 1, 1}}, // Z-shape
            {{0, 1, 1}, {1, 1, 0}}, // Reverse Z-shape
            {{1, 1, 1}, {0, 1, 0}}, // T-shape
            {{1, 1}, {1, 0}},  // L-shape
            {{1, 1}, {0, 1}}   // Reverse L-shape
            

        };
        return blockShapes[random.nextInt(blockShapes.length)];
    }

    private void placeBlock() {
        if (!canPlaceBlock(currentBlock, blockX, blockY)) return;
        
        for (int row = 0; row < currentBlock.length; row++) {
            for (int col = 0; col < currentBlock[row].length; col++) {
                if (currentBlock[row][col] == 1) {
                    board[blockY + row][blockX + col] = 1;
                }
            }
        }

        
        checkFullRows();
        currentBlock = getRandomBlock();
        blockX = 3;
        blockY = 0;
    }

    private void checkFullRows() {
        for (int row = 0; row < gridSize; row++) {
            boolean full = true;
            for (int col = 0; col < gridSize; col++) {
                if (board[row][col] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int r = row; r > 0; r--) {
                    board[r] = board[r - 1].clone();
                }
                board[0] = new int[gridSize]; // Empty top row
                score += 100;
            }
        }
    }

    private boolean canPlaceBlock(int[][] block, int x, int y) {
        for (int row = 0; row < block.length; row++) {
            for (int col = 0; col < block[row].length; col++) {
                if (block[row][col] == 1) {
                    int boardX = x + col;
                    int boardY = y + row;
                    if (boardX < 0 || boardX >= gridSize || boardY >= gridSize || board[boardY][boardX] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    
    private void drawBoard(Graphics g) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(col * cellSize, row * cellSize, cellSize, cellSize);
                if (board[row][col] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private void drawBlock(Graphics g, int[][] block, int x, int y) {
        g.setColor(Color.RED);
        for (int row = 0; row < block.length; row++) {
            for (int col = 0; col < block[row].length; col++) {
                if (block[row][col] == 1) {
                    g.fillRect((x + col) * cellSize, (y + row) * cellSize, cellSize, cellSize);
                    g.setColor(Color.RED);
                    g.drawRect((x + col) * cellSize, (y + row) * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canPlaceBlock(currentBlock, blockX - 1, blockY)) blockX--;
                break;
            case KeyEvent.VK_RIGHT:
                if (canPlaceBlock(currentBlock, blockX + 1, blockY)) blockX++;
                break;
            case KeyEvent.VK_DOWN:
                if (canPlaceBlock(currentBlock, blockX, blockY + 1)) blockY++;
                else placeBlock(); // Place block when it can't go further
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawBoard(g);
    drawBlock(g, currentBlock, blockX, blockY);
    
    // Draw the score at the top-left corner
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.drawString("Score: " + score, 10, 30);
}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Block Game");
        BlockGame game = new BlockGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}