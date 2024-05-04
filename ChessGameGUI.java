import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGameGUI extends JFrame {
    private Board board;

    public ChessGameGUI() {
        super("Chess Game");
        board = new Board();

        JPanel chessPanel = new JPanel(new GridLayout(8, 8));

        // Initialize the chessboard GUI
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(60, 60));
                button.addActionListener(new MoveListener(i, j));
                chessPanel.add(button);
            }
        }

        getContentPane().add(chessPanel);
    }

    private class MoveListener implements ActionListener {
        private int x, y;

        public MoveListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle the move when a button is clicked
            Move move = new Move(x, y);
            if (board.isValidMove(move)) {
                board.makeMove(move);
                // Update the GUI
            } else {
                JOptionPane.showMessageDialog(ChessGameGUI.this, "Invalid Move");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGameGUI gameGUI = new ChessGameGUI();
            gameGUI.setSize(480, 480);
            gameGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameGUI.setVisible(true);
        });
    }
}
