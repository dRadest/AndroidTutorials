package com.example.android.draddest.mdcexercise;

import android.content.ClipData;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.N;

public class MainActivity extends AppCompatActivity {

    // instance variable for the drawer layout
    private DrawerLayout mDrawerLayout;

    // number of items to display in our list
    private static final int NUMBER_OF_ITEMS = 56;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initializing the recycler view and setting the adapter on it
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter adapter = new ItemAdapter(NUMBER_OF_ITEMS);
        recyclerView.setAdapter(adapter);

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);
                        // handle navigation
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.first_drawer_item:
                                Toast.makeText(MainActivity.this, "Sit back and relax", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.second_drawer_item:
                                Toast.makeText(MainActivity.this, "Hasta la victoria siempre", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.third_drawer_item:
                                Toast.makeText(MainActivity.this, "IT rocks", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    // simple view holder for the recycler view
    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView itemIdView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemIdView = (TextView) itemView.findViewById(R.id.item_id);
        }

        public void bind(int indexPosition){
            itemIdView.setText(String.valueOf(indexPosition));
        }
    }

    // adapter to populate the views
    public static class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        private int mNumberOfItems;

        public ItemAdapter(int numberOfItems){
            mNumberOfItems = numberOfItems;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.list_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            ItemViewHolder viewHolder = new ItemViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mNumberOfItems;
        }
    }
}
