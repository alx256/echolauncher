package com.example.echolauncher;

import android.util.Log;
import android.widget.ScrollView;

public class SnapListener {
    private Runnable runnable;
    public Thread thread;
    private ScrollView masterView;
    private int lastY;

    public SnapListener(ScrollView scrollView, MainActivity activity) {
        masterView = scrollView;
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("", "Doob");

                                if ((masterView.getScrollY() - lastY) == 0) {
                                    // Stopped scrolling on y axis
                                    //masterView.scrollTo(0, 0);
                                    Log.i("", "Stopped scrolling!");
                                } else {
                                    // Is scrolling
                                    lastY = masterView.getScrollY();
                                    masterView.postDelayed(runnable, 100);
                                    Log.i("", "Is scrolling!");
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {}
            }
        };
    }

    public void ScrollTo(int section) {
        masterView.scrollTo(0, masterView.getBottom());
        //masterView.scrollTo(masterView.getScrollX(), (masterView.getHeight() / 3) * section);
    }
}
