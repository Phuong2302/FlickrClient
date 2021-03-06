package com.ikota.flickrclient.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikota.flickrclient.R;
import com.ikota.flickrclient.data.model.PeopleInfo;

import java.util.List;

/**
 * Created by kota on 2015/08/11.
 * This adapter is used in ImageListFragment
 */
public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private Context mContext;
    private List<PeopleInfo> mDataSet;
    private final OnClickCallback mClickCallback;
    private final LayoutInflater mInflater;

    public interface OnClickCallback {
        void onFlickrClicked(View view);
        void onLocationClicked(View view);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View flickr_view;
        public TextView description, location;

        public ViewHolder(View v) {
            super(v);
            flickr_view = v.findViewById(R.id.flickr_info_parent);
            description = (TextView)v.findViewById(R.id.description);
            location = (TextView)v.findViewById(R.id.location);
        }
    }

    public AboutAdapter(Context context, List<PeopleInfo> myDataset, OnClickCallback listener) {
        mContext = context;
        mDataSet = myDataset;
        mClickCallback = listener;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mInflater.inflate(R.layout.row_user_about, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Resources r = mContext.getResources();
        holder.location.setText("Iowa City, USA");
        holder.description.setText(r.getString(R.string.description));
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickCallback!=null) mClickCallback.onLocationClicked(view);
            }
        });
        holder.flickr_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickCallback!=null) mClickCallback.onFlickrClicked(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


}
