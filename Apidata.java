package com.quantrium.verifydoc;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Apidata {
   // @FormUrlEncoded
    @Multipart
    @POST("Parser")
    //panVerification
    //one_to_one
    Call<ResponseData> callApi(@Part MultipartBody.Part requestPart);
    //FieldMap Map<String,byte[]> parameter
}
