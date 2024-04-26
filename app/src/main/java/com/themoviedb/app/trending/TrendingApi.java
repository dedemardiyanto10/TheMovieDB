package com.themoviedb.app.trending;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrendingApi {
    private static final String API_BASE_URL_DAY =
            "https://api.themoviedb.org/3/trending/movie/day";
    private static final String API_BASE_URL_WEEK =
            "https://api.themoviedb.org/3/trending/movie/week";
    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkM2U0M2ZlNWMzMWU5YTg2MTQzODVkNTViMzEyMjE3NSIsInN1YiI6IjY1Zjk5Y2Q5Nzk4Yzk0MDE4NTE2ODE3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FXWGIarbZVRQNUqsaS0_uuBguQsxdqwCsYpoGlSwW-8";

    public List<Trending> getTrendingMovies(String timeFrame, int page)
            throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = timeFrame.equals("day") ? API_BASE_URL_DAY : API_BASE_URL_WEEK;
        apiUrl += "?language=en-US&page=" + page + "&api_key=" + API_KEY;

        Request request =
                new Request.Builder()
                        .url(apiUrl)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        List<Trending> trendingMovies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);

                Trending trending =
                        new Trending(
                                movieObject.optInt("id"),
                                movieObject.optString("title"),
                                movieObject.optString("overview"),
                                movieObject.optString("poster_path"),
                                movieObject.optDouble("vote_average"),
                                movieObject.optString("release_date"),
                                movieObject.optDouble("popularity"),
                                movieObject.optString("original_language"),
                                movieObject.optDouble("vote_count"),
                                movieObject.optString("backdrop_path"));

                trendingMovies.add(trending);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trendingMovies;
    }
}
