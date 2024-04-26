package com.themoviedb.app.nowplaying;

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

public class AllNowPlayingActivity extends AppCompatActivity {

    private RecyclerView rvAllNowPlaying;
    private NowPlayingAdapter nowPlayingAdapter;
    private final List<NowPlaying> nowPlayingList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_now_playing);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Now Playing");

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        rvAllNowPlaying = findViewById(R.id.rvAllNowPlaying);

        nowPlayingAdapter = new NowPlayingAdapter(this, nowPlayingList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvAllNowPlaying.setLayoutManager(gridLayoutManager);
        rvAllNowPlaying.setAdapter(nowPlayingAdapter);

        nowPlayingAdapter.setOnItemClickListener(
                new NowPlayingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < nowPlayingList.size()) {
                            NowPlaying nowPlaying = nowPlayingList.get(position);

                            Intent intent =
                                    new Intent(
                                            AllNowPlayingActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", nowPlaying.getId());
                            intent.putExtra("title", nowPlaying.getTitle());
                            intent.putExtra("posterPath", nowPlaying.getPosterPath());
                            intent.putExtra("backdropPath", nowPlaying.getBackdropPath());
                            intent.putExtra("overview", nowPlaying.getOverview());
                            intent.putExtra("releaseDate", nowPlaying.getReleaseDate());
                            intent.putExtra("originalLanguage", nowPlaying.getOriginalLanguage());
                            intent.putExtra("voteCount", nowPlaying.getVoteCount());
                            intent.putExtra("voteAverage", nowPlaying.getVoteAverage());
                            intent.putExtra("popularity", nowPlaying.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvAllNowPlaying.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (!rvAllNowPlaying.canScrollVertically(1)) {
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
        Callable<List<NowPlaying>> callable =
                new Callable<List<NowPlaying>>() {
                    @Override
                    public List<NowPlaying> call() throws IOException, JSONException {
                        NowPlayingApi api = new NowPlayingApi();
                        return api.getNowPlayingMovies(currentPage);
                    }
                };

        Future<List<NowPlaying>> future = executor.submit(callable);

        try {
            List<NowPlaying> newItems = future.get();

            if (newItems.isEmpty()) {

                isLoading = false;
                return;
            }

            nowPlayingList.addAll(newItems);

            runOnUiThread(
                    () -> {
                        nowPlayingAdapter.notifyItemRangeChanged(0, nowPlayingList.size());
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
