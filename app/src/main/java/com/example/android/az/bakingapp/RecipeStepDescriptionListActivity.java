package com.example.android.az.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.android.az.bakingapp.R;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepDescriptionListActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recipestepdescription_list) View recyclerView;
    @BindString(R.string.ingredients) String ingredientsText;
    private boolean mTwoPane;
    private Intent intent;
    private String title;
    private List<Step> stepsList;
    private List<Ingredient> ingredientsList;
    private int stepsListSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestepdescription_list);
        ButterKnife.bind(this);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        intent = getIntent();
        if (intent.getExtras() != null) {
            title = intent.getStringExtra("title");
            stepsList = Parcels.unwrap(intent.getParcelableExtra("steps"));
            ingredientsList = Parcels.unwrap(intent.getParcelableExtra("ingredients"));
            toolbar.setTitle(title);

            stepsListSize = (savedInstanceState != null)? savedInstanceState.getInt("stepsListSize") :stepsList.size();

            if (stepsList.size() ==  stepsListSize){
                Step s = new Step();
                s.shortDescription = ingredientsText;
                s.id = -1;
                stepsList.add(0,s);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.recipestepdescription_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("stepsListSize", stepsListSize);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //navigateUpFromSameTask(this);
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(stepsList));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Step> mValues;

        public SimpleItemRecyclerViewAdapter(List<Step> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipestepdescription_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).shortDescription);
            if (holder.mItem.shortDescription == ingredientsText){
                holder.mContentView.setTextColor(Color.argb(255,0,0,255));
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString("shortDescription", holder.mItem.shortDescription);
                        arguments.putInt("id", holder.mItem.id);
                        if (holder.mItem.shortDescription == ingredientsText){
                            Log.e("tablet","true");
                            toolbar.setTitle(ingredientsText);
                            arguments.putParcelable("ingredients", intent.getParcelableExtra("ingredients"));
                        }else {
                            arguments.putString("description", holder.mItem.description);
                            arguments.putString("videoURL", holder.mItem.videoURL);
                            arguments.putString("imageURL", holder.mItem.thumbnailURL);
                        }
                        RecipeStepDescriptionDetailFragment fragment = new RecipeStepDescriptionDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction().replace(R.id.recipestepdescription_detail_container, fragment).commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeStepDescriptionDetailActivity.class);
                        intent.putExtra("shortDescription", holder.mItem.shortDescription);
                        if (holder.mItem.shortDescription == ingredientsText) {
                            Log.e("tablet","false");
                            intent.putExtra("ingredients", Parcels.wrap(ingredientsList));
                        }else {
                            intent.putExtra("description", holder.mItem.description);
                            intent.putExtra("videoURL", holder.mItem.videoURL);
                            intent.putExtra("imageURL", holder.mItem.thumbnailURL);
                        }
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public Step mItem;
            @BindView(R.id.content) TextView mContentView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                ButterKnife.bind(this, view);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
