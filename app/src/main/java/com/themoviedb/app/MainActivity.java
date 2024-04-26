package com.themoviedb.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.themoviedb.app.nowplaying.AllNowPlayingActivity;
import com.themoviedb.app.nowplaying.NowPlaying;
import com.themoviedb.app.nowplaying.NowPlayingAdapter;
import com.themoviedb.app.nowplaying.NowPlayingApi;
import com.themoviedb.app.popular.AllPopularActivity;
import com.themoviedb.app.popular.Popular;
import com.themoviedb.app.popular.PopularAdapter;
import com.themoviedb.app.popular.PopularApi;
import com.themoviedb.app.toprated.AllTopRatedActivity;
import com.themoviedb.app.toprated.TopRated;
import com.themoviedb.app.toprated.TopRatedAdapter;
import com.themoviedb.app.toprated.TopRatedApi;
import com.themoviedb.app.trending.AllTrendingActivity;
import com.themoviedb.app.trending.Trending;
import com.themoviedb.app.trending.TrendingAdapter;
import com.themoviedb.app.trending.TrendingApi;
import com.themoviedb.app.upcoming.AllUpComingActivity;
import com.themoviedb.app.upcoming.UpComing;
import com.themoviedb.app.upcoming.UpComingAdapter;
import com.themoviedb.app.upcoming.UpComingApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTrending;
    private TrendingAdapter trendingAdapter;
    private final List<Trending> trendingList = new ArrayList<>();
    private MaterialButtonToggleGroup tbTrending;

    private RecyclerView rvNowPlaying;
    private NowPlayingAdapter nowPlayingAdapter;
    private final List<NowPlaying> nowPlayingList = new ArrayList<>();

    private RecyclerView rvPopular;
    private PopularAdapter popularAdapter;
    private final List<Popular> popularList = new ArrayList<>();

    private RecyclerView rvTopRated;
    private TopRatedAdapter topRatedAdapter;
    private final List<TopRated> topRatedList = new ArrayList<>();

    private RecyclerView rvUpComing;
    private UpComingAdapter upComingAdapter;
    private final List<UpComing> upComingList = new ArrayList<>();

    private ExecutorService executor;
    private ShimmerFrameLayout shimmerTrending;
    private ShimmerFrameLayout shimmerNowPlaying;
    private ShimmerFrameLayout shimmerPopular;
    private ShimmerFrameLayout shimmerTopRated;
    private ShimmerFrameLayout shimmerUpComing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executor = Executors.newFixedThreadPool(4);

        shimmerTrending = findViewById(R.id.shimmerTrending);
        shimmerNowPlaying = findViewById(R.id.shimmerNowPlaying);
        shimmerPopular = findViewById(R.id.shimmerPopular);
        shimmerTopRated = findViewById(R.id.shimmerTopRated);
        shimmerUpComing = findViewById(R.id.shimmerUpComing);

        startShimmer(shimmerTrending);
        startShimmer(shimmerNowPlaying);
        startShimmer(shimmerPopular);
        startShimmer(shimmerTopRated);
        startShimmer(shimmerUpComing);

        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                });

        rvTrending = findViewById(R.id.rvTrending);
        trendingAdapter = new TrendingAdapter(this, trendingList);
        rvTrending.setHasFixedSize(true);
        rvTrending.setAdapter(trendingAdapter);

        //   fetchTrendingMovies("day");

        Button btnTrending = findViewById(R.id.btnTrending);

        btnTrending.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, AllTrendingActivity.class);

                    int checkedButtonId = tbTrending.getCheckedButtonId();

                    if (checkedButtonId == R.id.btnToday) {
                        intent.putExtra("timeFrame", "day");
                    } else if (checkedButtonId == R.id.btnWeek) {
                        intent.putExtra("timeFrame", "week");
                    }

                    startActivity(intent);
                });

        tbTrending = findViewById(R.id.tbTrending);

        tbTrending.addOnButtonCheckedListener(
                (group, checkedId, isChecked) -> {
                    if (isChecked) {
                        if (checkedId == R.id.btnToday) {
                            fetchTrendingMovies("day");
                        } else if (checkedId == R.id.btnWeek) {
                            fetchTrendingMovies("week");
                        }
                    }
                });

        trendingAdapter.setOnItemClickListener(
                position -> {
                    if (position >= 0 && position < trendingList.size()) {
                        Trending trending = trendingList.get(position);

                        Intent intent =
                                new Intent(MainActivity.this, DetailMovieActivity.class);
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
                });

        rvNowPlaying = findViewById(R.id.rvNowPlaying);
        nowPlayingAdapter = new NowPlayingAdapter(this, nowPlayingList);
        LinearLayoutManager nowPlayingLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvNowPlaying.setLayoutManager(nowPlayingLayoutManager);
        rvNowPlaying.setAdapter(nowPlayingAdapter);

        //  fetchNowPlayingMovies();

        Button btnNowPlaying = findViewById(R.id.btnNowPlaying);

        btnNowPlaying.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, AllNowPlayingActivity.class);
                    startActivity(intent);
                });

        nowPlayingAdapter.setOnItemClickListener(
                position -> {
                    if (position >= 0 && position < nowPlayingList.size()) {
                        NowPlaying nowPlaying = nowPlayingList.get(position);

                        Intent intent =
                                new Intent(MainActivity.this, DetailMovieActivity.class);
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
                });

        rvPopular = findViewById(R.id.rvPopular);
        popularAdapter = new PopularAdapter(this, popularList);
        LinearLayoutManager popularLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPopular.setLayoutManager(popularLayoutManager);
        rvPopular.setAdapter(popularAdapter);

        //  fetchPoulerMovies();

        Button btnPopular = findViewById(R.id.btnPopular);

        btnPopular.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, AllPopularActivity.class);
                    startActivity(intent);
                });

        popularAdapter.setOnItemClickListener(
                position -> {
                    if (position >= 0 && position < popularList.size()) {
                        Popular popular = popularList.get(position);

                        Intent intent =
                                new Intent(MainActivity.this, DetailMovieActivity.class);
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
                });

        rvTopRated = findViewById(R.id.rvTopRated);
        topRatedAdapter = new TopRatedAdapter(this, topRatedList);
        LinearLayoutManager topRatedLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTopRated.setLayoutManager(topRatedLayoutManager);
        rvTopRated.setAdapter(topRatedAdapter);

        //   fetchTopRatedMovies();

        Button btnTopRated = findViewById(R.id.btnTopRated);

        btnTopRated.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, AllTopRatedActivity.class);
                    startActivity(intent);
                });

        topRatedAdapter.setOnItemClickListener(
                position -> {
                    if (position >= 0 && position < topRatedList.size()) {
                        TopRated topRated = topRatedList.get(position);

                        Intent intent =
                                new Intent(MainActivity.this, DetailMovieActivity.class);
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
                });

        rvUpComing = findViewById(R.id.rvUpComing);
        upComingAdapter = new UpComingAdapter(this, upComingList);
        LinearLayoutManager upComingLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvUpComing.setLayoutManager(upComingLayoutManager);
        rvUpComing.setAdapter(upComingAdapter);

        //   fetchUpComingMovies();

        Button btnUpComing = findViewById(R.id.btnUpComing);

        btnUpComing.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, AllUpComingActivity.class);
                    startActivity(intent);
                });

        upComingAdapter.setOnItemClickListener(
                position -> {
                    if (position >= 0 && position < upComingList.size()) {
                        UpComing upComing = upComingList.get(position);

                        Intent intent =
                                new Intent(MainActivity.this, DetailMovieActivity.class);
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
                });

        registerReceiver(
                new NetworkChangeReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void checkAndFetchData() {
        if (isConnected()) {
            fetchInitialData();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void fetchInitialData() {
        fetchTrendingMovies("day");
        fetchNowPlayingMovies();
        fetchPopularMovies();
        fetchTopRatedMovies();
        fetchUpComingMovies();
    }

    private void fetchTrendingMovies(String timeFrame) {
        Callable<List<Trending>> callable =
                () -> {
                    TrendingApi api = new TrendingApi();
                    return api.getTrendingMovies(timeFrame, 1);
                };
        fetchData(callable, trendingList, trendingAdapter, shimmerTrending, rvTrending);
    }

    private void fetchNowPlayingMovies() {
        Callable<List<NowPlaying>> callable =
                () -> {
                    NowPlayingApi api = new NowPlayingApi();
                    return api.getNowPlayingMovies(1);
                };
        fetchData(callable, nowPlayingList, nowPlayingAdapter, shimmerNowPlaying, rvNowPlaying);
    }

    private void fetchPopularMovies() {
        Callable<List<Popular>> callable =
                () -> {
                    PopularApi api = new PopularApi();
                    return api.getPopularMovies(1);
                };
        fetchData(callable, popularList, popularAdapter, shimmerPopular, rvPopular);
    }

    private void fetchTopRatedMovies() {
        Callable<List<TopRated>> callable =
                () -> {
                    TopRatedApi api = new TopRatedApi();
                    return api.getTopRatedMovies(1);
                };
        fetchData(callable, topRatedList, topRatedAdapter, shimmerTopRated, rvTopRated);
    }

    private void fetchUpComingMovies() {
        Callable<List<UpComing>> callable =
                () -> {
                    UpComingApi api = new UpComingApi();
                    return api.getUpComingMovies(1);
                };
        fetchData(callable, upComingList, upComingAdapter, shimmerUpComing, rvUpComing);
    }

    private <T> void fetchData(
            Callable<List<T>> callable,
            List<T> list,
            RecyclerView.Adapter<?> adapter,
            ShimmerFrameLayout shimmerLayout,
            RecyclerView recyclerView) {
        executor.submit(
                () -> {
                    try {
                        List<T> result = callable.call();
                        List<T> oldList = new ArrayList<>(list);
                        list.clear();
                        list.addAll(result);
                        calculateDiffAndDispatchUpdates(
                                oldList, list, adapter, shimmerLayout, recyclerView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private <T> void calculateDiffAndDispatchUpdates(
            List<T> oldList,
            List<T> newList,
            RecyclerView.Adapter<?> adapter,
            ShimmerFrameLayout shimmerLayout,
            RecyclerView recyclerView) {
        DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(new MainDiffCallback<>(oldList, newList));
        runOnUiThread(
                () -> {
                    result.dispatchUpdatesTo(adapter);
                    stopShimmer(shimmerLayout, recyclerView);
                });
    }

    private void startShimmer(ShimmerFrameLayout shimmerLayout) {
        shimmerLayout.startShimmer();
    }

    private void stopShimmer(ShimmerFrameLayout shimmerLayout, RecyclerView recyclerView) {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (trendingList.isEmpty()) {
            fetchTrendingMovies("day");
        }
        if (nowPlayingList.isEmpty()) {
            fetchNowPlayingMovies();
        }
        if (popularList.isEmpty()) {
            fetchPopularMovies();
        }
        if (topRatedList.isEmpty()) {
            fetchTopRatedMovies();
        }
        if (upComingList.isEmpty()) {
            fetchUpComingMovies();
        }
    }
}
