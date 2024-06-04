package com.example.sae202;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Chronometre {
    private long sec, min, totalSec;
    private Label timeLabel;
    private Timer timer;
    private TimerTask timerTask;
    private echiquier echiquier;

    public Chronometre(long totalSec, echiquier echiquier) {
        this.totalSec = totalSec;
        this.echiquier = echiquier;
        this.timeLabel = new Label(formatTime(totalSec));
    }

    private String format(long value) {
        return value < 10 ? "0" + value : Long.toString(value);
    }

    private String formatTime(long totalSec) {
        long min = TimeUnit.SECONDS.toMinutes(totalSec);
        long sec = totalSec - (min * 60);
        return format(min) + ":" + format(sec);
    }

    public void convertTime() {
        min = TimeUnit.SECONDS.toMinutes(totalSec);
        sec = totalSec - (min * 60);
        totalSec--;
        updateLabel();
    }

    private void updateLabel() {
        Platform.runLater(() -> {
            timeLabel.setText(formatTime(totalSec));
            if (totalSec <= 0) {
                stop();
                echiquier.stopGame();
            }
        });
    }

    public void start() {
        if (timer == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (totalSec > 0) {
                        convertTime();
                    } else {
                        stop();
                    }
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

    public Label getTimeLabel() {
        return timeLabel;
    }
}