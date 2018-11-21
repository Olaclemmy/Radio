package org.oucho.radio2.utils;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import org.oucho.radio2.RadioApplication;
import org.oucho.radio2.radio.RadioService;
import org.oucho.radio2.radio.RadioKeys;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class VolumeTimer implements RadioKeys {

    private CountDownTimer volumeTimer;

    private final Context mContext = RadioApplication.getInstance();

    public void setVolume(float volume) {

        if (State.isPlaying() || State.isPaused()) {
            Intent niveau = new Intent(mContext, RadioService.class);
            niveau.putExtra("voldown", volume);
            mContext.startService(niveau);
        }
    }

    public void volumeDown(final ScheduledFuture task, final int delay) {

        final short minutes = (short) ( ( (delay / 1000) % 3600) / 60);

        // delay is greater or less than 10mn
        final boolean tempsMinuterie = minutes > 10;

            int cycle;

            if (tempsMinuterie) {
                cycle = 60000;
            } else {
                cycle = 1000;
            }


        volumeTimer = new CountDownTimer(delay, cycle) {
                @Override
                public void onTick(long mseconds) {

                    // for long timer > 10mn
                    long minutesTimer = ((task.getDelay(TimeUnit.MILLISECONDS) / 1000) % 3600) / 60 ;

                    // for short timer < 10mn
                    long secondesTimer = task.getDelay(TimeUnit.MILLISECONDS) / 1000;

                    if (tempsMinuterie) {

                        if (minutesTimer < 1) {
                            setVolume(0.1f);
                        } else if (minutesTimer < 2) {
                            setVolume(0.2f);
                        } else if (minutesTimer < 3) {
                            setVolume(0.3f);
                        } else if (minutesTimer < 4) {
                            setVolume(0.4f);
                        } else if (minutesTimer < 5) {
                            setVolume(0.5f);
                        } else if (minutesTimer < 6) {
                            setVolume(0.6f);
                        } else if (minutesTimer < 7) {
                            setVolume(0.7f);
                        } else if (minutesTimer < 8) {
                            setVolume(0.8f);
                        } else if (minutesTimer < 9) {
                            setVolume(0.9f);
                        } else if (minutesTimer < 10) {
                            setVolume(1.0f);
                        }

                    } else {

                        if (secondesTimer < 6) {
                            setVolume(0.1f);
                        } else if (secondesTimer < 12) {
                            setVolume(0.2f);
                        } else if (secondesTimer < 18) {
                            setVolume(0.3f);
                        } else if (secondesTimer < 24) {
                            setVolume(0.4f);
                        } else if (secondesTimer < 30) {
                            setVolume(0.5f);
                        } else if (secondesTimer < 36) {
                            setVolume(0.6f);
                        } else if (secondesTimer < 42) {
                            setVolume(0.7f);
                        } else if (secondesTimer < 48) {
                            setVolume(0.8f);
                        } else if (secondesTimer < 54) {
                            setVolume(0.9f);
                        } else if (secondesTimer < 60) {
                            setVolume(1.0f);
                        }
                    }
                }

                @Override
                public void onFinish() {

                    Intent intent = new Intent();
                    intent.setAction(INTENT_STATE);
                    intent.putExtra(ACTION_QUIT, true);
                    mContext.sendBroadcast(intent);
                }
            }.start();

    }

    public CountDownTimer getVolumeTimer() {
        return volumeTimer;
    }
}
