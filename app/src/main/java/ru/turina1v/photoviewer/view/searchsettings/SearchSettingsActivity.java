package ru.turina1v.photoviewer.view.searchsettings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import moxy.MvpAppCompatActivity;
import ru.turina1v.photoviewer.App;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.Category;
import ru.turina1v.photoviewer.model.JsonUtils;
import ru.turina1v.photoviewer.model.PhotoPreferences;
import ru.turina1v.photoviewer.view.searchsettings.CategoryAdapter;

import static ru.turina1v.photoviewer.model.PhotoPreferences.ORIENTATION_ALL;
import static ru.turina1v.photoviewer.model.PhotoPreferences.ORIENTATION_HORIZONTAL;
import static ru.turina1v.photoviewer.model.PhotoPreferences.ORIENTATION_VERTICAL;

public class SearchSettingsActivity extends MvpAppCompatActivity {
    @Inject
    PhotoPreferences photoPreferences;

    @BindView(R.id.orientation_vertical_radiobutton)
    RadioButton orientationVerticalBtn;
    @BindView(R.id.orientation_horizontal_radiobutton)
    RadioButton orientationHorizontalBtn;
    @BindView(R.id.orientation_all_radiobutton)
    RadioButton orientationAllBtn;
    @BindView(R.id.spinner_category)
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_settings);
        App.getComponent().inject(this);
        ButterKnife.bind(this);
        initToolbar();
        setCheckedOrientation();
        initCategorySpinner();
    }

    @OnCheckedChanged({R.id.orientation_vertical_radiobutton, R.id.orientation_horizontal_radiobutton, R.id.orientation_all_radiobutton})
    public void onOrientationCheckedChanged(CompoundButton button, boolean checked) {
        if (checked) {
            switch (button.getId()) {
                case R.id.orientation_vertical_radiobutton:
                    photoPreferences.saveOrientation(ORIENTATION_VERTICAL);
                    break;
                case R.id.orientation_horizontal_radiobutton:
                    photoPreferences.saveOrientation(ORIENTATION_HORIZONTAL);
                    break;
                case R.id.orientation_all_radiobutton:
                    photoPreferences.saveOrientation(ORIENTATION_ALL);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void setCheckedOrientation() {
        String orientation = photoPreferences.getOrientation();
        if (orientation == null) {
            orientationAllBtn.setChecked(true);
            return;
        }
        switch (orientation) {
            case (ORIENTATION_VERTICAL):
                orientationVerticalBtn.setChecked(true);
                break;
            case (ORIENTATION_HORIZONTAL):
                orientationHorizontalBtn.setChecked(true);
                break;
            case (ORIENTATION_ALL):
                orientationAllBtn.setChecked(true);
                break;
        }
    }

    private void initCategorySpinner() {
        Gson gson = new Gson();
        String json = JsonUtils.loadJSONFromAsset(this, "categories.json");
        Type listType = new TypeToken<List<Category>>() {
        }.getType();
        List<Category> categories = gson.fromJson(json, listType);
        CategoryAdapter adapter = new CategoryAdapter(this, R.layout.item_spinner_category, categories);
        spinner.setAdapter(adapter);
        spinner.setSelection(photoPreferences.getCategoryIndex());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = adapter.getItem(position);
                photoPreferences.saveCategory(category.getQuery());
                photoPreferences.saveCategoryIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(Activity.RESULT_OK);
        finish();
        return true;
    }
}
