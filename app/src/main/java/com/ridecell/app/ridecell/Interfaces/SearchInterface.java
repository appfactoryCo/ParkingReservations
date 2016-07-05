package com.ridecell.app.ridecell.Interfaces;



import com.ridecell.app.ridecell.DataModels.SpotData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface SearchInterface {
    @GET("/api/v1/parkinglocations/search") // Using Retrofit2
    Call<List<SpotData>> getSpots(@Query("lat") String query, @Query("lng") String type);
}



