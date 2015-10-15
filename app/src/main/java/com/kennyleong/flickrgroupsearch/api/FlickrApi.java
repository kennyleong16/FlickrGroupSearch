package com.kennyleong.flickrgroupsearch.api;

import com.kennyleong.flickrgroupsearch.model.GroupSearchResult;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Kenny Leong on 10/15/2015.
 */
public interface FlickrApi {
    @GET("/services/rest/?method=flickr.groups.search&api_key=ff7bb2c27e4aaa3defd1e83527ba86a4&per_page=15&format=json&nojsoncallback=1")
    Call<GroupSearchResult> searchGroupName(@Query("text") String name, @Query("page") int page);

}
