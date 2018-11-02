package iqbal.mykost.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import iqbal.mykost.R;
import iqbal.mykost.Album;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    DatabaseReference ref,refUser;
    private FirebaseAuth fAuth;
    private List<Album> albumList;
    private FirebaseAuth.AuthStateListener fStateListener;
    public static List<String> list_nama = new ArrayList();
    public static List<String> list_status = new ArrayList();
    public static List<String> list_harga = new ArrayList();
    public static List<String> list_key = new ArrayList();
    public static List<String> list_jml = new ArrayList();
    public static List<String> list_downloadURL = new ArrayList();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count,jmlSayur;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            jmlSayur = (TextView) view.findViewById(R.id.jmlSayur);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public AlbumsAdapter(final Context mContext) {
        this.mContext = mContext;
        this.albumList = albumList;
        Firebase.setAndroidContext(this.mContext);
        FirebaseApp.initializeApp(mContext.getApplicationContext());
        ref = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.title.setText("Judul");
        holder.count.setText("harga");
        holder.jmlSayur.setText("Sisa ksong : 5 unit");


        // loading album cover using Glide library
        Glide.with(mContext).load(R.drawable.mykost_logo)
                .into(holder.thumbnail);


    }


    /**
     * Showing popup menu when tapping on 3 dots
     */


    /**
     * Click listener for popup menu items
     */


    @Override
    public int getItemCount() {
       // return list_nama == null ? 0 : list_nama.size();
        return 5;
    }

}
