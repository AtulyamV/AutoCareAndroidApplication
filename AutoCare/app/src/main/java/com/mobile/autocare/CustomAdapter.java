package com.mobile.autocare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{
    String [] title;
    String [] subTitle;
    Context context;
    int [] homeImage;
    private static LayoutInflater inflater=null;
    public CustomAdapter(HomeActivity homeActivity, String[] title, String[] subTitle,int[] homeImage) {
        this.title = title;
        this.subTitle = subTitle;
        context = homeActivity;
        this.homeImage = homeImage;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView title;
        TextView subTitle;
        ImageView homeImage;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.program_list, null);
        holder.title=(TextView) rowView.findViewById(R.id.textView1);
        holder.subTitle = (TextView) rowView.findViewById(R.id.textView2);
        holder.homeImage=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.title.setText(title[position]);
        holder.subTitle.setText(subTitle[position]);
        holder.homeImage.setImageResource(homeImage[position]);
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemValue = title[position];

                if (itemValue.equals("QUOTE")) {
                    Intent intent = new Intent(context, QuoteActivity.class);
                    context.startActivity(intent);

                } else {
                    Intent intent = new Intent(context, PhotoUploadActivity.class);
                    intent.putExtra("selected", itemValue);
                    context.startActivity(intent);
                }
            }
        });
        return rowView;
    }

}
