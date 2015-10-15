package com.kennyleong.flickrgroupsearch.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Kenny Leong on 10/15/2015.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;

    private int currentPage = 1;
    private int totalPages;
    private boolean isLoading = false;
    private int totalEntries;
    private int prevItemCount = 0;
    private int prevPage = 1;


    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;

    }

    public abstract void onLoadMore(int newPage);

    public void setTotalEntries (int totalEntries) {
        this.totalEntries = totalEntries;
    }

    public void refresh() {
        currentPage = 1;
        prevItemCount = 0;
    }



    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

        if (isLoading) {

            int difference = totalItemCount - prevItemCount;

            if (difference > 1 || totalItemCount >= totalEntries) {
                isLoading = false;
                prevItemCount = totalItemCount;
            }

        }
        else {

            if (totalItemCount >= totalEntries) {
                //last page
            }
            else {
                if ((firstVisibleItem + visibleItemCount) >= totalItemCount && totalItemCount < totalEntries) {

                    onLoadMore(++currentPage);
                    isLoading = true;
                    prevItemCount = totalItemCount;

                }
            }


        }

    }
}
