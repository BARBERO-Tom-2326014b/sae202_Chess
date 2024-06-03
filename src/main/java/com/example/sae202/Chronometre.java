package com.example.sae202;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Chronometre {
    private long sec, min, totalSec = 0;
    private JLabel timeLabel;

    private String format(long value) {
        if (value < 10) {
            return "0" + value;
        }
        return Long.toString(value);
    }

    public void convertTime() {
        min = TimeUnit.SECONDS.toMinutes(totalSec);
        sec = totalSec - (min * 60);
        timeLabel.setText(format(min) + ":" + format(sec));
        totalSec--;
    }

    private void setTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        convertTime();
                        if (totalSec <= 0) {
                            timer.cancel();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);  // 1000ms = 1 second
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Chronometre");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        String[] times = {"1", "3", "5", "10", "15", "30"};
        JComboBox<String> timeComboBox = new JComboBox<>(times);

        JButton startButton = new JButton("Start");

        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Serif", Font.BOLD, 32));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                totalSec = 60 * Long.parseLong((String) timeComboBox.getSelectedItem());
                setTimer();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(new JLabel("Choix temps en minute:"));
        panel.add(timeComboBox);
        panel.add(startButton);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(timeLabel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Chronometre chronometre = new Chronometre();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chronometre.createAndShowGUI();
            }
        });
    }
}

