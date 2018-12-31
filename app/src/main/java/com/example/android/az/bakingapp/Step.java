package com.example.android.az.bakingapp;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

/**
 * Created by AZain on 25/12/2018.
 */
@Parcel
class Step {
    @Expose
    public Integer id;
    @Expose
    public String shortDescription;
    @Expose
    public String description;
    @Expose
    public String videoURL;
    @Expose
    public String thumbnailURL;
}
