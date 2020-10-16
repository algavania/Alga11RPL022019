package com.practice.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.practice.myapplication.R;
import com.practice.myapplication.RealmHelper;
import com.practice.myapplication.model.ItemProperty;
import com.practice.myapplication.model.Preferences;
import com.practice.myapplication.ui.FavoriteActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.UserViewHolder> implements Filterable {

    public List<ItemProperty> dataList;
    public List<ItemProperty> dataListFull;
    OnItemClickListener mListener;
    Context mContext;
    Realm realm;
    RealmHelper realmHelper;
    Preferences preferences;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public FavoriteAdapter(final Context mContext, final List<ItemProperty> dataList) {
        Realm.init(mContext);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);
        realmHelper = new RealmHelper(realm);
        preferences = new Preferences();
        this.mContext = mContext;
        this.dataList = dataList;
        dataListFull = new ArrayList<>(dataList);
        // Realm object only can be accessed in the same thread (in this case is UI thread).
        // Adapter is running on background thread, so we need to run it on UI thread via runnable.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ((FavoriteActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final RealmResults<ItemProperty> allItems = realm.where(ItemProperty.class).findAll();
                        dataListFull = realm.copyFromRealm(allItems);
                    }
                });
            }
        };
        runnable.run();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_list, parent, false);
        return new UserViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int position) {
        Realm.init(mContext);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);
        realmHelper = new RealmHelper(realm);
        preferences = new Preferences();

        holder.tv_listTitle.setText(dataList.get(position).getTitle());
        holder.tv_listDescription.setText(dataList.get(position).getDescription());
        holder.img_favorite.setImageResource(R.drawable.ic_baseline_favorite_red);
        holder.img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ItemProperty> storeList = realmHelper.delete(dataList.get(position));
                dataList = storeList;
                FavoriteActivity favoriteActivity = new FavoriteActivity();
                favoriteActivity.tv_noFavorite = ((Activity) mContext).findViewById(R.id.tv_noFavorite);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

                dataListFull = new ArrayList<>(dataList);

                if (dataList.isEmpty()) {
                    favoriteActivity.tv_noFavorite.setVisibility(View.VISIBLE);
                } else {
                    favoriteActivity.tv_noFavorite.setVisibility(View.INVISIBLE);
                }
            }
        });
        Picasso.get().load(dataList.get(position).getImageUrl())
                .placeholder(R.drawable.icon)
                .fit()
                .into(holder.img_listImage);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_listTitle;
        private TextView tv_listDescription;
        private ImageView img_listImage;
        private ImageView img_favorite;
        private RelativeLayout relativeLayout;

        public UserViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            tv_listTitle = itemView.findViewById(R.id.tv_listTitle);
            tv_listDescription = itemView.findViewById(R.id.tv_listDescription);
            img_favorite = itemView.findViewById(R.id.img_favorite);
            img_listImage = itemView.findViewById(R.id.img_listImage);
            relativeLayout = itemView.findViewById(R.id.relative_layout);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return dataListFilter;
    }

    private Filter dataListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ItemProperty> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (ItemProperty item : dataListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataList = (List) filterResults.values;
            notifyDataSetChanged();
        }
    };
}
