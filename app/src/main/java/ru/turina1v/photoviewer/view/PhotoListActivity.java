package ru.turina1v.photoviewer.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.App;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.PhotoPreferences;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.presenter.PhotoListPresenter;

import static ru.turina1v.photoviewer.model.PhotoPreferences.CATEGORY_ALL;
import static ru.turina1v.photoviewer.model.PhotoPreferences.DEFAULT_ORIENTATION;
import static ru.turina1v.photoviewer.model.PhotoPreferences.DEFAULT_QUERY;

@SuppressWarnings({"FieldCanBeLocal"})
public class PhotoListActivity extends MvpAppCompatActivity implements PhotoListView, OnPhotoClickListener, SearchView.OnQueryTextListener {
    private final int requestCodeSettings = 111;
    @InjectPresenter
    PhotoListPresenter presenter;
    @Inject
    PhotoPreferences photoPreferences;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    MenuItem searchViewItem;

    PhotoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        App.getComponent().inject(this);
        ButterKnife.bind(this);

        initToolbar();
        boolean isLoadFromDB = photoPreferences.getIsLoadFromDb();
        if (!isLoadFromDB) {
            presenter.downloadPhotoList(DEFAULT_QUERY, DEFAULT_ORIENTATION, null);
        } else {
            long updateTimestamp = photoPreferences.getUpdateTimestamp();
            if (presenter.isUpdateNeeded(updateTimestamp)) {
                presenter.downloadPhotoList(photoPreferences.getQuery(), photoPreferences.getOrientation(), photoPreferences.getCategory());
            } else {
                presenter.getPhotosFromDB();
            }
        }
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestCodeSettings && resultCode == Activity.RESULT_OK){
            if (!photoPreferences.getCategory().equals(CATEGORY_ALL)){
                photoPreferences.saveQuery("");
            }
            presenter.updatePhotoList(photoPreferences.getQuery(),
                    photoPreferences.getOrientation(), photoPreferences.getCategory());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void initPhotoRecycler(List<Hit> photos) {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoListAdapter(photos);
        adapter.setOnPictureClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridItemDecoration(2, 16));
    }

    @Override
    public void updatePhotoRecycler(List<Hit> photos) {
        adapter.setPhotosList(photos);
    }

    @Override
    public void saveQueryPreference(String query) {
        photoPreferences.saveQuery(query);
    }

    @Override
    public void onPhotoClick(int position, String photoUrl) {
        openDetailPhoto(position, photoUrl);
    }

    @Override
    public void openDetailPhoto(int position, String photoUrl) {
        Intent intent = new Intent(PhotoListActivity.this, PhotoDetailActivity.class);
        intent.putExtra(PhotoDetailActivity.EXTRA_POSITION, position);
        intent.putExtra(PhotoDetailActivity.EXTRA_PHOTO_URL, photoUrl);
        startActivity(intent);
    }

    @Override
    public void openSearchSettings(){
        Intent intent = new Intent(PhotoListActivity.this, SearchSettingsActivity.class);
        startActivityForResult(intent, requestCodeSettings);
    }

    @Override
    public void saveLoadFromDB() {
        photoPreferences.saveIsLoadFromDb(true);
        photoPreferences.saveUpdateTimestamp(presenter.getUpdateTimestamp(System.currentTimeMillis()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        searchViewItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(this);
        MenuItem filterItem = menu.findItem(R.id.menu_filter);
        filterItem.setOnMenuItemClickListener(item -> {
            openSearchSettings();
            return false;
        });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        presenter.updatePhotoList(query, photoPreferences.getOrientation(), photoPreferences.getCategory());
        return false;
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
