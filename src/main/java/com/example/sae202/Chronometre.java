package com.example.sae202;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Chronometre {
    private long sec, min, totalSec = 0;
    private JLabel timeLabel;
    private Timer timer;
    private TimerTask timerTask;

    public Chronometre(long totalSec) {
        this.totalSec = totalSec;
        this.timeLabel = new JLabel(format(totalSec));
    }

    private String format(long value) {
        if (value < 10) {
            return "0" + value;
        }
        return Long.toString(value);
    }

    private String formatTime(long totalSec) {
        long min = TimeUnit.SECONDS.toMinutes(totalSec);
        long sec = totalSec - (min * 60);
        return format(min) + ":" + format(sec);
    }

    public void convertTime() {
        min = TimeUnit.SECONDS.toMinutes(totalSec);
        sec = totalSec - (min * 60);
        timeLabel.setText(format(min) + ":" + format(sec));
        totalSec--;
    }

    public void start() {
        if (timer == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
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
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public long getTotalSec() {
        return totalSec;
    }
}
