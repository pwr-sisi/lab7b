package edu.wt.w07b;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.Query;


public interface StackOverflowAPI {
    String BASE_URL = "https://api.stackexchange.com/2.2/";

    @GET("search?order=desc&sort=activity&site=stackoverflow")
    Call<ListWrapper<ItemQuestion>> getQuestions(@Query("intitle") String title);
}