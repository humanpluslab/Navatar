package com.navatar.data.source;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NavHistoryRepository implements NavHistoryDataSource {


    private final NavHistoryDataSource mNavHistoryDataSource;

    @Inject
    NavHistoryRepository(@Local NavHistoryDataSource navHistoryLocalDataSource) {
        mNavHistoryDataSource = navHistoryLocalDataSource;
    }

}
