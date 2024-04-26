package com.themoviedb.app.upcoming;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpComingApi {
    private static final String API_BASE_URL = "https://api.themoviedb.org/3/movie/upcoming";
    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkM2U0M2ZlNWMzMWU5YTg2MTQzODVkNTViMzEyMjE3NSIsInN1YiI6IjY1Zjk5Y2Q5Nzk4Yzk0MDE4NTE2ODE3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FXWGIarbZVRQNUqsaS0_uuBguQsxdqwCsYpoGlSwW-8";

    public List<UpComing> getUpComingMovies(int page) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = API_BASE_URL + "?language=en-US&page=" + page;

        Request request =
                new Request.Builder()
                        .url(apiUrl)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        List<UpComing> upComingMovies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);

                UpComing upComing =
                        new UpComing(
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

                upComingMovies.add(upComing);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return upComingMovies;
    }

    public static void main(String[] args) {
        UpComingApi api = new UpComingApi();
        int page = 1;
        try {
            List<UpComing> upComingMovies = api.getUpComingMovies(page);
            do {
                for (UpComing movie : upComingMovies) {
                    System.out.println("Title: " + movie.getTitle());
                    System.out.println("Overview: " + movie.getOverview());
                    System.out.println("Poster_Url: " + movie.getPosterPath());
                    System.out.println("Vote: " + movie.getVoteAverage());
                    System.out.println("Release Date: " + movie.getReleaseDate());
                    System.out.println("Popularity: " + movie.getPopularity());
                    System.out.println("original_language : " + movie.getOriginalLanguage());
                    System.out.println("vote_count: " + movie.getVoteCount());
                    System.out.println("backdrop_path: " + movie.getBackdropPath());
                }
                page++;
            } while (upComingMovies.size() > 0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
