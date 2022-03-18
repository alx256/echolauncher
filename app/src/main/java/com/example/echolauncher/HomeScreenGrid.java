package com.example.echolauncher;

import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeScreenGrid extends Fragment {
    public static class InstructionCollection {
        public InstructionCollection(HomeScreenGridAdapter.Instruction instruction, PinItem item) {
            INSTRUCTION = instruction;
            ITEM = item;
        }

        public HomeScreenGridAdapter.Instruction getInstruction() {
            return INSTRUCTION;
        }

        public PinItem getItem() {
            return ITEM;
        }

        private final HomeScreenGridAdapter.Instruction INSTRUCTION;
        private final PinItem ITEM;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen_grid, container, false);

        recyclerView = view.findViewById(R.id.homeScreenGrid);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setAlignItems(AlignItems.STRETCH);
        manager.setJustifyContent(JustifyContent.SPACE_EVENLY);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(manager);

        HomeScreenGridAdapter adapter = new HomeScreenGridAdapter(getContext());
        recyclerView.setAdapter(adapter);

        ImageView delete = view.findViewById(R.id.cross);
        delete.setY(-Globals.metrics.heightPixels + delete.getLayoutParams().height);
        delete.setVisibility(View.INVISIBLE);

        final long ANIMATION_DURATION = 212;

        shrinkAnimation = new ScaleAnimation(1.0f, SHRINK_SCALE,
                1.0f, SHRINK_SCALE,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrinkAnimation.setDuration(ANIMATION_DURATION);
        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (recyclerView.getScaleX() != SHRINK_SCALE &&
                    recyclerView.getScaleY() != SHRINK_SCALE)
                    animation.cancel();
                recyclerView.setScaleX(SHRINK_SCALE);
                recyclerView.setScaleY(SHRINK_SCALE);
            }

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        expandAnimation = new ScaleAnimation(1.0f, 1.0f + (1.0f - SHRINK_SCALE),
                1.0f, 1.0f + (1.0f - SHRINK_SCALE),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        expandAnimation.setDuration(ANIMATION_DURATION);
        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (recyclerView.getScaleX() != 1.0f &&
                    recyclerView.getScaleY() != 1.0f)
                    animation.cancel();

                recyclerView.setScaleX(1.0f);
                recyclerView.setScaleY(1.0f);
            }

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        recyclerView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                delete.setVisibility(View.VISIBLE);
                shrink(view);

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    delete.setVisibility(View.INVISIBLE);
                    expand(view);
                }

                return true;
            }
        });

        storage = new Storage(getContext(), "identifier,position,screen,segment", "echolauncher_homescreen");

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
                HomeScreenGridAdapter adapter = (HomeScreenGridAdapter) recyclerView.getAdapter();
                adapter.updateTotal(holder.itemView.getMeasuredHeight());
                adapter.notifyItemChanged(0);

                try {
                    List<Storage.Line> contents = storage.readItems();
                    for (Storage.Line line : contents) {
                        HomeScreenGrid.updateGrid(line.getInt("position"),
                                HomeScreenGridAdapter.Instruction.ADD,
                                Search.get(line.getString("identifier")));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ViewTreeObserver temp = view.getViewTreeObserver();
                temp.removeOnGlobalLayoutListener(this);
            }
        });

        return view;
    }

    static public void updateGrid(int position, HomeScreenGridAdapter.Instruction instruction,
                                  PinItem item) {
        if (homeScreenInstructions.get(position) == null)
            homeScreenInstructions.put(position, new ArrayList<>());
        homeScreenInstructions.get(position).add(new InstructionCollection(instruction, item));
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    private void shrink(View view) {
        if (isMultiplePageMode)
            return;

        view.clearAnimation();
        view.startAnimation(shrinkAnimation);
        view.setBackgroundColor(0x33FFFFFF);
        isMultiplePageMode = true;
    }

    private void expand(View view) {
        if (!isMultiplePageMode)
            return;

        view.clearAnimation();
        view.startAnimation(expandAnimation);
        view.setBackgroundColor(Color.TRANSPARENT);
        isMultiplePageMode = false;
    }

    public final static float SHRINK_SCALE = 0.8f;
    public static Map<Integer, List<InstructionCollection>> homeScreenInstructions;

    private View view;
    private Animation shrinkAnimation, expandAnimation;
    private static Storage storage;
    private static boolean isMultiplePageMode;
    private static RecyclerView recyclerView;
}
