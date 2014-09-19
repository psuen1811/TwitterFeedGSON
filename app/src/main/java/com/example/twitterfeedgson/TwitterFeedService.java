package com.example.twitterfeedgson;

/**
 * Created by paksuen on 9/18/14.
 */

import java.util.ArrayList;
import retrofit.http.GET;

public interface TwitterFeedService {

    @GET("/twitteroauth/pak.php")
    ArrayList<TwitterFeedContainer> getFeeds();
}
