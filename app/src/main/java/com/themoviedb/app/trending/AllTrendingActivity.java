package com.themoviedb.app.trending;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.themoviedb.app.DetailMovieActivity;
import com.themoviedb.app.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.json.JSONException;

public class AllTrendingActivity extends AppCompatActivity {

    private RecyclerView rvAllTrending;
    private AllTrendingAdapter allTrendingAdapter;
    private final List<Trending> trendingList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private String timeFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trending);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("timeFrame")) {
            timeFrame = intent.getStringExtra("timeFrame");
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title;
        if (timeFrame != null) {
            if (timeFrame.equals("day")) {
                title = "Trending Today";
            } else if (timeFrame.equals("week")) {
                title = "Trending This Week";
            } else {
                title = "Trending";
            }
        } else {
            title = "Trending";
        }
        
        toolbar.setTitle(title);

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        rvAllTrending = findViewById(R.id.rvAllTrending);

        allTrendingAdapter = new AllTrendingAdapter(this, trendingList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvAllTrending.setLayoutManager(gridLayoutManager);
        rvAllTrending.setAdapter(allTrendingAdapter);

        allTrendingAdapter.setOnItemClickListener(
                new AllTrendingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < trendingList.size()) {
                            Trending trending = trendingList.get(position);

                            Intent intent =
                                    new Intent(AllTrendingActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", trending.getId());
                            intent.putExtra("title", trending.getTitle());
                            intent.putExtra("posterPath", trending.getPosterPath());
                            intent.putExtra("backdropPath", trending.getBackdropPath());
                            intent.putExtra("overview", trending.getOverview());
                            intent.putExtra("releaseDate", trending.getReleaseDate());
                            intent.putExtra("originalLanguage", trending.getOriginalLanguage());
                            intent.putExtra("voteCount", trending.getVoteCount());
                            intent.putExtra("voteAverage", trending.getVoteAverage());
                            intent.putExtra("popularity", trending.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvAllTrending.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (!rvAllTrending.canScrollVertically(1)) {
                            fetchNowPlayingMovies();
                        }
                    }
                });

        fetchNowPlayingMovies();
    }

    private void fetchNowPlayingMovies() {
        if (isLoading) return;

        isLoading = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<List<Trending>> callable =
                new Callable<List<Trending>>() {
                    @Override
                    public List<Trending> call() throws IOException, JSONException {
                        TrendingApi api = new TrendingApi();
                        return api.getTrendingMovies(timeFrame, currentPage);
                    }
                };

        Future<List<Trending>> future = executor.submit(callable);

        try {
            List<Trending> newItems = future.get();

            if (newItems.isEmpty()) {

                isLoading = false;
                return;
            }

            trendingList.addAll(newItems);

            runOnUiThread(
                    () -> {
                        allTrendingAdapter.notifyItemRangeChanged(0, trendingList.size());
                        isLoading = false;
                    });

            if (newItems.size() == 20) {
                currentPage++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
        }
    }
}
