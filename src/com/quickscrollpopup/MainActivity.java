package com.quickscrollpopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }
        QSExpendableListView listView;
		private ArrayList<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		private ExpandableListAdapter listAdapter;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            listView = (QSExpendableListView) rootView.findViewById(R.id.lvExp);
            prepareListData();
            listAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
            listView.setAdapter(listAdapter);
            listView.setQuickScrollEnable(true);
            return rootView;
        }/*
         * Preparing the list data
         */
        private void prepareListData() {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
     
            // Adding child data
            listDataHeader.add("Top 250");
            listDataHeader.add("Now Showing");
            listDataHeader.add("Coming Soon..");
     
            // Adding child data
            List<String> top250 = new ArrayList<String>();
            top250.add("The Shawshank Redemption");
            top250.add("The Godfather");
            top250.add("The Godfather: Part II");
            top250.add("Pulp Fiction");
            top250.add("The Good, the Bad and the Ugly");
            top250.add("The Dark Knight");
            top250.add("12 Angry Men");
            top250.add("The Shawshank Redemption");
            top250.add("The Godfather");
            top250.add("The Godfather: Part II");
            top250.add("Pulp Fiction");
            top250.add("The Good, the Bad and the Ugly");
            top250.add("The Dark Knight");
            top250.add("12 Angry Men");
     
            List<String> nowShowing = new ArrayList<String>();
            nowShowing.add("The Conjuring");
            nowShowing.add("Despicable Me 2");
            nowShowing.add("Turbo");
            nowShowing.add("Grown Ups 2");
            nowShowing.add("Red 2");
            nowShowing.add("The Wolverine");
            nowShowing.add("The Conjuring");
            nowShowing.add("Despicable Me 2");
            nowShowing.add("Turbo");
            nowShowing.add("Grown Ups 2");
            nowShowing.add("Red 2");
            nowShowing.add("The Wolverine");
            
            List<String> comingSoon = new ArrayList<String>();
            comingSoon.add("2 Guns");
            comingSoon.add("The Smurfs 2");
            comingSoon.add("The Spectacular Now");
            comingSoon.add("The Canyons");
            comingSoon.add("Europa Report");
            comingSoon.add("2 Guns");
            comingSoon.add("The Smurfs 2");
            comingSoon.add("The Spectacular Now");
            comingSoon.add("The Canyons");
            comingSoon.add("Europa Report");
            
            listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
            listDataChild.put(listDataHeader.get(1), nowShowing);
            listDataChild.put(listDataHeader.get(2), comingSoon);
        }
        
        
        
        
        public class ExpandableListAdapter extends BaseExpandableListAdapter {
        	 
            private Context _context;
            private List<String> _listDataHeader; // header titles
            // child data in format of header title, child title
            private HashMap<String, List<String>> _listDataChild;
         
            public ExpandableListAdapter(Context placeholderFragment, List<String> listDataHeader,
                    HashMap<String, List<String>> listChildData) {
                this._context = placeholderFragment;
                this._listDataHeader = listDataHeader;
                this._listDataChild = listChildData;
            }
         
            @Override
            public Object getChild(int groupPosition, int childPosititon) {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .get(childPosititon);
            }
         
            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }
         
            @Override
            public View getChildView(int groupPosition, final int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
         
                final String childText = (String) getChild(groupPosition, childPosition);
         
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item, null);
                }
         
                TextView txtListChild = (TextView) convertView
                        .findViewById(R.id.lblListItem);
         
                txtListChild.setText(childText);
                return convertView;
            }
         
            @Override
            public int getChildrenCount(int groupPosition) {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .size();
            }
         
            @Override
            public Object getGroup(int groupPosition) {
                return this._listDataHeader.get(groupPosition);
            }
         
            @Override
            public int getGroupCount() {
                return this._listDataHeader.size();
            }
         
            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }
         
            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                String headerTitle = (String) getGroup(groupPosition);
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_group, null);
                }
         
                TextView lblListHeader = (TextView) convertView
                        .findViewById(R.id.lblListHeader);
                lblListHeader.setTypeface(null, Typeface.BOLD);
                lblListHeader.setText(headerTitle);
                ExpandableListView eLV = (ExpandableListView) parent;
                eLV.expandGroup(groupPosition);
                return convertView;
            }
         
            @Override
            public boolean hasStableIds() {
                return false;
            }
         
            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        }
    }

}
