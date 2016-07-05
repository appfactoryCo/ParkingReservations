package com.ridecell.app.ridecell.Interfaces;


import com.ridecell.app.ridecell.DataModels.SpotData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReserveInterfacePATCH {
    @PATCH("/api/v1/parkinglocations/{id}")
    Call<SpotData> reserveSpot(@Path("id") int id, @Body SpotData spotData);
}



