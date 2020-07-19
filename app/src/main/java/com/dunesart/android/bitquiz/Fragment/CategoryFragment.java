package com.dunesart.android.bitquiz.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.dunesart.android.bitquiz.Common;
import com.dunesart.android.bitquiz.ItemClickListener;
import com.dunesart.android.bitquiz.Model.Category;
import com.dunesart.android.bitquiz.R;
import com.dunesart.android.bitquiz.Start;
import com.dunesart.android.bitquiz.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    RecyclerView listCategory;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    DatabaseReference categories;
    DatabaseReference users;
    View view;
    String Tag = CategoryFragment.class.getSimpleName();

    Toolbar toolbar;
    TextView textView;

    public static CategoryFragment newInstance() {
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = FirebaseDatabase.getInstance().getReference("Category");
        categories.keepSynced(true);

    }

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_category, container, false);

//        toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setTitle(R.string.app_name);
//        view.setActionBar(toolbar)

//        textView = view.findViewById(R.id.score);
//        textView.setText(Common.currentUser.getScore());

        listCategory = view.findViewById(R.id.category_recyclerView);
        layoutManager = new LinearLayoutManager(container.getContext());
        listCategory.setLayoutManager(layoutManager);
        listCategory.setHasFixedSize(true);
        loadCategories();
        listCategory.setAdapter(adapter);
        return view;
    }

    private void loadCategories() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_layout,
                CategoryViewHolder.class,
                categories

        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                if (model == null)
                    Log.d("aaaaaaaa", "populateViewHolder: empty");
                viewHolder.category_name.setText(model.getName());
                Picasso.with(getActivity()).load(model.getImage()).into(viewHolder.category_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongPressed) {
                        Intent intent = new Intent(getActivity(), Start.class);
                        Common.CategoryId = adapter.getRef(position).getKey();
                        Common.CategoryName = model.getName();
                        Log.e(Tag, "Category Id : " + Common.CategoryId);
                        Log.e(Tag, "Category Name : " + model.getName());
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        };
    }

}
