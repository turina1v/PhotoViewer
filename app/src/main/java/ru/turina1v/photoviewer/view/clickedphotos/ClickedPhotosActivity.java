package ru.turina1v.photoviewer.view.clickedphotos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.presenter.ClickedPhotosPresenter;
import ru.turina1v.photoviewer.view.photodetail.PhotoDetailActivity;
import ru.turina1v.photoviewer.view.photolist.GridItemDecoration;
import ru.turina1v.photoviewer.view.photolist.OnPhotoClickListener;
import ru.turina1v.photoviewer.view.photolist.PhotoListAdapter;

public class ClickedPhotosActivity extends MvpAppCompatActivity implements ClickedPhotosView, OnPhotoClickListener {
    @InjectPresenter
    ClickedPhotosPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.text_load_info)
    TextView loadInfoText;
    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    PhotoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        ButterKnife.bind(this);
        initToolbar();
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.getPhotosFromDB());
        loadInfoText.setText(R.string.load_info_images_loading);
        initPhotoRecycler();
        //presenter.clearAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getPhotosFromDB();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
    }

    @Override
    public void updatePhotoRecycler(List<Hit> photos) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        if (photos.size() == 0) {
            loadInfoText.setText(R.string.load_info_no_clicked_photos);
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
    public void openDetailPhoto(Hit photo) {
        Intent intent = new Intent(ClickedPhotosActivity.this, PhotoDetailActivity.class);
        intent.putExtra(PhotoDetailActivity.EXTRA_PHOTO, photo);
        intent.putExtra(PhotoDetailActivity.EXTRA_IS_SET_EXPIRED, false);
        startActivity(intent);
    }

    @Override
    public void onPhotoClick(Hit photo) {
        openDetailPhoto(photo);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.toolbar_clicked_photos);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}
