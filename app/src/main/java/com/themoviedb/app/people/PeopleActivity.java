package com.themoviedb.app.people;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.themoviedb.app.CastMovies;
import com.themoviedb.app.CastMoviesAdapter;
import com.themoviedb.app.DetailMovieActivity;
import com.themoviedb.app.PeopleDetails;
import com.themoviedb.app.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PeopleActivity extends AppCompatActivity {

    private TextView tvTitle,
            tvKnownAs,
            tvRating,
            tvDepartment,
            tvGender,
            tvBirthday,
            tvPlace,
            tvDeathday,
            tvBiography;
    private RecyclerView rvPeople, rvCastMovies;
    private final List<String> peoplePhotoList = new ArrayList<>();

    private CastMoviesAdapter castMoviesAdapter;
    private final List<CastMovies> castMoviesList = new ArrayList<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkM2U0M2ZlNWMzMWU5YTg2MTQzODVkNTViMzEyMjE3NSIsInN1YiI6IjY1Zjk5Y2Q5Nzk4Yzk0MDE4NTE2ODE3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FXWGIarbZVRQNUqsaS0_uuBguQsxdqwCsYpoGlSwW-8";

    private PeopleDetails peopleDetails = new PeopleDetails();
    private boolean isDataFetched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        tvTitle = findViewById(R.id.tvTitle);
        tvKnownAs = findViewById(R.id.tvKnownAs);
        tvRating = findViewById(R.id.tvRating);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvGender = findViewById(R.id.tvGender);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvPlace = findViewById(R.id.tvPlace);
        tvDeathday = findViewById(R.id.tvDeathday);
        tvBiography = findViewById(R.id.tvBiography);

        rvCastMovies = findViewById(R.id.rvCastMovies);

        castMoviesAdapter = new CastMoviesAdapter(this, castMoviesList);
        LinearLayoutManager castMoviesLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCastMovies.setLayoutManager(castMoviesLayoutManager);

        castMoviesAdapter.setOnItemClickListener(
                new CastMoviesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position >= 0 && position < castMoviesList.size()) {
                            CastMovies castMovies = castMoviesList.get(position);

                            Intent intent =
                                    new Intent(PeopleActivity.this, DetailMovieActivity.class);
                            intent.putExtra("id", castMovies.getId());
                            intent.putExtra("title", castMovies.getTitle());
                            intent.putExtra("posterPath", castMovies.getPosterPath());
                            intent.putExtra("backdropPath", castMovies.getBackdropPath());
                            intent.putExtra("overview", castMovies.getOverview());
                            intent.putExtra("releaseDate", castMovies.getReleaseDate());
                            intent.putExtra("originalLanguage", castMovies.getOriginalLanguage());
                            intent.putExtra("voteCount", castMovies.getVoteCount());
                            intent.putExtra("voteAverage", castMovies.getVoteAverage());
                            intent.putExtra("popularity", castMovies.getPopularity());

                            startActivity(intent);
                        }
                    }
                });

        rvPeople = findViewById(R.id.rvPeople);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvPeople);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(
                v -> {
                    finish();
                });

        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("name");

            toolbar.setTitle(name);
            toolbar.setSubtitle(String.valueOf(id));

            tvTitle.setText(name);
            fetchPeopleDetails(id);
            fetchPeoplePhoto(id);
            fetchCastMovies(id);
        }
    }

    private void fetchPeopleDetails(int id) {
        executorService.submit(
                () -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request =
                                new Request.Builder()
                                        .url(
                                                "https://api.themoviedb.org/3/person/"
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
                            fetchPeopleDetails(jsonObject);
                        } else {
                            Log.e(
                                    "DetailPeopleActivity",
                                    "Failed to fetch movie details. Code: " + response.code());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void fetchPeopleDetails(JSONObject jsonObject) {
        try {

            JSONArray knownAsArray = jsonObject.getJSONArray("also_known_as");
            StringBuilder knownAsStringBuilder = new StringBuilder();
            for (int i = 0; i < knownAsArray.length(); i++) {
                knownAsStringBuilder.append(knownAsArray.getString(i));
                if (i < knownAsArray.length() - 1) {
                    knownAsStringBuilder.append(", ");
                }
            }
            peopleDetails.setKnownAs(knownAsStringBuilder.toString());

            double popularity = jsonObject.getDouble("popularity");
            peopleDetails.setPopularity(popularity);

            String department = jsonObject.getString("known_for_department");
            peopleDetails.setDepartment(department);

            String genderString;
            int genderCode = jsonObject.getInt("gender");

            if (genderCode == 1) {
                genderString = "Female";
            } else if (genderCode == 2) {
                genderString = "Male";
            } else {
                genderString = "Non-binary";
            }

            peopleDetails.setGender(genderString);

            String birthday = jsonObject.getString("birthday");
            peopleDetails.setBirthday(birthday);

            String place = jsonObject.getString("place_of_birth");
            peopleDetails.setPlace(place);

            String deathday = jsonObject.getString("deathday");
            peopleDetails.setDeathday(deathday);

            String biography = jsonObject.getString("biography");
            peopleDetails.setBiography(biography);

            this.peopleDetails = peopleDetails;

            runOnUiThread(
                    () -> {
                        tvKnownAs.setText(peopleDetails.getKnownAs());
                        tvRating.setText(String.valueOf(peopleDetails.getPopularity()));
                        tvDepartment.setText(peopleDetails.getDepartment());
                        tvGender.setText(peopleDetails.getGender());
                        tvBirthday.setText(peopleDetails.getBirthday());
                        tvPlace.setText(peopleDetails.getPlace());
                        tvDeathday.setText(peopleDetails.getDeathday());
                        tvBiography.setText(peopleDetails.getBiography());
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchPeoplePhoto(int id) {
        executorService.submit(
                () -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request =
                                new Request.Builder()
                                        .url(
                                                "https://api.themoviedb.org/3/person/"
                                                        + id
                                                        + "/images")
                                        .get()
                                        .addHeader("accept", "application/json")
                                        .addHeader("Authorization", "Bearer " + API_KEY)
                                        .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray profilesArray = jsonObject.getJSONArray("profiles");
                            processPeoplePhoto(profilesArray);
                        } else {
                            Log.e(
                                    "PeopleActivity",
                                    "Failed to fetch people photo. Code: " + response.code());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processPeoplePhoto(JSONArray profilesArray) {
        try {
            List<String> uniqueKeys = new ArrayList<>();
            for (int i = 0; i < profilesArray.length(); i++) {
                JSONObject peoplePhotoObject = profilesArray.getJSONObject(i);
                String peoplePhoto = peoplePhotoObject.getString("file_path");
                uniqueKeys.add(peoplePhoto);
            }
            peoplePhotoList.clear();
            peoplePhotoList.addAll(uniqueKeys);
            runOnUiThread(
                    () -> {
                        PeopleAdapter adapter = new PeopleAdapter(peoplePhotoList);
                        LinearLayoutManager peopleLayoutManager =
                                new LinearLayoutManager(
                                        this, LinearLayoutManager.HORIZONTAL, false);
                        rvPeople.setLayoutManager(peopleLayoutManager);
                        rvPeople.setAdapter(adapter);
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchCastMovies(int id) {
        executorService.submit(
                () -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request =
                                new Request.Builder()
                                        .url(
                                                "https://api.themoviedb.org/3/person/"
                                                        + id
                                                        + "/movie_credits?language=en-US?")
                                        .get()
                                        .addHeader("accept", "application/json")
                                        .addHeader("Authorization", "Bearer " + API_KEY)
                                        .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray castMoviesArray = jsonObject.getJSONArray("cast");
                            processCastMovies(castMoviesArray);
                        } else {
                            Log.e(
                                    "PeopleActivity",
                                    "Failed to fetch cast movies. Code: " + response.code());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processCastMovies(JSONArray castMoviesArray) {
        try {
            castMoviesList.clear();
            for (int i = 0; i < castMoviesArray.length(); i++) {
                JSONObject movieObject = castMoviesArray.getJSONObject(i);
                int id = movieObject.optInt("id");
                String title = movieObject.optString("title");
                String overview = movieObject.optString("overview");
                String posterPath = movieObject.optString("poster_path");
                double voteAverage = movieObject.optDouble("vote_average");
                String releaseDate = movieObject.optString("release_date");
                double popularity = movieObject.optDouble("popularity");
                String originalLanguage = movieObject.optString("original_language");
                double voteCount = movieObject.optDouble("vote_count");
                String backdropPath = movieObject.optString("backdrop_path");

                CastMovies cashMovies =
                        new CastMovies(
                                id,
                                title,
                                overview,
                                posterPath,
                                voteAverage,
                                releaseDate,
                                popularity,
                                originalLanguage,
                                voteCount,
                                backdropPath);
                castMoviesList.add(cashMovies);
            }

            runOnUiThread(
                    () -> {
                        rvCastMovies.setAdapter(castMoviesAdapter);
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
                fetchPeopleDetails(id);
                fetchPeoplePhoto(id);
                fetchCastMovies(id);
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
