package com.kennyleong.flickrgroupsearch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.kennyleong.flickrgroupsearch.api.FlickrApi;
import com.kennyleong.flickrgroupsearch.model.Group;
import com.kennyleong.flickrgroupsearch.model.GroupSearchResult;
import com.kennyleong.flickrgroupsearch.ui.EndlessRecyclerOnScrollListener;
import com.kennyleong.flickrgroupsearch.ui.GroupListAdapter;

import java.util.Collections;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.support.v7.widget.SearchView.*;

public class MainActivity extends AppCompatActivity implements OnQueryTextListener, Callback<GroupSearchResult> {

    private final static String FLICKR_BASE_URL = "https://api.flickr.com";

    private MenuItem search;
    private String currentQuery;
    private SearchView searchView;
    private RecyclerView search_result_view;
    private LinearLayoutManager linearLayoutManager;
    private int currentPage;
    private GroupListAdapter groupListAdapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    private FlickrApi flickrApi;

    private ProgressBar loading_result_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MenuItemCompat.expandActionView(search);


            }
        });

        search_result_view = (RecyclerView) findViewById(R.id.search_result_view);
        linearLayoutManager = new LinearLayoutManager(this);
        search_result_view.setLayoutManager(linearLayoutManager);
        search_result_view.setAdapter(new GroupListAdapter(Collections.<Group> emptyList()));

        loading_result_view = (ProgressBar) findViewById(R.id.loading_result_view);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        search = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (!query.equalsIgnoreCase("")) {
            loading_result_view.setVisibility(View.VISIBLE);
            currentQuery = query;
            currentPage = 1;
            search_result_view.removeOnScrollListener(endlessRecyclerOnScrollListener);

            setTitle(String.format(getString(R.string.search_result_title), currentQuery));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FLICKR_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            flickrApi = retrofit.create(FlickrApi.class);
            Call<GroupSearchResult> call = flickrApi.searchGroupName(currentQuery, currentPage);

            call.enqueue(this);

            MenuItemCompat.collapseActionView(search);


        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public void onResponse(Response<GroupSearchResult> response, Retrofit retrofit) {

        loading_result_view.setVisibility(View.INVISIBLE);

        if (response.message().equalsIgnoreCase("OK")) {

            if (currentPage == 1) {

                int total = response.body().groups.total;

                if (total > 0) {

                    List<Group> data = response.body().groups.group;
                    groupListAdapter = new GroupListAdapter(data);

                    search_result_view.setAdapter(groupListAdapter);

                    endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
                        @Override
                        public void onLoadMore(int newPage) {

                            groupListAdapter.add(null); //to create loading footer

                            Call<GroupSearchResult> loadMoreCall = flickrApi.searchGroupName(currentQuery, newPage);
                            currentPage = newPage;
                            loadMoreCall.enqueue(MainActivity.this);

                        }
                    };
                    endlessRecyclerOnScrollListener.setTotalEntries(total);
                    search_result_view.addOnScrollListener(endlessRecyclerOnScrollListener);
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.search_no_result), Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.BLUE)
                            .show();
                }





            }
            else {

                groupListAdapter.remove(groupListAdapter.getItemCount()-1); //remove the loading footer

                try {
                    List<Group> newPageData = response.body().groups.group;
                    groupListAdapter.addAll(newPageData);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onFailure(Throwable t) {

        Snackbar.make(findViewById(android.R.id.content), t.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }
}
