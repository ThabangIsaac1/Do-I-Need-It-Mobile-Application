package com.example.do_i_need_it;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class MyAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<MyModel> modelArrayList;

    public  MyAdapter(Context context, ArrayList<MyModel> modelArrayList){
        this.context = context;
        this.modelArrayList = modelArrayList;


    }

    @Override
    public int getCount() {
       return  modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_item,container,false);

        ImageView bannerIv = view.findViewById(R.id.bannerIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.description);
        //TextView dateTv = view.findViewById(R.id.dateTv);

        MyModel model = modelArrayList.get(position);
        String tittle = model.getTitle();
        String description = model.getDescription();
        String date = model.getDate();
        int image = model.getImage();

        bannerIv.setImageResource(image);
        titleTv.setText(tittle);
        descriptionTv.setText(description);
       // dateTv.setText(date);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(context,tittle+"\n"+ description+"\n"+date,Toast.LENGTH_SHORT).show();
            }
        });


  container.addView(view,position);
        return view;
    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
