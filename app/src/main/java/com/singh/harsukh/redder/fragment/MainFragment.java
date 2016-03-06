package com.singh.harsukh.redder.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.singh.harsukh.redder.BuildConfig;
import com.singh.harsukh.redder.R;
import com.singh.harsukh.redder.adapter.MainAdapter;
import com.singh.harsukh.redder.data.RedditAPI;
import com.singh.harsukh.redder.model.Listing;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by nano1 on 3/5/2016.
 */
public class MainFragment extends Fragment implements MainAdapter.ClickListener{

    private List<Listing.DataEntity.ChildrenEntity> childrenEntities;
    private Listing listing;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private String section;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        Bundle bundle = getArguments();
        section = bundle.getString("title");
        if (section == null){
            section = "askreddit";
        }

        recyclerView = (RecyclerView) layout.findViewById(R.id.main_recycleView);
        mainAdapter = new MainAdapter(getActivity(),childrenEntities);
        mainAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mainAdapter);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    public void fetchData(){
        final String BASE_URL = BuildConfig.BASE_REDDIT_URL;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(interceptor).build();

        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RedditAPI redditAPI = retrofit.create(RedditAPI.class);

        Call<Listing> call  = redditAPI.getPostsFromSubreddit(section);
        call.enqueue(new Callback<Listing>() {
            @Override
            public void onResponse(Response<Listing> response) {
                listing = response.body();
                childrenEntities = listing.getData().getChildren();
                mainAdapter.swapList(childrenEntities);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void itemClicked(View view, int position) {

    }
}
