package com.kennyleong.flickrgroupsearch.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kennyleong.flickrgroupsearch.R;
import com.kennyleong.flickrgroupsearch.model.Group;

import java.util.List;

/**
 * Created by Kenny Leong on 10/15/2015.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<Group> groupList;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 0;

    public GroupListAdapter(List<Group> data) {
        groupList = data;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);

        }


    }

    public class ItemViewHolder extends GroupListAdapter.ViewHolder implements View.OnClickListener {

        public ItemViewHolder(View v) {
            super(v);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition()-1; // gets item position

        }
    }

    public class FooterViewHolder extends  GroupListAdapter.ViewHolder {

        public FooterViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(groupList.get(position) != null)
            return TYPE_ITEM;
        return TYPE_FOOTER;
    }


    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        GroupListAdapter.ViewHolder vh;

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_name, parent, false);
            vh = new ItemViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_footer, parent, false);
            vh = new FooterViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(GroupListAdapter.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {

            TextView row_title = (TextView) ((ItemViewHolder) holder).itemView.findViewById(R.id.group_name);
            row_title.setText(groupList.get(position).name);

        }

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void addAll(List<Group> data) {
        groupList.addAll(data);
        notifyDataSetChanged();
    }

    public void add(Group data) {
        groupList.add(data);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        groupList.remove(position);
        notifyDataSetChanged();
    }



}
