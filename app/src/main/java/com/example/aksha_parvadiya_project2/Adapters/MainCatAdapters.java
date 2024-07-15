package com.example.aksha_parvadiya_project2.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.aksha_parvadiya_project2.OtherCalss.MainCategory;
import com.example.aksha_parvadiya_project2.R;

import java.util.ArrayList;

public class MainCatAdapters extends RecyclerView.Adapter<MainCatAdapters.ViewHolder> {

    Context context;
    ArrayList<MainCategory> mainCatlist;

    public interface OnMainCategoryClickListener {
        void onMainCategoryClicked(String categoryId);
    }

    private OnMainCategoryClickListener listener;
    public MainCatAdapters(Context context, ArrayList<MainCategory> mainCatlist,OnMainCategoryClickListener listener) {
        this.context=context;
        this.mainCatlist=mainCatlist;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.main_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtname.setText(mainCatlist.get(position).getName());

        Glide.with(context)
                .load(mainCatlist.get(position).getImage())
                .into(holder.imgcat);

        String categoryId = mainCatlist.get(position).getId();
        holder.mainLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onMainCategoryClicked(categoryId);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mainCatlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgcat;
        TextView txtname;
        LinearLayout mainLinear;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgcat=itemView.findViewById(R.id.imgcat);
            txtname=itemView.findViewById(R.id.txtname);
            mainLinear=itemView.findViewById(R.id.mainLinear);
        }
    }
}
