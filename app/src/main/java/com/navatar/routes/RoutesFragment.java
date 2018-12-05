package com.navatar.routes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.navatar.R;
import com.navatar.data.source.RouteData;

import java.util.List;

import dagger.android.support.DaggerFragment;

import static android.support.v4.util.Preconditions.checkNotNull;

public class RoutesFragment extends DaggerFragment implements RoutesContract.View {


    private static class RoutesAdapter extends BaseAdapter {

        private List<RouteData> mRoutes;

        public RoutesAdapter(List<RouteData> routes) {
            setList(routes);
        }

        public void replaceData(List<RouteData> routes) {
            setList(routes);
            notifyDataSetChanged();
        }

        private void setList(List<RouteData> routes) {
            mRoutes = checkNotNull(routes);
        }

        @Override
        public int getCount() {
            return mRoutes.size();
        }

        @Override
        public RouteData getItem(int i) {
            return mRoutes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                //rowView = inflater.inflate(R.layout.task_item, viewGroup, false);
            }

            final RouteData route = getItem(i);

            TextView titleTV = rowView.findViewById(R.id.title);
            titleTV.setText(route.getBuildingId());

            return rowView;
        }
    }

}
