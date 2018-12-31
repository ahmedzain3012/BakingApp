package com.example.android.az.bakingapp;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by AZain on 25/12/2018.
 */

public class Recipes {
    @Expose
    public Integer id;
    @Expose
    public String name;
    @Expose
    public List<Ingredient> ingredients = null;
    @Expose
    public List<Step> steps = null;
    @Expose
    public Integer servings;
    @Expose
    public String image;
}
