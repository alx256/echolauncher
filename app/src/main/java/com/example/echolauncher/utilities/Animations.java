package com.example.echolauncher.utilities;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.example.echolauncher.R;

import java.util.HashMap;
import java.util.Map;

public class Animations {
    public enum AnimationName {
        SHRINK_ANIMATION,
        EXPAND_ANIMATION
    }

    private static class AnimationDetails {
        private Animation animation;
        private int color;
        private float to, stop;
    }

    public static void perform(AnimationName name, View view) {
        AnimationDetails details = get(name, view);
        assert details != null;

        bindViewToAnimation(details, view);
        view.clearAnimation();
        view.startAnimation(details.animation);
        view.setBackgroundColor(details.color);
    }

    public static float getShrinkScale() {
        return SHRINK_SCALE;
    }

    private static AnimationDetails get(AnimationName name, View view) {
        if (animations == null)
            animations = new HashMap<>();

        if (animations.containsKey(name))
            return animations.get(name);

        switch (name) {
            case SHRINK_ANIMATION:
                return constructHomeScreenGridAnimation(name,
                        SHRINK_SCALE, SHRINK_SCALE,
                        view.getContext().getResources().getColor(R.color.light_gray_background),
                        view);
            case EXPAND_ANIMATION:
                return constructHomeScreenGridAnimation(name,
                        2.0f - SHRINK_SCALE, 1.0f, Color.TRANSPARENT, view);
        }

        return null;
    }

    private static AnimationDetails constructHomeScreenGridAnimation(AnimationName name,
                                                                     float to, float stop,
                                                                     int color, View view) {
        AnimationDetails details = new AnimationDetails();
        details.color = color;
        details.animation = new ScaleAnimation(1.0f, to, 1.0f, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        details.animation.setDuration(ANIMATION_DURATION);

        // Store extra data so it can be used later
        // if necessary
        details.to = to;
        details.stop = stop;

        animations.put(name, details);
        return details;
    }

    private static void bindViewToAnimation(AnimationDetails details, View view) {
        details.animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (view.getScaleX() != details.stop &&
                        view.getScaleY() != details.stop)
                    animation.cancel();
                view.setScaleX(details.stop);
                view.setScaleY(details.stop);
            }

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    private static Map<AnimationName, AnimationDetails> animations;
    private static final float SHRINK_SCALE = 0.8f;
    private static final long ANIMATION_DURATION = 212;
}
