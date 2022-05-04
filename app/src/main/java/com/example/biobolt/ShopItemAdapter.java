package com.example.biobolt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShopItem> shopItemsData;
    private ArrayList<ShopItem> shopItemsDataAll;
    private Context context;
    private int lastPosition = -1;

    ShopItemAdapter(Context context, ArrayList<ShopItem> itemsData) {
        this.shopItemsData = itemsData;
        this.shopItemsDataAll = itemsData;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.termekek, parent, false));
    }

    @Override
    public void onBindViewHolder(ShopItemAdapter.ViewHolder holder, int position) {
        ShopItem currentItem = shopItemsData.get(position);
        
        holder.bindTo(currentItem);

        if(holder.getBindingAdapterPosition() > lastPosition) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(anim);
            lastPosition = holder.getBindingAdapterPosition();
        }

        holder.itemView.setOnLongClickListener(view -> {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.zoom_in));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return shopItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shopFilter;
    }

    private Filter shopFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShopItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = shopItemsDataAll.size();
                results.values = shopItemsDataAll;
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for(ShopItem item : shopItemsDataAll) {
                    if(item.getName().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            shopItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView infoText;
        private TextView priceText;
        private ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.productName);
            infoText = itemView.findViewById(R.id.description);
            priceText = itemView.findViewById(R.id.price);
            itemImage = itemView.findViewById(R.id.itemImage);

        }

        public void bindTo(ShopItem currentItem) {
            nameText.setText(currentItem.getName());
            infoText.setText(currentItem.getInfo());
            priceText.setText(currentItem.getPrice());
            Glide.with(context).load(currentItem.getImageResource()).into(itemImage);

            itemView.findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Kosárba gomb megnyomva.");
                    ((ShopActivity)context).updateAlertIcon(currentItem);
                }
            });

            itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Törlés gomb megnyomva.");
                    ((ShopActivity)context).deleteData(currentItem);
                }
            });
        }
    }
}


