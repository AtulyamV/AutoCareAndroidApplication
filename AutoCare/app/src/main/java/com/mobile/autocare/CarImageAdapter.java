package com.mobile.autocare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CarImageAdapter extends BaseAdapter {
    private Context mContext;
    public Bitmap[] mBitmaps;
    public int getCount() {
        return mBitmaps.length;
    }
    public Object getItem(int position) {
        return mBitmaps[position];
    }
    public long getItemId(int position) {
        return 0;
    }
    public CarImageAdapter(Context c) {
        mContext = c;
        mBitmaps = getInitialBitmaps();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else{
            imageView = (ImageView) convertView;
        }


        imageView.setImageBitmap(mBitmaps[position]);
        return imageView;
    }

    public Bitmap[] getInitialBitmaps() {
        Bitmap[] bitmaps = new Bitmap[9];
        bitmaps[0] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.chevrolet);
        bitmaps[1] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fiat);
        bitmaps[2] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ford);
        bitmaps[3] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.honda);
        bitmaps[4] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hyundai);
        bitmaps[5] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marutisuzuki);
        bitmaps[6] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tatamotors);
        bitmaps[7] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.toyota);
        bitmaps[8] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.volkswagen);

        return bitmaps;
    }
}
