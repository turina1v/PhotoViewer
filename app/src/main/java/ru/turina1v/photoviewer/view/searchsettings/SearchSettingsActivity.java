package ru.turina1v.photoviewer.view.searchsettings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suke.widget.SwitchButton;

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

import static ru.turina1v.photoviewer.model.PhotoPreferences.ORIENTATION_ALL;
import static ru.turina1v.photoviewer.model.PhotoPreferences.ORIENTATION_HORIZONTAL;
import static ru.turina1v.photoviewer.model.PhotoPreferences.ORIENTATION_VERTICAL;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_BLACK;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_BLUE_DARK;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_BLUE_LIGHT;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_BROWN;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_GRAY;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_GRAYSCALE;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_GREEN;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_ORANGE;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_PINK;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_PURPLE;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_RED;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_TRANSPARENT;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_WHITE;
import static ru.turina1v.photoviewer.model.PhotoPreferences.COLOR_YELLOW;

public class SearchSettingsActivity extends MvpAppCompatActivity implements SwitchButton.OnCheckedChangeListener {
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
    @BindView(R.id.checkbox_transparent)
    CheckBox transparentCheckbox;
    @BindView(R.id.checkbox_grayscale)
    CheckBox grayscaleCheckbox;
    @BindView(R.id.switch_red)
    SwitchButton switchRed;
    @BindView(R.id.switch_orange)
    SwitchButton switchOrange;
    @BindView(R.id.switch_yellow)
    SwitchButton switchYellow;
    @BindView(R.id.switch_green)
    SwitchButton switchGreen;
    @BindView(R.id.switch_blue_light)
    SwitchButton switchBlueLight;
    @BindView(R.id.switch_blue_dark)
    SwitchButton switchBlueDark;
    @BindView(R.id.switch_purple)
    SwitchButton switchPurple;
    @BindView(R.id.switch_pink)
    SwitchButton switchPink;
    @BindView(R.id.switch_white)
    SwitchButton switchWhite;
    @BindView(R.id.switch_gray)
    SwitchButton switchGray;
    @BindView(R.id.switch_black)
    SwitchButton switchBlack;
    @BindView(R.id.switch_brown)
    SwitchButton switchBrown;
    @BindView(R.id.color_blur_layout)
    LinearLayout colorBlurLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_settings);
        App.getComponent().inject(this);
        ButterKnife.bind(this);
        initToolbar();
        initColorSwitch();
        setCheckedOrientation();
        initCategorySpinner();
        transparentCheckbox.setChecked(photoPreferences.isColor(COLOR_TRANSPARENT));
        grayscaleCheckbox.setChecked(photoPreferences.isColor(COLOR_GRAYSCALE));
        setCheckedColorSwitch();
    }

    @OnCheckedChanged({R.id.orientation_vertical_radiobutton, R.id.orientation_horizontal_radiobutton,
            R.id.orientation_all_radiobutton})
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

    @OnCheckedChanged({R.id.checkbox_transparent, R.id.checkbox_grayscale})
    public void onColorCheckboxChanged(CompoundButton button, boolean checked){
        if (checked){
            switch (button.getId()){
                case R.id.checkbox_transparent:
                    photoPreferences.saveColor(COLOR_TRANSPARENT, true);
                    break;
                case R.id.checkbox_grayscale:
                    photoPreferences.clearSwitchColors();
                    photoPreferences.saveColor(COLOR_GRAYSCALE, true);
                    setCheckedColorSwitch();
                    colorBlurLayout.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (button.getId()){
                case R.id.checkbox_transparent:
                    photoPreferences.saveColor(COLOR_TRANSPARENT, false);
                    break;
                case R.id.checkbox_grayscale:
                    photoPreferences.saveColor(COLOR_GRAYSCALE, false);
                    colorBlurLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()){
            case R.id.switch_red:
                photoPreferences.saveColor(COLOR_RED, isChecked);
                break;
            case R.id.switch_orange:
                photoPreferences.saveColor(COLOR_ORANGE, isChecked);
                break;
            case R.id.switch_yellow:
                photoPreferences.saveColor(COLOR_YELLOW, isChecked);
                break;
            case R.id.switch_green:
                photoPreferences.saveColor(COLOR_GREEN, isChecked);
                break;
            case R.id.switch_blue_light:
                photoPreferences.saveColor(COLOR_BLUE_LIGHT, isChecked);
                break;
            case R.id.switch_blue_dark:
                photoPreferences.saveColor(COLOR_BLUE_DARK, isChecked);
                break;
            case R.id.switch_purple:
                photoPreferences.saveColor(COLOR_PURPLE, isChecked);
                break;
            case R.id.switch_pink:
                photoPreferences.saveColor(COLOR_PINK, isChecked);
                break;
            case R.id.switch_white:
                photoPreferences.saveColor(COLOR_WHITE, isChecked);
                break;
            case R.id.switch_gray:
                photoPreferences.saveColor(COLOR_GRAY, isChecked);
                break;
            case R.id.switch_black:
                photoPreferences.saveColor(COLOR_BLACK, isChecked);
                break;
            case R.id.switch_brown:
                photoPreferences.saveColor(COLOR_BROWN, isChecked);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(Activity.RESULT_OK);
        finish();
        return true;
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

    private void setCheckedColorSwitch(){
        switchRed.setChecked(photoPreferences.isColor(COLOR_RED));
        switchOrange.setChecked(photoPreferences.isColor(COLOR_ORANGE));
        switchYellow.setChecked(photoPreferences.isColor(COLOR_YELLOW));
        switchGreen.setChecked(photoPreferences.isColor(COLOR_GREEN));
        switchBlueLight.setChecked(photoPreferences.isColor(COLOR_BLUE_LIGHT));
        switchBlueDark.setChecked(photoPreferences.isColor(COLOR_BLUE_DARK));
        switchPurple.setChecked(photoPreferences.isColor(COLOR_PURPLE));
        switchPink.setChecked(photoPreferences.isColor(COLOR_PINK));
        switchWhite.setChecked(photoPreferences.isColor(COLOR_WHITE));
        switchGray.setChecked(photoPreferences.isColor(COLOR_GRAY));
        switchBlack.setChecked(photoPreferences.isColor(COLOR_BLACK));
        switchBrown.setChecked(photoPreferences.isColor(COLOR_BROWN));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initColorSwitch(){
        switchRed.setOnCheckedChangeListener(this);
        switchOrange.setOnCheckedChangeListener(this);
        switchYellow.setOnCheckedChangeListener(this);
        switchGreen.setOnCheckedChangeListener(this);
        switchBlueLight.setOnCheckedChangeListener(this);
        switchBlueDark.setOnCheckedChangeListener(this);
        switchPurple.setOnCheckedChangeListener(this);
        switchPink.setOnCheckedChangeListener(this);
        switchWhite.setOnCheckedChangeListener(this);
        switchGray.setOnCheckedChangeListener(this);
        switchBlack.setOnCheckedChangeListener(this);
        switchBrown.setOnCheckedChangeListener(this);
    }




}
