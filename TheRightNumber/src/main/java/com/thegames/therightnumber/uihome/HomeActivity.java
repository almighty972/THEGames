package com.thegames.therightnumber.uihome;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.AbstractFragment;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.uigame.GameActivity;

public class HomeActivity extends AbstractActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HomeFragment extends AbstractFragment {

        private DrawerLayout mDrawerLayout;
        private RelativeLayout mLeftDrawer;
        private ListView mDrawerList;
        private ImageButton mPlayImageButton;
        private ImageButton mMenuImageButton;

        public HomeFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mPlayImageButton = (ImageButton) view.findViewById(R.id.playBtn);
            mPlayImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gameIntent = new Intent(getActivity(), GameActivity.class);
                    startActivity(gameIntent);
                }
            });

            mMenuImageButton = (ImageButton) view.findViewById(R.id.menuImageButton);
            mMenuImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleDrawer();
                }
            });

            mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
            mLeftDrawer = (RelativeLayout) view.findViewById(R.id.left_drawer);
            mDrawerList = (ListView) view.findViewById(R.id.menuListView);

            // Set the adapter for the list view
            MenuAdapter menuAdapter = new MenuAdapter(getActivity(),
                    R.layout.drawer_list_item, getResources().getStringArray(R.array.menu_entries));
            mDrawerList.setAdapter(menuAdapter);

            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        }

        /** Swaps fragments in the main content view */
        private void selectItem(int position) {
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mLeftDrawer);
        }

        private class DrawerItemClickListener implements ListView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }
        }

        private void toggleDrawer() {
            if (!mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
                mDrawerLayout.openDrawer(mLeftDrawer);
            } else {
                mDrawerLayout.closeDrawer(mLeftDrawer);
            }
        }

        /**
         * Menu Drawer adapter
         */
        private class MenuAdapter extends ArrayAdapter<String> {

            private String[] mItems;
            private Context mContext;
            private int mResourceId;

            public MenuAdapter(Context context, int resource, String[] objects) {
                super(context, resource, objects);
                mItems = objects;
                mContext = context;
                mResourceId = resource;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View row = convertView;
                ItemHolder holder = null;

                if(row == null) {
                    LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                    row = inflater.inflate(mResourceId, parent, false);

                    holder = new ItemHolder();
                    holder.entryName = (TextView)row.findViewById(R.id.menuEntryNameTextView);
                    if(position == 0) {
                        holder.entryName.setBackgroundResource(R.drawable.selector_menu_item_round_topright);
                    } else {
                        holder.entryName.setBackgroundResource(R.drawable.selector_menu_item);
                    }

                    row.setTag(holder);
                } else {
                    holder = (ItemHolder)row.getTag();
                }

                holder.entryName.setText(mItems[position]);

                return row;
            }
        }

        static class ItemHolder
        {
            TextView entryName;
        }

    }





}
