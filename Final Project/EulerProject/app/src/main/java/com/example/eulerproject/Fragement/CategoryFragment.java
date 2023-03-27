package com.example.eulerproject.Fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toolbar;

import com.example.eulerproject.Adapters.CategoryAdapter;
import com.example.eulerproject.DbQuery;
import com.example.eulerproject.R;

public class CategoryFragment extends Fragment {

    public CategoryFragment(){

    }

    private GridView catView;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);


        catView = view.findViewById(R.id.cat_Grid);

        //loadCategories();

        CategoryAdapter adapter = new CategoryAdapter(DbQuery.g_catList);
        catView.setAdapter(adapter);

        return view;
    }

}