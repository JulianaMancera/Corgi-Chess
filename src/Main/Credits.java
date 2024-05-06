package Main;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Credits extends JFrame {   

    private JFrame mainFrame; // Reference to the main frame

    public Credits(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {
        setSize(1100, 800);

        ImageIcon backgroundImage = new ImageIcon("D:\\Downloads\\Chess_3\\Chess\\res\\piece\\creds.png");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        setContentPane(backgroundLabel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Close the credits frame and show the main frame
                dispose(); // Close the credits frame
                mainFrame.setVisible(true); // Show the main frame
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
