package com.digitaloutlet.service

import com.digitaloutlet.model.request.PublishProducts
import com.digitaloutlet.model.request.ReqProductsByParentID
import com.digitaloutlet.model.request.ReqPublishProducts
import com.digitaloutlet.model.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface APIClient {

    @GET("digioutlet/checkMerchantRegistrationVer2")
    fun checkMerchantRegistration(@QueryMap map: Map<String, String>): Call<ResCheckMerchantStatus>

    @GET("digioutlet/generateOTP")
    fun generateOTP(@QueryMap map: Map<String, String>): Call<ResGenerateOTP>

    @GET("digioutlet/validateOTPVer2")
    fun validateOTP(@QueryMap map: Map<String, String>): Call<ResValidateOTP>

    @GET("digioutlet/deleteStatus")
    fun logout(@QueryMap map: Map<String, String>): Call<ResLogout>

    @GET("getTOS")
    fun getTOS(@QueryMap map: Map<String, String>): Call<ResTOS>

    @GET("digioutlet/saveMerchantVer2")
    fun saveMerchant(@QueryMap map: Map<String, String>): Call<ResSaveMerchant>

    @GET("getParentCat")
    fun getParentCategories(@QueryMap map: Map<String, String>): Call<ResParentCategory>

    @POST("getProductsBaseOnParentId")
    fun getProductDetails(@Body parentIds: ReqProductsByParentID): Call<ResProducts>

    @POST("MerchantProductCRUD")
    fun publishProducts(@Body reqPublishProducts: ReqPublishProducts): Call<ResPublishProduct>

    @GET("searchProductDetails?query=suthar")
    fun searchProductQuery(@QueryMap map: Map<String, String>): Call<ResProducts>

    @GET("digioutlet/getProductDetailsOnMSIDN")
    fun getProductDetailsOnMSISDN(@QueryMap map: Map<String, String>): Call<ResProductsOnMsisdn>
}