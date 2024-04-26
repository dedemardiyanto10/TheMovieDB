package com.themoviedb.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.MaterialToolbar;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailMovieActivity extends AppCompatActivity {

    private TextView tvBudget;
    private TextView tvGenres;
    private TextView tvProductionCompanies;
    private TextView tvRuntime;
    private TextView tvStatus;
    private TextView tvRevenue;
    private TextView tvTagLine;
    private ImageView ivCompanies;
    private RecyclerView rvTrailer;

    private final List<String> trailerKeysList = new ArrayList<>();

    private MovieDetails movieDetails = new MovieDetails();

    private RecyclerView rvCast;
    private CastAdapter castAdapter;
    private final List<Cast> castList = new ArrayList<>();

    private boolean isDataFetched = false;

    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkM2U0M2ZlNWMzMWU5YTg2MTQzODVkNTViMzEyMjE3NSIsInN1YiI6IjY1Zjk5Y2Q5Nzk4Yzk0MDE4NTE2ODE3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FXWGIarbZVRQNUqsaS0_uuBguQsxdqwCsYpoGlSwW-8";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Window window = getWindow();
        window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        //  tvId = findViewById(R.id.tvId);
        TextView tvTitle = findViewById(R.id.tvTitle);
        RoundedImageView ivPoster = findViewById(R.id.ivPoster);
        ImageView kbvBackdrop = findViewById(R.id.kbvBackdrop);

        TextView tvLanguage = findViewById(R.id.tvLanguage);
        TextView tvReleaseDate = findViewById(R.id.tvReleaseDate);
        TextView tvOverView = findViewById(R.id.tvOverView);

        TextView tvPopularity = findViewById(R.id.tvPopularity);
        TextView tvVoteCount = findViewById(R.id.tvVoteCount);
        TextView tvVoteAverage = findViewById(R.id.tvVoteAverage);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        tvBudget = findViewById(R.id.tvBudget);
        tvGenres = findViewById(R.id.tvGenres);
        tvProductionCompanies = findViewById(R.id.tvProductionCompanies);

        tvRuntime = findViewById(R.id.tvRuntime);
        tvStatus = findViewById(R.id.tvStatus);
        tvRevenue = findViewById(R.id.tvRevenue);
        tvTagLine = findViewById(R.id.tvTagLine);

        ivCompanies = findViewById(R.id.ivCompanies);

        rvTrailer = findViewById(R.id.rvTrailer);

        rvCast = findViewById(R.id.rvCast);

        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id", 0);
            String title = intent.getStringExtra("title");
            String posterPath = intent.getStringExtra("posterPath");
            String backdropPath = intent.getStringExtra("backdropPath");
            String overview = intent.getStringExtra("overview");
            String releaseDate = intent.getStringExtra("releaseDate");
            String originalLanguage = intent.getStringExtra("originalLanguage");
            double voteAverage = intent.getDoubleExtra("voteAverage", 0);
            double voteCount = intent.getDoubleExtra("voteCount", 0);
            double popularity = intent.getDoubleExtra("popularity", 0);

            tvTitle.setText(title);
            //  tvId.setText(String.valueOf(id));
            toolbar.setTitle(title);
            toolbar.setSubtitle(String.valueOf(id));

            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w780" + posterPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPoster);

            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w780" + backdropPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(kbvBackdrop);

            tvLanguage.setText(originalLanguage.toUpperCase());
            tvOverView.setText(overview);
            tvReleaseDate.setText(releaseDate);

            tvPopularity.setText(String.valueOf(popularity));
            tvVoteCount.setText(String.valueOf(voteCount));

            String formattedRating = String.format("%.1f", voteAverage).replace(",", ".");
            tvVoteAverage.setText(formattedRating + " / 10");
            float newValue = (float) (voteAverage / 2);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize(0.5f);
            ratingBar.setRating(newValue);

            fetchMovieDetails(id);
            fetchMovieTrailers(id);
            fetchMovieCast(id);
        }
    }

    private void fetchMovieDetails(int id) {
        executorService.submit(
                () -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request =
                                new Request.Builder()
                                        .url(
                                                "https://api.themoviedb.org/3/movie/"
                                                        + id
                                                        + "?language=en-US")
                                        .get()
                                        .addHeader("accept", "application/json")
                                        .addHeader("Authorization", "Bearer " + API_KEY)
                                        .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            updateUI(jsonObject);
                        } else {
                            Log.e(
                                    "DetailMovieActivity",
                                    "Failed to fetch movie details. Code: " + response.code());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void updateUI(JSONObject jsonObject) {
        try {

            double budget = jsonObject.getDouble("budget");
            String formattedBudget = String.format("$%,.2f", budget);
            movieDetails.setBudget(formattedBudget);

            JSONArray genresArray = jsonObject.getJSONArray("genres");
            StringBuilder genresStringBuilder = new StringBuilder();
            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject genreObject = genresArray.getJSONObject(i);
                String genreName = genreObject.getString("name");
                genresStringBuilder.append(genreName);
                if (i < genresArray.length() - 1) {
                    genresStringBuilder.append(", ");
                }
            }
            movieDetails.setGenres(genresStringBuilder.toString());

            JSONArray productionCompaniesArray = jsonObject.getJSONArray("production_companies");
            StringBuilder productionCompaniesStringBuilder = new StringBuilder();
            for (int i = 0; i < productionCompaniesArray.length(); i++) {
                JSONObject companyObject = productionCompaniesArray.getJSONObject(i);
                String companyName = companyObject.getString("name");
                String originCountry = companyObject.getString("origin_country");
                String logoPath = companyObject.optString("logo_path", "");

                productionCompaniesStringBuilder
                        .append(companyName)
                        .append(" (")
                        .append(originCountry)
                        .append(")");
                if (i < productionCompaniesArray.length() - 1) {
                    productionCompaniesStringBuilder.append(", ");
                }
                if (i == 0) {
                    movieDetails.setCompanyName(companyName);
                    movieDetails.setOriginCountry(originCountry);
                    movieDetails.setLogoPath(logoPath);
                }
            }
            movieDetails.setProductionCompanies(productionCompaniesStringBuilder.toString());

            String runtime = jsonObject.getString("runtime");
            int totalMinutes = Integer.parseInt(runtime);
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            String formattedRuntime = hours + " Hours " + minutes + " Minutes";
            movieDetails.setRuntime(formattedRuntime);

            String status = jsonObject.getString("status");
            movieDetails.setStatus(status.toUpperCase());

            double revenue = jsonObject.getDouble("revenue");
            String formattedRevenue = String.format("$%,.2f", revenue);
            movieDetails.setRevenue(formattedRevenue);

            String tagline = jsonObject.getString("tagline");
            movieDetails.setTagline(tagline);

            this.movieDetails = movieDetails;

            runOnUiThread(
                    () -> {
                        tvBudget.setText(movieDetails.getBudget());
                        tvGenres.setText(movieDetails.getGenres());
                        tvProductionCompanies.setText(movieDetails.getProductionCompanies());
                        tvRuntime.setText(movieDetails.getRuntime());
                        tvStatus.setText(movieDetails.getStatus());
                        tvRevenue.setText(movieDetails.getRevenue());
                        tvTagLine.setText(movieDetails.getTagline());

                        if (!isDestroyed()) {

                            Glide.with(this)
                                    .load(
                                            "https://image.tmdb.org/t/p/w780"
                                                    + movieDetails.getLogoPath())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCompanies);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchMovieTrailers(int id) {
        executorService.submit(
                () -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request =
                                new Request.Builder()
                                        .url(
                                                "https://api.themoviedb.org/3/movie/"
                                                        + id
                                                        + "/videos?language=en-US")
                                        .get()
                                        .addHeader("accept", "application/json")
                                        .addHeader("Authorization", "Bearer " + API_KEY)
                                        .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray resultsArray = jsonObject.getJSONArray("results");
                            // Proses daftar trailer
                            processTrailers(resultsArray);
                        } else {
                            Log.e(
                                    "DetailMovieActivity",
                                    "Failed to fetch movie trailers. Code: " + response.code());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processTrailers(JSONArray trailersArray) {
        try {
            HashSet<String> uniqueKeys = new HashSet<>();
            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailerObject = trailersArray.getJSONObject(i);
                String trailerKey = trailerObject.getString("key");
                if (!uniqueKeys.contains(trailerKey)) {
                    uniqueKeys.add(trailerKey);
                }
            }
            trailerKeysList.clear();
            trailerKeysList.addAll(uniqueKeys);
            runOnUiThread(
                    () -> {
                        TrailerAdapter adapter = new TrailerAdapter(trailerKeysList);
                        LinearLayoutManager trailerLayoutManager =
                                new LinearLayoutManager(
                                        this, LinearLayoutManager.HORIZONTAL, false);
                        rvTrailer.setLayoutManager(trailerLayoutManager);
                        rvTrailer.setAdapter(adapter);
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchMovieCast(int id) {
        executorService.submit(
                () -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request =
                                new Request.Builder()
                                        .url(
                                                "https://api.themoviedb.org/3/movie/"
                                                        + id
                                                        + "/credits?language=en-US")
                                        .get()
                                        .addHeader("accept", "application/json")
                                        .addHeader("Authorization", "Bearer " + API_KEY)
                                        .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray castArray = jsonObject.getJSONArray("cast");
                            processCast(castArray);
                        } else {
                            Log.e(
                                    "DetailMovieActivity",
                                    "Failed to fetch movie cast. Code: " + response.code());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processCast(JSONArray castArray) {
        try {
            ArrayList<Cast> castList = new ArrayList<>();
            for (int i = 0; i < castArray.length(); i++) {
                JSONObject castObject = castArray.getJSONObject(i);
                int castId = castObject.getInt("id");
                String castName = castObject.getString("name");
                String profilePath = castObject.getString("profile_path");
                String castCharacter = castObject.getString("character");
                Cast cast = new Cast(castId, castName, profilePath, castCharacter);
                castList.add(cast);
            }
            runOnUiThread(
                    () -> {
                        castAdapter = new CastAdapter(this, castList);
                        LinearLayoutManager castLayoutManager =
                                new LinearLayoutManager(
                                        this, LinearLayoutManager.HORIZONTAL, false);
                        rvCast.setLayoutManager(castLayoutManager);
                        rvCast.setAdapter(castAdapter);
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id", 0);
            if (!isDataFetched) {
                fetchMovieDetails(id);
                fetchMovieTrailers(id);
                fetchMovieCast(id);
                isDataFetched = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
