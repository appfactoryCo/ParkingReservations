package com.ridecell.app.ridecell.Interfaces;


import com.ridecell.app.ridecell.DataModels.SpotData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReserveInterface {
    @POST("/api/v1/parkinglocations/{id}/reserve/")
    Call<SpotData> reserveSpot(@Path("id") int id, @Body SpotData spotData);
}



