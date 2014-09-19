package com.example.twitterfeedgson;

/**
 * Created by paksuen on 9/18/14.
 */
import retrofit.RestAdapter;

public class AdapterService {

    private final static String URL_BASE = "http://www.themasterpak.com";

    RestAdapter theMasterPakAdapter = new RestAdapter.Builder()
            .setEndpoint(URL_BASE)
            .build();

    private AdapterService()
    {

    }

    public RestAdapter getTheMasterPakAdapter()
    {
        return this.theMasterPakAdapter;
    }

    private static class SingletonHolder {
        public static final AdapterService INSTANCE = new AdapterService();
    }

    public static AdapterService getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
