package com.themoviedb.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private SearchAdapter searchAdapter;
    private final List<Search> searchList = new ArrayList<>();
    private EditText etSearch;
    private Button btnClear;
    private int currentPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        RecyclerView rvSearchQuery = findViewById(R.id.rvSearchQuery);
        etSearch = findViewById(R.id.etSearch);

        etSearch.requestFocus();
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);

        btnClear = findViewById(R.id.btnClear);

        searchAdapter = new SearchAdapter(this, searchList);
        rvSearchQuery.setLayoutManager(new GridLayoutManager(this, 3));
        rvSearchQuery.setAdapter(searchAdapter);

        btnClear.setOnClickListener(v -> etSearch.setText(""));

        etSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchMovies(s.toString());
                        btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        searchAdapter.setOnItemClickListener(
                new SearchAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < searchList.size()) {
                            Search search = searchList.get(position);

                            Intent intent =
                                    new Intent(SearchActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", search.getId());
                            intent.putExtra("title", search.getTitle());
                            intent.putExtra("posterPath", search.getPosterPath());
                            intent.putExtra("backdropPath", search.getBackdropPath());
                            intent.putExtra("overview", search.getOverview());
                            intent.putExtra("releaseDate", search.getReleaseDate());
                            intent.putExtra("originalLanguage", search.getOriginalLanguage());
                            intent.putExtra("voteCount", search.getVoteCount());
                            intent.putExtra("voteAverage", search.getVoteAverage());
                            intent.putExtra("popularity", search.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvSearchQuery.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (!isLoading && !recyclerView.canScrollVertically(1)) {
                            loadNextPage();
                        }
                    }
                });
    }

    private void loadNextPage() {
        isLoading = true;
        currentPage++;

        OkHttpClient client = new OkHttpClient();

        Request request =
                new Request.Builder()
                        .url(
                                "https://api.themoviedb.org/3/search/movie?query="
                                        + etSearch.getText().toString()
                                        + "&include_adult=false&language=en-US&page="
                                        + currentPage)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader(
                                "Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkM2U0M2ZlNWMzMWU5YTg2MTQzODVkNTViMzEyMjE3NSIsInN1YiI6IjY1Zjk5Y2Q5Nzk4Yzk0MDE4NTE2ODE3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FXWGIarbZVRQNUqsaS0_uuBguQsxdqwCsYpoGlSwW-8")
                        .build();

        client.newCall(request)
                .enqueue(
                        new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                                isLoading = false;
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response)
                                    throws IOException {
                                if (response.isSuccessful()) {
                                    try {
                                        assert response.body() != null;
                                        JSONArray resultsArray =
                                                new JSONObject(response.body().string())
                                                        .getJSONArray("results");
                                        List<Search> newSearchList = new ArrayList<>();
                                        for (int i = 0; i < resultsArray.length(); i++) {
                                            JSONObject movieObject = resultsArray.getJSONObject(i);
                                            newSearchList.add(
                                                    new Search(
                                                            movieObject.optInt("id"),
                                                            movieObject.optString("title"),
                                                            movieObject.optString("overview"),
                                                            movieObject.optString("poster_path"),
                                                            movieObject.optDouble("vote_average"),
                                                            movieObject.optString("release_date"),
                                                            movieObject.optDouble("popularity"),
                                                            movieObject.optString(
                                                                    "original_language"),
                                                            movieObject.optDouble("vote_count"),
                                                            movieObject.optString(
                                                                    "backdrop_path")));
                                        }
                                        runOnUiThread(
                                                () -> {
                                                    int startPosition = searchList.size();
                                                    searchList.addAll(newSearchList);
                                                    searchAdapter.notifyItemRangeInserted(
                                                            startPosition, newSearchList.size());
                                                    isLoading = false;
                                                });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
    }

    private void searchMovies(String query) {
        int itemCount = searchList.size();
        searchList.clear();
        searchAdapter.notifyItemRangeRemoved(0, itemCount);
        currentPage = 1;
        if (!query.isEmpty()) {
            loadNextPage();
        }
    }
}
