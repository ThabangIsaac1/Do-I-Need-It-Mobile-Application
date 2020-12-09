package com.example.do_i_need_it;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;


import java.util.ArrayList;


public class MyAdapter extends PagerAdapter implements View.OnClickListener  {

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
        Button viewbtn = view.findViewById(R.id.btncard) ;


        MyModel model = modelArrayList.get(position);

            String name = model.getName();
            String description = model.getDescription();
            String date = model.getDate();
            String image = model.getImage().trim();
            Glide.with(context).load(image)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .disallowHardwareConfig()
                    .into(bannerIv);
            titleTv.setText(name);
            descriptionTv.setText(description);



//View Item properties
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("product_id",model.getProductId());
                intent.putExtra("display_product_name", model.getName());
                intent.putExtra("display_url",model.getSite());
                intent.putExtra("latitude",model.getLatitude());
                intent.putExtra("longitude",model.getLongitude());
                intent.putExtra("display_image",model.getImage());
                intent.putExtra("display_address",model.getAddress());
                context.startActivity(intent);

            }
        });


  container.addView(view,position);

        return view;
    }




    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


    @Override
    public void onClick(View v) {

    }
}
