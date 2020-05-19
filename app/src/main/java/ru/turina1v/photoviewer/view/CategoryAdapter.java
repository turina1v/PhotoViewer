package ru.turina1v.photoviewer.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private Context context;
    private int layoutId;

    public CategoryAdapter(@NonNull Context context, int layoutId, @NonNull List<Category> objects) {
        super(context, android.R.layout.simple_list_item_2, objects);
        this.context = context;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Category category = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_category, null);
        }
        ((TextView)convertView.findViewById(R.id.spinner_item_text)).setText(category.getName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Category category = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_category, null);
        }
        ((TextView)convertView.findViewById(R.id.spinner_item_text)).setText(category.getName());
        return convertView;
    }
}
