package com.example.android.az.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.az.bakingapp.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecipesAdapter mAdapter;
    @BindView(R.id.pb_loading_indicator) ProgressBar pb;
    @BindView(R.id.recipes) RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    @BindBool(R.bool.isTablet) boolean isTablet;
    private int cardsInRow;
    private ArrayList<Recipes> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pb.setVisibility(View.VISIBLE);

        cardsInRow = (isTablet)? 3: 1;

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(cardsInRow, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        iAPI apiService = iAPI.retrofit.create(iAPI.class);
        Call<ArrayList<Recipes>> call = apiService.listRecipes();
        call.enqueue(new Callback<ArrayList<Recipes>>(){
            @Override
            public void onResponse(Call<ArrayList<Recipes>> call, Response<ArrayList<Recipes>> response) {
                pb.setVisibility(View.GONE);
                if (response.code() == 200){
                    list = response.body();
                    mAdapter = new RecipesAdapter(getBaseContext(), response.body());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(onItemClickListener);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipes>> call, Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    RecipesAdapter.OnItemClickListener onItemClickListener = new RecipesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            List<Ingredient> ingredientsWidget = list.get(position).ingredients;
            StringBuilder builder = new StringBuilder();
            for (int i=0; i< ingredientsWidget.size(); i++){
                int serial = i + 1;
                builder.append(serial + "- " + ingredientsWidget.get(i).quantity +" "+ ingredientsWidget.get(i).measure +" of "+ ingredientsWidget.get(i).ingredient + "\n");
            }

            SharedPreferences preferences = getSharedPreferences("Recipe", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ingredientsWidget", builder.toString());
            editor.putString("title", list.get(position).name);
            editor.apply();

//            Intent intentWidget = new Intent(getBaseContext(),RecipesWidget.class);
//            intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), RecipesWidget.class));
//            intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
//            sendBroadcast(intentWidget);
            RecipesWidget myWidget = new RecipesWidget();
            myWidget.onUpdate(getBaseContext(), AppWidgetManager.getInstance(getBaseContext()),ids);

            Intent intent = new Intent(getBaseContext(), RecipeStepDescriptionListActivity.class);
            intent.putExtra("title", list.get(position).name);
            intent.putExtra("ingredients", Parcels.wrap(list.get(position).ingredients));
            intent.putExtra("steps", Parcels.wrap(list.get(position).steps));
            startActivity(intent);
        }
    };
}
