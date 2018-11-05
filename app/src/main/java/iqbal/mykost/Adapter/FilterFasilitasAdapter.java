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
import iqbal.mykost.Fragment.FragmentHome;
import iqbal.mykost.Kelas.Kosan;
import iqbal.mykost.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class FilterFasilitasAdapter extends RecyclerView.Adapter<FilterFasilitasAdapter.MyViewHolder> {

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
    public static List<String> list_fasilitas = new ArrayList();

    private List<Kosan> kosanList;


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


    public FilterFasilitasAdapter(final Context mContext) {
        this.mContext = mContext;
        this.albumList = albumList;
        Firebase.setAndroidContext(this.mContext);
        FirebaseApp.initializeApp(mContext.getApplicationContext());
        ref = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        kosanList = new ArrayList<>();

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
                int hargaKosan = 0;
                int temp = 0;
                int index = 0;
                int nilaiFasilitas = 0;
                kosanList.clear();

                FragmentHome.progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot child : dataSnapshot.getChildren()){

                    nilaiFasilitas = 0;
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

                    Kosan kosan = new Kosan(namaKosan,alamat,latlon,harga,isAc,isKmrMandi,isKasur,isLemari,
                            isDekatKampus,nearKampus,downloadUrl,sisaKamar,isWifi,uidPemilik);

                    if (isAc.equals("on")){
                        nilaiFasilitas++;
                    }
                    if (isLemari.equals("on")){
                        nilaiFasilitas++;
                    }
                    if (isKmrMandi.equals("on")){
                        nilaiFasilitas++;
                    }
                    if (isDekatKampus.equals("on")){
                        nilaiFasilitas++;
                    }
                    if (isWifi.equals("on")){
                        nilaiFasilitas++;
                    }
                    if (isKasur.equals("on")){
                        nilaiFasilitas++;
                    }

                    if (index == 0){
                        temp = nilaiFasilitas;
                    }

                    if (nilaiFasilitas < temp){
                        kosanList.add(kosan);
                        list_key.add(key);
                    }else if (nilaiFasilitas == temp){
                        kosanList.add(kosan);
                        list_key.add(key);
                    }
                    else {

                        Kosan kosanTemp = kosanList.get(index - 1);
                        String keyTemp = list_key.get(index - 1).toString();

                        kosanList.remove(index - 1);
                        kosanList.add(kosan);
                        kosanList.add(kosanTemp);

                        list_key.remove(index - 1);
                        list_key.add(key);
                        list_key.add(keyTemp);

                        temp = nilaiFasilitas;
                    }


                       index++;
                }
                FragmentHome.progressBar.setVisibility(View.GONE);
                notifyDataSetChanged();
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

        holder.title.setText(kosanList.get(position).getNamaKos().toString());
        holder.count.setText("Rp. "+kosanList.get(position).getHarga().toString());
        holder.jmlSayur.setText("Sisa ksong : "+kosanList.get(position).getSisaKamar().toString());


        // loading album cover using Glide library
        Glide.with(mContext).load(kosanList.get(position).getDownloadUrl().toString())
                .into(holder.thumbnail);

        holder.title.setTag(holder);
        holder.count.setTag(holder);
        holder.jmlSayur.setTag(holder);

        holder.title.setOnClickListener(clickListener);
        holder.count.setOnClickListener(clickListener);
        holder.jmlSayur.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            MyViewHolder viewHolder = (MyViewHolder) view.getTag();
            int position = viewHolder.getPosition();
            Intent i = new Intent(mContext.getApplicationContext(), DetailKosanActivity.class);
            i.putExtra("nama",kosanList.get(position).getNamaKos().toString());
            i.putExtra("harga",kosanList.get(position).getHarga().toString());
            i.putExtra("url",kosanList.get(position).getDownloadUrl().toString());
            i.putExtra("key",list_key.get(position).toString());
            i.putExtra("sisaKosong",kosanList.get(position).getSisaKamar().toString());
            i.putExtra("nearKampus",kosanList.get(position).getNearKampus().toString());
            i.putExtra("latlon",kosanList.get(position).getLatlon().toString());
            i.putExtra("alamat",kosanList.get(position).getAlamat().toString());
            i.putExtra("isAc",kosanList.get(position).getIsAC().toString());
            i.putExtra("isLemari",kosanList.get(position).getIsLemari().toString());
            i.putExtra("isKasur",kosanList.get(position).getIsKasur().toString());
            i.putExtra("isWifi",kosanList.get(position).getIsWifi().toString());
            i.putExtra("isKmrMandi",kosanList.get(position).getIsKmrMandi().toString());
            i.putExtra("isNearKampus",kosanList.get(position).getIsDekatKampus().toString());
            mContext.startActivity(i);


        }
    };

    public int getItemCount() {
        return kosanList == null ? 0 : kosanList.size();
       // return 5;
    }

}
