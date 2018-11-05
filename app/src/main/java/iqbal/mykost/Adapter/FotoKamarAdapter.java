package iqbal.mykost.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import iqbal.mykost.Kelas.Galery;
import iqbal.mykost.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class FotoKamarAdapter extends RecyclerView.Adapter<FotoKamarAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> kamarList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public FotoKamarAdapter(Context mContext, List<String> kamarList) {
        this.mContext = mContext;
        this.kamarList = kamarList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemlist_kamar, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        // loading album cover using Glide library
        Glide.with(mContext).load(kamarList.get(position).toString()).into(holder.thumbnail);

    }

    /**
     * Showing popup menu when tapping on 3 dots
     */


    /**
     * Click listener for popup menu items
     */

    @Override
    public int getItemCount() {
        return kamarList.size();
    }
}
