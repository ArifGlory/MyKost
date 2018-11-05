package iqbal.mykost.Adapter;

import android.content.Context;
import android.content.Intent;
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

import iqbal.mykost.Album;
import iqbal.mykost.DetailKosanActivity;
import iqbal.mykost.Fragment.FragmentHomePemilik;
import iqbal.mykost.R;
import iqbal.mykost.SharedVariable;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterPemilikKos extends RecyclerView.Adapter<AdapterPemilikKos.MyViewHolder> {

    private Context mContext;
    DatabaseReference ref,refUser;
    private FirebaseAuth fAuth;
    private List<Album> albumList;
    private FirebaseAuth.AuthStateListener fStateListener;
    public static List<String> list_nama = new ArrayList();
    public static List<String> list_sisaKosong = new ArrayList();
    public static List<String> list_harga = new ArrayList();
    public static List<String> list_nearKampus = new ArrayList();
    public static List<String> list_isKmrMandi = new ArrayList();
    public static List<String> list_isWifi = new ArrayList();
    public static List<String> list_latlon = new ArrayList();
    public static List<String> list_isKasur = new ArrayList();
    public static List<String> list_isNearKampus = new ArrayList();
    public static List<String> list_isAc = new ArrayList();
    public static List<String> list_key = new ArrayList();
    public static List<String> list_downloadURL = new ArrayList();
    public static List<String> list_isLemari = new ArrayList();
    public static List<String> list_alamat = new ArrayList();

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


    public AdapterPemilikKos(final Context mContext) {
        this.mContext = mContext;
        this.albumList = albumList;
        Firebase.setAndroidContext(this.mContext);
        FirebaseApp.initializeApp(mContext.getApplicationContext());
        ref = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        ref.child("kosan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list_downloadURL.clear();
                list_harga.clear();
                list_key.clear();
                list_nama.clear();
                list_sisaKosong.clear();
                list_key.clear();
                list_isAc.clear();
                list_isKasur.clear();
                list_isKmrMandi.clear();
                list_isWifi.clear();
                list_isNearKampus.clear();
                list_isLemari.clear();
                list_latlon.clear();
                list_alamat.clear();
                FragmentHomePemilik.progressBar.setVisibility(View.VISIBLE);

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    String uidPemilik = child.child("uidPemilik").getValue().toString();
                    String namaKosan = child.child("namaKos").getValue().toString();
                    String harga = child.child("harga").getValue().toString();
                    String sisaKamar = child.child("sisaKamar").getValue().toString();
                    String downloadUrl = child.child("downloadUrl").getValue().toString();
                    String key = child.getKey();
                    String nearKampus = child.child("nearKampus").getValue().toString();
                    String latlon = child.child("latlon").getValue().toString();
                    String isAc = child.child("isAC").getValue().toString();
                    String isLemari = child.child("isLemari").getValue().toString();
                    String isKmrMandi = child.child("isKmrMandi").getValue().toString();
                    String isDekatKampus = child.child("isDekatKampus").getValue().toString();
                    String isKasur = child.child("isKasur").getValue().toString();
                    String isWifi = child.child("isWifi").getValue().toString();
                    String alamat = child.child("alamat").getValue().toString();

                    if (uidPemilik.equals(SharedVariable.userID)){
                        list_sisaKosong.add(sisaKamar);
                        list_nama.add(namaKosan);
                        list_harga.add(harga);
                        list_downloadURL.add(downloadUrl);
                        list_key.add(key);
                        list_nearKampus.add(nearKampus);
                        list_latlon.add(latlon);
                        list_isNearKampus.add(isDekatKampus);
                        list_isWifi.add(isWifi);
                        list_isKmrMandi.add(isKmrMandi);
                        list_isAc.add(isAc);
                        list_isKasur.add(isKasur);
                        list_isLemari.add(isLemari);
                        list_alamat.add(alamat);
                    }

                }
                notifyDataSetChanged();
                FragmentHomePemilik.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.title.setText(list_nama.get(position).toString());
        holder.count.setText("Rp. "+list_harga.get(position).toString());
        holder.jmlSayur.setText("Sisa ksong : "+list_sisaKosong.get(position).toString());


        // loading album cover using Glide library
        Glide.with(mContext).load(list_downloadURL.get(position).toString())
                .into(holder.thumbnail);

        holder.title.setOnClickListener(clickListener);
        holder.count.setOnClickListener(clickListener);
        holder.jmlSayur.setOnClickListener(clickListener);

        holder.title.setTag(holder);
        holder.count.setTag(holder);
        holder.jmlSayur.setTag(holder);

    }


    /**
     * Showing popup menu when tapping on 3 dots
     */


    /**
     * Click listener for click menu items
     */
    View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            AdapterPemilikKos.MyViewHolder viewHolder = (AdapterPemilikKos.MyViewHolder) view.getTag();
            int position = viewHolder.getPosition();
            Intent i = new Intent(mContext.getApplicationContext(), DetailKosanActivity.class);
            i.putExtra("nama",list_nama.get(position).toString());
            i.putExtra("harga",list_harga.get(position).toString());
            i.putExtra("url",list_downloadURL.get(position).toString());
            i.putExtra("key",list_key.get(position).toString());
            i.putExtra("sisaKosong",list_sisaKosong.get(position).toString());
            i.putExtra("nearKampus",list_nearKampus.get(position).toString());
            i.putExtra("latlon",list_latlon.get(position).toString());
            i.putExtra("alamat",list_alamat.get(position).toString());
            i.putExtra("isAc",list_isAc.get(position).toString());
            i.putExtra("isLemari",list_isLemari.get(position).toString());
            i.putExtra("isKasur",list_isKasur.get(position).toString());
            i.putExtra("isWifi",list_isWifi.get(position).toString());
            i.putExtra("isKmrMandi",list_isKmrMandi.get(position).toString());
            i.putExtra("isNearKampus",list_isNearKampus.get(position).toString());
            mContext.startActivity(i);


        }
    };


    @Override
    public int getItemCount() {
        return list_nama == null ? 0 : list_nama.size();
     //   return 3;
    }

}
