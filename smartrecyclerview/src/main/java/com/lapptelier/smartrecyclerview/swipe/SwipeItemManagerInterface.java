package com.lapptelier.smartrecyclerview.swipe;

import com.lapptelier.smartrecyclerview.swipe.SwipeLayout;
import com.lapptelier.smartrecyclerview.swipe.SwipeMode;

import java.util.List;

public interface SwipeItemManagerInterface {

    void openItem(int position);

    void closeItem(int position);

    void closeAllExcept(SwipeLayout layout);
    
    void closeAllItems();

    List<Integer> getOpenItems();

    List<SwipeLayout> getOpenLayouts();

    void removeShownLayouts(SwipeLayout layout);

    boolean isOpen(int position);

    SwipeMode getMode();

    void setMode(SwipeMode mode);
}
