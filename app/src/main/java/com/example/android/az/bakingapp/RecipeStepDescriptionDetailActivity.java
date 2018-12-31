package com.example.android.az.bakingapp;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.android.az.bakingapp.R;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepDescriptionDetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindBool(R.bool.isTablet) boolean isTablet;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestepdescription_detail);
        ButterKnife.bind(this);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            toolbar.setTitle(getIntent().getStringExtra("shortDescription"));
            Bundle arguments = new Bundle();
            arguments.putString("shortDescription", getIntent().getStringExtra("shortDescription"));
            arguments.putInt("id", getIntent().getIntExtra("id", -1));
            if (getIntent().hasExtra("ingredients")) {
                List<Ingredient> in = Parcels.unwrap(getIntent().getParcelableExtra("ingredients"));
                Log.e("ing size", in.size() +"");
                arguments.putParcelable("ingredients", getIntent().getParcelableExtra("ingredients"));
            }else {
                arguments.putString("description", getIntent().getStringExtra("description"));
                arguments.putString("videoURL", getIntent().getStringExtra("videoURL"));
                arguments.putString("imageURL", getIntent().getStringExtra("imageURL"));
                if (!isTablet) {
                    if (this.getResources().getConfiguration().orientation == 2) {
                        Log.e("orientation", "landscapte");
                        appBar.setVisibility(View.GONE);
                    }
                }
            }
            RecipeStepDescriptionDetailFragment fragment = new RecipeStepDescriptionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.recipestepdescription_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().hasExtra("videoURL")){
            if (!isTablet) {
                if (this.getResources().getConfiguration().orientation == 2) {
                    appBar.setVisibility(View.GONE);
                }
            }
        }
    }
}
