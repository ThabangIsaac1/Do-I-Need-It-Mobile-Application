package com.example.do_i_need_it;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddItems extends Fragment  {
    private Button addRecordBtn;
    FloatingActionButton popupBtn;
    ImageView productImage;
    private EditText productName_txt, productDescription_txt,productPrice_txt,productSite_txt;
    String productName,productDescription,productPrice,productSite;

    private ViewPager viewPager;
    private ArrayList <MyModel> modelArrayList;
    private MyAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_add_items,container,false);
        Dialog dialog = new Dialog(getContext());

        viewPager = view1.findViewById(R.id.viewPager);
        popupBtn = view1.findViewById(R.id.fab);
        dialog.setTitle("Add Item");

        // inflate the layout
        dialog.setContentView(R.layout.additem_dialog);

        addRecordBtn = dialog.findViewById(R.id.upload);
        productName_txt = dialog.findViewById(R.id.productName);
        productDescription_txt = dialog.findViewById(R.id.productDescription);
        productSite_txt = dialog.findViewById(R.id.productSite);
        productPrice_txt = dialog.findViewById(R.id.productPrice);
        productImage = dialog.findViewById(R.id.productImage);

        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the dialog
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
            }

        });

        addRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Test", Toast.LENGTH_SHORT).show();
            }
        });






        loadCards();



     return  view1;
    }

    private void loadCards() {
            modelArrayList = new ArrayList<>();

            modelArrayList.add(new MyModel(
                    "Nike Shoes",
                    "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n",
                    "03/08/2020",
                     R.drawable.one
            ));
modelArrayList.add(new MyModel("Nike Shoes 2","\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n","03/08/2020",R.drawable.two));
        modelArrayList.add(new MyModel("Nike Shoes 4","\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia n","03/08/2020",R.drawable.three));

        myAdapter = new MyAdapter(getContext(), modelArrayList);
        viewPager.setAdapter(myAdapter);
        viewPager.setPadding(100,0,100,0);

    }


}
