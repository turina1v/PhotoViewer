package ru.turina1v.photoviewer.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.presenter.PhotoListPresenter;

@SuppressWarnings({"FieldCanBeLocal"})
public class PhotoListActivity extends MvpAppCompatActivity implements PhotoListView {
    @InjectPresenter
    PhotoListPresenter presenter;

    SharedPreferences preferences;
    PhotoListAdapter adapter;
    private final String PREFERENCES_USER = "preferences_user";
    private final String PREFERENCES_LOAD_FROM_DB = "preferences_load_from_db";
    private final String PREFERENCES_UPDATE_TIMESTAMP = "preferences_update_timestamp";
    private final String PREFERENCES_QUERY = "preferences_query";
    private final String DEFAULT_QUERY = "cats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        initToolbar();
        preferences = getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
        boolean isLoadFromDB = preferences.getBoolean(PREFERENCES_LOAD_FROM_DB, false);
        if (!isLoadFromDB) {
            presenter.downloadPhotosList(DEFAULT_QUERY);
        } else {
            long updateTimestamp = preferences.getLong(PREFERENCES_UPDATE_TIMESTAMP, 0);
            if (presenter.isUpdateNeeded(updateTimestamp)) {
                presenter.downloadPhotosList(preferences.getString(PREFERENCES_QUERY, DEFAULT_QUERY));
            } else {
                presenter.getPhotosFromDB();
            }
        }
    }

    @Override
    public void initPhotoRecycler(List<Hit> photos) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoListAdapter(photos);
        adapter.setOnPictureClickListener(presenter);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridItemDecoration(2, 16));
    }

    @Override
    public void updatePhotoRecycler(List<Hit> photos) {
        adapter.setPhotosList(photos);
    }

    @Override
    public void saveQueryPreference(String query) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_QUERY, query);
        editor.apply();
    }

    @Override
    public void openDetailPhoto(int position, String photoUrl) {
        Intent intent = new Intent(PhotoListActivity.this, PhotoDetailActivity.class);
        intent.putExtra(PhotoDetailActivity.EXTRA_POSITION, position);
        intent.putExtra(PhotoDetailActivity.EXTRA_PHOTO_URL, photoUrl);
        startActivity(intent);
    }

    @Override
    public void saveLoadFromDB() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_LOAD_FROM_DB, true);
        editor.putLong(PREFERENCES_UPDATE_TIMESTAMP, presenter.getUpdateTimestamp(System.currentTimeMillis()));
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(presenter);
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }
}
