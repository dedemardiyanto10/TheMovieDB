package com.themoviedb.app.popular;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class AllPopularActivity extends AppCompatActivity {

    private RecyclerView rvAllPopular;
    private PopularAdapter popularAdapter;
    private final List<Popular> popularList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_popular);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Popular");

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        rvAllPopular = findViewById(R.id.rvAllPopular);

        popularAdapter = new PopularAdapter(this, popularList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvAllPopular.setLayoutManager(gridLayoutManager);
        rvAllPopular.setAdapter(popularAdapter);

        popularAdapter.setOnItemClickListener(
                new PopularAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < popularList.size()) {
                            Popular popular = popularList.get(position);

                            Intent intent =
                                    new Intent(AllPopularActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", popular.getId());
                            intent.putExtra("title", popular.getTitle());
                            intent.putExtra("posterPath", popular.getPosterPath());
                            intent.putExtra("backdropPath", popular.getBackdropPath());
                            intent.putExtra("overview", popular.getOverview());
                            intent.putExtra("releaseDate", popular.getReleaseDate());
                            intent.putExtra("originalLanguage", popular.getOriginalLanguage());
                            intent.putExtra("voteCount", popular.getVoteCount());
                            intent.putExtra("voteAverage", popular.getVoteAverage());
                            intent.putExtra("popularity", popular.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvAllPopular.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (!rvAllPopular.canScrollVertically(1)) {
                            fetchPopularMovies();
                        }
                    }
                });

        fetchPopularMovies();
    }

    private void fetchPopularMovies() {
        if (isLoading) return;

        isLoading = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<List<Popular>> callable =
                new Callable<List<Popular>>() {
                    @Override
                    public List<Popular> call() throws IOException, JSONException {
                        PopularApi api = new PopularApi();
                        return api.getPopularMovies(currentPage);
                    }
                };

        Future<List<Popular>> future = executor.submit(callable);

        try {
            List<Popular> newItems = future.get();

            if (newItems.isEmpty()) {

                isLoading = false;
                return;
            }

            popularList.addAll(newItems);

            runOnUiThread(
                    () -> {
                        popularAdapter.notifyItemRangeChanged(0, popularList.size());
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
