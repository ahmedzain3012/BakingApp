package com.example.android.az.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.android.az.bakingapp.R;

public class RecipesWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        SharedPreferences preferences = context.getSharedPreferences("Recipe", 0);

        if (preferences.contains("title")) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipes_widget);
            views.setTextViewText(R.id.appwidget_text, preferences.getString("title", null));
            views.setTextViewText(R.id.appwidget_list, preferences.getString("ingredientsWidget", null));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

