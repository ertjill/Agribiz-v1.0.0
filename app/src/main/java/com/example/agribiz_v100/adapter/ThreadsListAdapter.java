package com.example.agribiz_v100.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ChatThreadUserModel;

import java.util.List;

public class ThreadsListAdapter extends BaseAdapter {

    Context context;
    List<ChatThreadUserModel> usersList;

    public ThreadsListAdapter(Context context, List<ChatThreadUserModel> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.chat_threads_list_item_layout,null);

        ImageView user_image_iv = convertView.findViewById(R.id.user_image_iv);
        TextView username_tv=convertView.findViewById(R.id.username_tv);

        Glide.with(context)
                .load(usersList.get(position).getUserImage())
                .into(user_image_iv);
        username_tv.setText(usersList.get(position).getUserDisplayName().substring(0,usersList.get(position).getUserDisplayName().length()-2));

        return convertView;
    }
}
