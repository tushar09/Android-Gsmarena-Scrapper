package com.captaindroid.gsmarena.scrapper.services;

import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/phone/addPhoneBrands")
    Call<Void> saveAllPhoneBrands(@Body List<PhoneBrand> brands);

    @POST("/api/v1/phone/addPhoneDetails")
    Call<List<PhoneModel>> saveAllPhoneDetails(@Body List<PhoneModel> models);
}
