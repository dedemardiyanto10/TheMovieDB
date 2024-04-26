package com.themoviedb.app.upcoming;

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

public class AllUpComingActivity extends AppCompatActivity {

    private RecyclerView rvAllUpComing;
    private UpComingAdapter upComingAdapter;
    private final List<UpComing> upComingList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_up_coming);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Up Coming");

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        rvAllUpComing = findViewById(R.id.rvAllUpComing);

        upComingAdapter = new UpComingAdapter(this, upComingList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvAllUpComing.setLayoutManager(gridLayoutManager);
        rvAllUpComing.setAdapter(upComingAdapter);

        upComingAdapter.setOnItemClickListener(
                new UpComingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < upComingList.size()) {
                            UpComing upComing = upComingList.get(position);

                            Intent intent =
                                    new Intent(AllUpComingActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", upComing.getId());
                            intent.putExtra("title", upComing.getTitle());
                            intent.putExtra("posterPath", upComing.getPosterPath());
                            intent.putExtra("backdropPath", upComing.getBackdropPath());
                            intent.putExtra("overview", upComing.getOverview());
                            intent.putExtra("releaseDate", upComing.getReleaseDate());
                            intent.putExtra("originalLanguage", upComing.getOriginalLanguage());
                            intent.putExtra("voteCount", upComing.getVoteCount());
                            intent.putExtra("voteAverage", upComing.getVoteAverage());
                            intent.putExtra("popularity", upComing.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvAllUpComing.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (!rvAllUpComing.canScrollVertically(1)) {
                            fetchUpComingMovies();
                        }
                    }
                });

        fetchUpComingMovies();
    }

    private void fetchUpComingMovies() {
        if (isLoading) return;

        isLoading = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<List<UpComing>> callable =
                new Callable<List<UpComing>>() {
                    @Override
                    public List<UpComing> call() throws IOException, JSONException {
                        UpComingApi api = new UpComingApi();
                        return api.getUpComingMovies(currentPage);
                    }
                };

        Future<List<UpComing>> future = executor.submit(callable);

        try {
            List<UpComing> newItems = future.get();

            if (newItems.isEmpty()) {

                isLoading = false;
                return;
            }

            upComingList.addAll(newItems);

            runOnUiThread(
                    () -> {
                        upComingAdapter.notifyItemRangeChanged(0, upComingList.size());
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
