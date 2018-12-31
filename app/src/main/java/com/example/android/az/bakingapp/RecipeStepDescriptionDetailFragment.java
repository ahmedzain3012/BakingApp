package com.example.android.az.bakingapp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepDescriptionDetailFragment extends Fragment {
    @BindView(R.id.recipestepdescription_detail) TextView details;
    @BindView(R.id.recipestepdescription_image) ImageView image;
    @BindView(R.id.stepList) LinearLayout stepList;
    @BindView(R.id.video_view) SimpleExoPlayerView playerView;
//    @BindString(R.string.ingredients) String ingredientsText;
    @BindView(R.id.recipestepdescriptionDetails_list) View recyclerView;
    @BindBool(R.bool.isTablet) boolean isTablet;
    private String mItem, description, videoURL, imageURL;
    private List<Ingredient> ingredients;
    private SimpleExoPlayer player;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true, isIngredients = false;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private Activity activity;

    public RecipeStepDescriptionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();

        if (savedInstanceState != null){
            /// done 1: Get the video position from the "playerPosition" if changed orientation or app interrupted and put it in "playbackPosition" variable
            playbackPosition = savedInstanceState.getLong("playerPosition", 0);
        }

        if (getArguments().containsKey("shortDescription")) {
            mItem = getArguments().getString("shortDescription");
            isIngredients = true;
            Log.e("mItem", mItem);

            Toolbar appBarLayout = (Toolbar) activity.findViewById(R.id.toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestepdescription_detail, container, false);
        ButterKnife.bind(this,rootView);

        if (getArguments().containsKey("ingredients")){
            ingredients = Parcels.unwrap(getArguments().getParcelable("ingredients"));
            setupRecyclerView((RecyclerView) recyclerView);
            stepList.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            releasePlayer();
            Log.e("ing size", ingredients.size() +"");
        }else {
            description = getArguments().getString("description");
            videoURL = getArguments().getString("videoURL");
            imageURL = getArguments().getString("imageURL");
            recyclerView.setVisibility(View.GONE);
            if (!videoURL.toString().contains("https")){
                playerView.setVisibility(View.GONE);
                releasePlayer();
            }else {
                if (!isTablet){
                    if (activity.getResources().getConfiguration().orientation == 2){
                        Log.e("orientation", "landscapte");
                        Point size = new Point();
                        activity.getWindowManager().getDefaultDisplay().getSize(size);
                        playerView.setBackgroundColor(Color.parseColor("black"));
                        playerView.getLayoutParams().height = size.y;
                        playerView.getLayoutParams().width = size.x;
                        playerView.layout(0,0,size.x,size.y);  //activity.getWindowManager().LayoutParams lp = new View.MeasureSpec();
                        //playerView.getVideoSurfaceView().setLayoutParams()  .set setMinimumHeight(size.y);
                        stepList.setVisibility(View.GONE);
                        hideSystemUi();
                    }
//            activity.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
//            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //Remove notification bar
                }
                initializePlayer();
            }
            details.setText(description);
            if (imageURL.isEmpty()){
                image.setVisibility(View.GONE);
            }else{
                Picasso.with(activity.getBaseContext()).load(imageURL).into(image);
            }
        }
        return rootView;
    }

    private void initializePlayer() {
        if (player  == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), new DefaultTrackSelector(videoTrackSelectionFactory), new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            /// done 2: "initializePlayer()" could be called from OnCreateView, OnRestart, and OnResume, seekTo "position" reads from the Global var "playbackPosition" if changed by "Todo 1" or will be "0"
            Log.e("playbackPosition", playbackPosition +"");
            player.seekTo(currentWindow, playbackPosition);
        }
        MediaSource mediaSource = buildMediaSource(Uri.parse(videoURL));
        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (!isIngredients){
                initializePlayer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            if (!isIngredients){
                initializePlayer();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            /// done 3: OnPause or OnStop (change orientation or went to background) "playbackPosition" variable value will be update with the current video position
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER);
        return new ExtractorMediaSource(uri, dataSourceFactory, new DefaultExtractorsFactory(), null, null);
    }

    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void showSystemUI() {
        playerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ingredients));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Ingredient> mValues;

        public SimpleItemRecyclerViewAdapter(List<Ingredient> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipestepdescription_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).quantity +" "+ mValues.get(position).measure +" of "+ mValues.get(position).ingredient);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public Ingredient mItem;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /// done 4: On app change life cycle status change, "onSaveInstanceState" will be update from Todo 3 and then update Todo 1 in restart
        outState.putLong("playerPosition", playbackPosition);
        super.onSaveInstanceState(outState);
    }
}
