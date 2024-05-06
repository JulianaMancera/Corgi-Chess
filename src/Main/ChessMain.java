package Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessMain {
    public static void main(String[] args) {
        JFrame window = new JFrame("Corgi Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        ImageIcon icon = new ImageIcon("res/piece/CorgiCuter.png"); // Change the path to your icon file
        window.setIconImage(icon.getImage());

        ImageIcon backgroundImage = new ImageIcon("res/piece/Start.png");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(null);

        JButton startButton = new JButton("");
        startButton.setBounds(659, 400, 322, 80);
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GamePanel gamePanel = new GamePanel();
                window.setContentPane(gamePanel);
                window.pack();
                gamePanel.launchGame();
            }
        });

        JButton CredsBut = new JButton("");
        CredsBut.setBounds(10, 695, 200, 75);
        CredsBut.setOpaque(false);
        CredsBut.setContentAreaFilled(false);
        CredsBut.setBorderPainted(false);
        CredsBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Credits creditsFrame = new Credits(window);
            }
        });

        backgroundLabel.add(startButton);
        backgroundLabel.add(CredsBut);


        window.setSize(1100, 800);
        window.setContentPane(backgroundLabel);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}