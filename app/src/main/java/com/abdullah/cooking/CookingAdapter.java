package com.abdullah.cooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class CookingAdapter extends BaseAdapter {

    private List<Cooking> CookingList;
    private Context context;

    public CookingAdapter(List<Cooking> mainList, Context applicationContext) {
        this.CookingList = mainList;
        this.context = applicationContext;
    }

    @Override
    public int getCount() {
        return CookingList.size();
    }

    @Override
    public Object getItem(int position) {
        return CookingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView != null) {
            mViewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.cooking_layout, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            convertView.setTag(mViewHolder);
        }

        Cooking cooking = (Cooking) getItem(position);
        mViewHolder.nameTextView.setText(cooking.getName());
        return convertView;
    }

    static class ViewHolder {
        private TextView nameTextView;
    }
}
