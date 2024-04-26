package com.themoviedb.app.toprated;

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

public class AllTopRatedActivity extends AppCompatActivity {

    private RecyclerView rvAllTopRated;
    private TopRatedAdapter topRatedAdapter;
    private final List<TopRated> topRatedList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_top_rated);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Top Rated");

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        rvAllTopRated = findViewById(R.id.rvAllTopRated);

        topRatedAdapter = new TopRatedAdapter(this, topRatedList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvAllTopRated.setLayoutManager(gridLayoutManager);
        rvAllTopRated.setAdapter(topRatedAdapter);

        topRatedAdapter.setOnItemClickListener(
                new TopRatedAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < topRatedList.size()) {
                            TopRated topRated = topRatedList.get(position);

                            Intent intent =
                                    new Intent(AllTopRatedActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", topRated.getId());
                            intent.putExtra("title", topRated.getTitle());
                            intent.putExtra("posterPath", topRated.getPosterPath());
                            intent.putExtra("backdropPath", topRated.getBackdropPath());
                            intent.putExtra("overview", topRated.getOverview());
                            intent.putExtra("releaseDate", topRated.getReleaseDate());
                            intent.putExtra("originalLanguage", topRated.getOriginalLanguage());
                            intent.putExtra("voteCount", topRated.getVoteCount());
                            intent.putExtra("voteAverage", topRated.getVoteAverage());
                            intent.putExtra("popularity", topRated.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvAllTopRated.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (!rvAllTopRated.canScrollVertically(1)) {
                            fetchTopRatedMovies();
                        }
                    }
                });

        fetchTopRatedMovies();
    }

    private void fetchTopRatedMovies() {
        if (isLoading) return;

        isLoading = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<List<TopRated>> callable =
                new Callable<List<TopRated>>() {
                    @Override
                    public List<TopRated> call() throws IOException, JSONException {
                        TopRatedApi api = new TopRatedApi();
                        return api.getTopRatedMovies(currentPage);
                    }
                };

        Future<List<TopRated>> future = executor.submit(callable);

        try {
            List<TopRated> newItems = future.get();

            if (newItems.isEmpty()) {

                isLoading = false;
                return;
            }

            topRatedList.addAll(newItems);

            runOnUiThread(
                    () -> {
                        topRatedAdapter.notifyItemRangeChanged(0, topRatedList.size());
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
