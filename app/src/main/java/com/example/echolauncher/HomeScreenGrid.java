package com.example.echolauncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HomeScreenGrid is used to hold a
 * Flexbox layout. This holds apps and
 * widgets in a layout where widgets do
 * not adjust the column size despite being
 * longer
 * */

public class HomeScreenGrid {
    // Contains the Instruction and the item which
    // is having the instruction carried out on
    // it
    public class InstructionCollection {
        public InstructionCollection(HomeScreenGridAdapter.Instruction instruction, Item item) {
            INSTRUCTION = instruction;
            ITEM = item;
        }

        public HomeScreenGridAdapter.Instruction getInstruction() {
            return INSTRUCTION;
        }

        public Item getItem() {
            return ITEM;
        }

        private final HomeScreenGridAdapter.Instruction INSTRUCTION;
        private final Item ITEM;
    }

    public HomeScreenGrid(int pageNumber) {
        this.pageNumber = pageNumber;

        items = new ArrayList<>();
        homeScreenInstructions = new HashMap<>();
    }

    public void updateGrid(int position, HomeScreenGridAdapter.Instruction instruction, Item item) {
        // If item is null then something
        // has gone wrong. Make sure that
        // this is not the case
        assert item != null;

        // Create homeScreenInstructions if it does not
        // already exist
        if (homeScreenInstructions.get(position) == null)
            homeScreenInstructions.put(position, new ArrayList<>());

        // Add the new instructions
        homeScreenInstructions.get(position).add(new InstructionCollection(instruction, item));
        // Update the necessary item
        Pages.getGridAdapter().notifyItemChanged(position);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void clearItems() {
        items.clear();
    }

    public Map<Integer, List<InstructionCollection>> getHomeScreenInstructions() {
        return homeScreenInstructions;
    }

    public List<Item> getItems() {
        return items;
    }

    private Map<Integer, List<InstructionCollection>> homeScreenInstructions;
    private List<Item> items;
    private int pageNumber;
}
