package com.example.android.az.bakingapp;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

/**
 * Created by AZain on 25/12/2018.
 */
@Parcel
class Ingredient {
    @Expose
    public String quantity;
    @Expose
    public String measure;
    @Expose
    public String ingredient;
}
