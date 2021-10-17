package com.example.rssreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import java.util.List;

public class UrlListAdapter extends RecyclerView.Adapter<UrlListAdapter.ItemHolder>{

    public UrlListAdapter(List<String> mUrl, Context context) {
        this.mUrl = mUrl;
        this.context = context;
    }

    private List<String> mUrl;
    private Context context;


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_url,null);
        UrlListAdapter.ItemHolder itemHolder=new UrlListAdapter.ItemHolder(v);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UrlListAdapter.ItemHolder itemHolder, final int i){
        String link = mUrl.get(i);
        itemHolder.tvUrl.setText(link);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", link);
                Log.d("AdapterData",link);
                ((Activity)context).setResult(Activity.RESULT_OK,returnIntent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrl.size();
    }
    public class ItemHolder extends RecyclerView.ViewHolder{
        TextView tvUrl;
        public ItemHolder(View itemView) {
            super(itemView);

            tvUrl = itemView.findViewById(R.id.tvUrl);
        }
    }
}
