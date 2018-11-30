package com.navatar.data;

import java.util.ArrayList;
import java.util.List;

public class Building {


    private List<Landmark> mLandmarks;

    public List<Landmark> getmLandmarks() {
        return mLandmarks;
    }

    public Building() {
        mLandmarks = new ArrayList<>();
    }
}
