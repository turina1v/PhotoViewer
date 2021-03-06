package ru.turina1v.photoviewer.view.photolist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.App;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.AnalyticsUtils;
import ru.turina1v.photoviewer.model.PhotoPreferences;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.presenter.PhotoListPresenter;
import ru.turina1v.photoviewer.view.AboutAppActivity;
import ru.turina1v.photoviewer.view.clickedphotos.ClickedPhotosActivity;
import ru.turina1v.photoviewer.view.photodetail.PhotoDetailActivity;
import ru.turina1v.photoviewer.view.searchsettings.SearchSettingsActivity;

public class PhotoListActivity extends MvpAppCompatActivity implements PhotoListView, OnPhotoClickListener {
    private final int requestCodeSettings = 111;

    @InjectPresenter
    PhotoListPresenter presenter;
    @Inject
    PhotoPreferences photoPreferences;

    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.text_load_info)
    TextView loadInfoText;

    private PhotoListAdapter adapter;
    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        App.getComponent().inject(this);
        ButterKnife.bind(this);
        photoPreferences.clearAll();
        initToolbar();
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.downloadPhotoList(photoPreferences.getQuery(), photoPreferences.getOrientation(), photoPreferences.getCategory(), photoPreferences.getColorQuery(),
                photoPreferences.getEditorsChoiceQuery(), photoPreferences.getOrder(), photoPreferences.getSafeSearchQuery()));
        loadInfoText.setTextColor(Color.BLACK);
        loadInfoText.setText(R.string.load_info_images_loading);
        initPhotoRecycler();
        presenter.downloadPhotoList(null, null, null, null,
                null, null, photoPreferences.getSafeSearchQuery());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestCodeSettings && resultCode == Activity.RESULT_OK) {
            adapter.clearPhotosList();
            presenter.resetPage();
            presenter.downloadPhotoList(photoPreferences.getQuery(), photoPreferences.getOrientation(),
                    photoPreferences.getCategory(), photoPreferences.getColorQuery(),
                    photoPreferences.getEditorsChoiceQuery(), photoPreferences.getOrder(), photoPreferences.getSafeSearchQuery());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_main, menu);

        MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.clearPhotosList();
                photoPreferences.saveQuery(query);
                presenter.resetPage();
                presenter.downloadPhotoList(query, photoPreferences.getOrientation(), photoPreferences.getCategory(),
                        photoPreferences.getColorQuery(), photoPreferences.getEditorsChoiceQuery(), photoPreferences.getOrder(), photoPreferences.getSafeSearchQuery());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        EditText et = searchView.findViewById(R.id.search_src_text);
        closeButton.setOnClickListener(v -> {
            String query = et.getText().toString();
            if ("".equals(query)) {
                searchView.onActionViewCollapsed();
                searchViewItem.collapseActionView();
                if (photoPreferences.getQuery() != null) {
                    photoPreferences.clearQuery();
                    presenter.downloadPhotoList(null, photoPreferences.getOrientation(),
                            photoPreferences.getCategory(), photoPreferences.getColorQuery(),
                            photoPreferences.getEditorsChoiceQuery(), photoPreferences.getOrder(), photoPreferences.getSafeSearchQuery());
                }
            } else {
                et.setText("");
            }
        });

        MenuItem filterItem = menu.findItem(R.id.menu_filter);
        filterItem.setOnMenuItemClickListener(item -> {
            openSearchSettings();
            return false;
        });

        MenuItem lastClickedItem = menu.findItem(R.id.menu_last_clicked);
        lastClickedItem.setOnMenuItemClickListener(item -> {
            openLastClicked();
            return false;
        });

        MenuItem infoItem = menu.findItem(R.id.menu_info);
        infoItem.setOnMenuItemClickListener(item -> {
            openAppInfo();
            return false;
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            finish();
        } else {
            isBackPressed = true;
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    isBackPressed = false;
                }
            }, 3000);
        }
    }

    @Override
    public void initPhotoRecycler() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoListAdapter();
        adapter.setOnPictureClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridItemDecoration(2, 16));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    presenter.appendPhotoList(photoPreferences.getQuery(), photoPreferences.getOrientation(),
                            photoPreferences.getCategory(), photoPreferences.getColorQuery(),
                            photoPreferences.getEditorsChoiceQuery(), photoPreferences.getOrder(), photoPreferences.getSafeSearchQuery());
                }
            }
        });
    }

    @Override
    public void updatePhotoRecycler(List<Hit> photos) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        if (photos.size() == 0) {
            loadInfoText.setText(R.string.load_info_nothing_found);
        } else {
            loadInfoText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setPhotosList(photos);
        }
    }

    @Override
    public void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        loadInfoText.setVisibility(View.VISIBLE);
        loadInfoText.setText(R.string.load_info_images_loading);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorScreen(int stringId) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        loadInfoText.setVisibility(View.VISIBLE);
        loadInfoText.setText(stringId);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorToast(int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void appendPhotoRecycler(List<Hit> photos) {
        adapter.addToPhotosList(photos);
    }

    @Override
    public void onPhotoClick(Hit photo) {
        openDetailPhoto(photo);
    }

    @Override
    public void onCommercialClick(String url) {
        Uri uri = Uri.parse(url);
        Intent browser = new Intent(Intent.ACTION_VIEW, uri);
        App.getAnalytics().logEvent(AnalyticsUtils.EVENT_CLICK_PIXABAY_LINK_MAIN);
        startActivity(browser);
    }

    @Override
    public void openDetailPhoto(Hit photo) {
        Intent intent = new Intent(PhotoListActivity.this, PhotoDetailActivity.class);
        intent.putExtra(PhotoDetailActivity.EXTRA_PHOTO, photo);
        intent.putExtra(PhotoDetailActivity.EXTRA_IS_SET_EXPIRED, true);
        startActivity(intent);
    }

    @Override
    public void openSearchSettings() {
        startActivityForResult(new Intent(PhotoListActivity.this, SearchSettingsActivity.class), requestCodeSettings);
    }

    @Override
    public void openLastClicked() {
        startActivity(new Intent(PhotoListActivity.this, ClickedPhotosActivity.class));
    }

    @Override
    public void openAppInfo() {
        startActivity(new Intent(PhotoListActivity.this, AboutAppActivity.class));
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
