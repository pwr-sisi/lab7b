package edu.wt.w07b;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;


public interface StackOverflowAPI {
    String BASE_URL = "https://api.stackexchange.com/2.2/";

    @GET("search?order=desc&sort=activity&site=stackoverflow")
    Call<QuestionsList<Question>> getQuestions(@Query("intitle") String title);
}