package iqbal.mykost;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iqbal.mykost.Adapter.FotoKamarAdapter;
import iqbal.mykost.Kelas.Galery;
import iqbal.mykost.Kelas.Kosan;
import iqbal.mykost.Kelas.KosanList;

public class DetailKosanActivity extends AppCompatActivity {

    Switch isAc,isLemari,isKasur,isWifi,isNearKampus,isKamarMandi;
    RelativeLayout relaSpinnerKampus;
    EditText etNamaKosan,etHarga,etSisaKamar;
    private String latlon;
    ImageView imgBrowse;
    private Double lat,lon, userLon,userLat;
    Button btnLihatGambar,btnKeMaps,btnCariAlamat,btnUbah;
    Spinner spinKampus;
    private int PLACE_PICKER_REQUEST = 1;
    TextView txtAlamat,txtNearKampus;
    public static ProgressBar progressBar;

    DatabaseReference ref;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;

    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    FirebaseUser fbUser;
    //DialogInterface.OnClickListener listener;
    Uri uri,file;
    private String nearKampus,SisAc,SisLemari,SisKasur,SisWifi,SisNearKampus,SisKamarMandi,latlonKosan,namaKos,
    sisaKamar,downloadUrl,harga,keyKosan,alamat,alamatNow;
    Intent intent;
    private List<String> kamarList;
    FotoKamarAdapter adapter;

    FloatingActionButton fabSetting,fabEdit,fabDelete;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    RelativeLayout relaAlamat;
    DialogInterface.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kosan);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(DetailKosanActivity.this);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();

        nearKampus = intent.getStringExtra("nearKampus");
        namaKos = intent.getStringExtra("nama");
        harga = intent.getStringExtra("harga");
        keyKosan = intent.getStringExtra("key");
        downloadUrl = intent.getStringExtra("url");
        latlonKosan = intent.getStringExtra("latlon");
        sisaKamar = intent.getStringExtra("sisaKosong");
        SisAc = intent.getStringExtra("isAc");
        SisLemari = intent.getStringExtra("isLemari");
        SisWifi = intent.getStringExtra("isWifi");
        SisNearKampus = intent.getStringExtra("isNearKampus");
        SisKamarMandi = intent.getStringExtra("isKmrMandi");
        SisKasur = intent.getStringExtra("isKasur");
        alamat = intent.getStringExtra("alamat");

        kamarList = new ArrayList<>();
        adapter = new FotoKamarAdapter(this,kamarList);



        relaSpinnerKampus = (RelativeLayout) findViewById(R.id.relaSpinnerKampus);
        isAc = (Switch) findViewById(R.id.toogleAC);
        isLemari = (Switch) findViewById(R.id.toogleLemari);
        isKasur = (Switch) findViewById(R.id.toogleKasur);
        isWifi = (Switch) findViewById(R.id.toogleWifi);
        isNearKampus = (Switch) findViewById(R.id.toogleNearKampus);
        isKamarMandi = (Switch) findViewById(R.id.toogleWC);
        spinKampus = (Spinner) findViewById(R.id.sp_kampus);
        btnKeMaps = (Button) findViewById(R.id.btnKeMaps);
        btnLihatGambar = (Button) findViewById(R.id.signUpBtn);
        etHarga = (EditText) findViewById(R.id.etHargaKos);
        etNamaKosan = (EditText) findViewById(R.id.etNamaKos);
        etSisaKamar = (EditText) findViewById(R.id.etSisaKamar);
        txtAlamat = (TextView) findViewById(R.id.createAccount);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgBrowse = (ImageView) findViewById(R.id.img_browse);
        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabSetting = (FloatingActionButton) findViewById(R.id.fabSetting);
        relaAlamat = (RelativeLayout) findViewById(R.id.relaAlamat);
        btnCariAlamat = (Button) findViewById(R.id.btnPilihAlamat);
        btnUbah = (Button) findViewById(R.id.btnUbah);
        txtNearKampus = (TextView) findViewById(R.id.txtNearKampus);

        fab_open = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        etNamaKosan.setEnabled(false);
        etSisaKamar.setEnabled(false);
        etHarga.setEnabled(false);
        isAc.setEnabled(false);
        isLemari.setEnabled(false);
        isNearKampus.setEnabled(false);
        isKamarMandi.setEnabled(false);
        isKasur.setEnabled(false);
        isWifi.setEnabled(false);
        imgBrowse.setEnabled(false);

        Glide.with(this).load(downloadUrl)
                .into(imgBrowse);

        etNamaKosan.setText(namaKos);
        etSisaKamar.setText(sisaKamar);
        etHarga.setText(harga);
        txtAlamat.setText(alamat);


        if (SisAc.equals("on")){
            isAc.setChecked(true);
        }else {
            isAc.setChecked(false);
        }

        if (SisLemari.equals("on")){
            isLemari.setChecked(true);
        }else {
            isLemari.setChecked(false);
        }

        if (SisNearKampus.equals("on")){
            isNearKampus.setChecked(true);
        }else {
            isNearKampus.setChecked(false);
        }

        if (SisKamarMandi.equals("on")){
            isKamarMandi.setChecked(true);
        }else {
            isKamarMandi.setChecked(false);
        }

        if (SisKasur.equals("on")){
            isKasur.setChecked(true);
        }else {
            isKasur.setChecked(false);
        }

        if (SisWifi.equals("on")){
            isWifi.setChecked(true);
        }else {
            isWifi.setChecked(false);
        }

        if (nearKampus.equals("off")){
            txtNearKampus.setVisibility(View.GONE);
        }else {
            txtNearKampus.setText(nearKampus);
        }

        ref.child("kosan").child(keyKosan).child("gambarList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                kamarList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    String downlaodUrl = child.child("downloadUrlGambar").getValue().toString();
                    kamarList.add(downlaodUrl);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnLihatGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater minlfater = LayoutInflater.from(DetailKosanActivity.this);
                View v = minlfater.inflate(R.layout.dialog_gambar_kamar, null);
                final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(DetailKosanActivity.this).create();
                dialog.setView(v);

                final RecyclerView recycler_view = (RecyclerView) v.findViewById(R.id.recycler_view);
                recycler_view.setAdapter(adapter);
                recycler_view.setLayoutManager(new LinearLayoutManager(DetailKosanActivity.this));

                dialog.show();
            }
        });
        btnKeMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent  = new Intent(getApplicationContext(),MapsKostActivity.class);
                intent.putExtra("latlon",latlonKosan);
                startActivity(intent);
            }
        });

        if (!SharedVariable.userID.equals("") || SharedVariable.userID.length() != 0){
            fabSetting.setVisibility(View.VISIBLE);
        }
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFB();
            }
        });
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relaAlamat.setVisibility(View.VISIBLE);
                etNamaKosan.setEnabled(true);
                etSisaKamar.setEnabled(true);
                etHarga.setEnabled(true);
                isAc.setEnabled(true);
                isLemari.setEnabled(true);
                isNearKampus.setEnabled(true);
                isKamarMandi.setEnabled(true);
                isKasur.setEnabled(true);
                isWifi.setEnabled(true);
                btnUbah.setVisibility(View.VISIBLE);
                imgBrowse.setEnabled(true);
            }
        });
        btnCariAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(DetailKosanActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        imgBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailKosanActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });
        isAc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isAc.isChecked()){
                    SisAc = "on";
                }else {
                    SisAc = "off";
                }
            }
        });
        isKamarMandi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isKamarMandi.isChecked()){
                    SisKamarMandi = "on";
                }else {
                    SisKamarMandi = "off";
                }
            }
        });
        isKasur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isKasur.isChecked()){
                    SisKasur = "on";
                }else {
                    SisKasur = "off";
                }
            }
        });
        isLemari.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isLemari.isChecked()){
                    SisLemari = "on";
                }else {
                    SisLemari = "off";
                }
            }
        });
        isWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isWifi.isChecked()){
                    SisWifi = "on";
                }else {
                    SisWifi = "off";
                }
            }
        });
        isNearKampus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isNearKampus.isChecked()){
                    relaSpinnerKampus.setVisibility(View.VISIBLE);
                    SisNearKampus = "on";
                }else {
                    relaSpinnerKampus.setVisibility(View.GONE);
                    SisNearKampus = "off";
                }
            }
        });
        spinKampus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                nearKampus = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailKosanActivity.this);
                builder.setMessage("Anda yakin ingin menghapus menu ini ?");
                builder.setCancelable(false);

                listener = new DialogInterface.OnClickListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == DialogInterface.BUTTON_POSITIVE){
                            ref.child("kosan").child(keyKosan).setValue(null);
                            Intent i = new Intent(getApplicationContext(),PemilikKosActivity.class);
                            startActivity(i);
                        }

                        if(which == DialogInterface.BUTTON_NEGATIVE){
                            dialog.cancel();
                        }
                    }
                };
                builder.setPositiveButton("Ya",listener);
                builder.setNegativeButton("Tidak", listener);
                builder.show();
            }
        });

    }

    private void checkValidation(){
        String getNama = etNamaKosan.getText().toString();
        String getHarga = etHarga.getText().toString();
        String getSisa = etSisaKamar.getText().toString();
        String getAlamat = txtAlamat.getText().toString();

        if (getNama.equals("") || getNama.length() == 0
                || getHarga.equals("") || getHarga.length() == 0
                || getSisa.equals("") || getSisa.length() == 0
                || getAlamat.equals("Alamat") || getAlamat.length() == 0
                ) {

            customToast("Semua Field harus diiisi harus diisi");
        }else if (SisNearKampus.equals("on") && nearKampus.equals("off")){
            customToast("Pilih Kampus terdekat");
        }else if (uri == null){
            updateDataTanpaUri();
        }else {
            uploadGambar(uri);
        }
    }

    private void updateDataTanpaUri(){
        String nm = etNamaKosan.getText().toString();
        String harga = etHarga.getText().toString();
        String alamat = txtAlamat.getText().toString();
        String sisaKosong = etSisaKamar.getText().toString();

        ref.child("kosan").child(keyKosan).child("namaKos").setValue(etNamaKosan.getText().toString());
        ref.child("kosan").child(keyKosan).child("harga").setValue(etHarga.getText().toString());
        ref.child("kosan").child(keyKosan).child("sisaKamar").setValue(sisaKosong);
        ref.child("kosan").child(keyKosan).child("alamat").setValue(txtAlamat.getText().toString());
        ref.child("kosan").child(keyKosan).child("latlon").setValue(latlonKosan);
        ref.child("kosan").child(keyKosan).child("isAc").setValue(SisAc);
        ref.child("kosan").child(keyKosan).child("isDekatKampus").setValue(SisNearKampus);
        ref.child("kosan").child(keyKosan).child("isKasur").setValue(SisKasur);
        ref.child("kosan").child(keyKosan).child("isKmrMandi").setValue(SisKamarMandi);
        ref.child("kosan").child(keyKosan).child("isLemari").setValue(SisLemari);
        ref.child("kosan").child(keyKosan).child("isWifi").setValue(SisWifi);
        ref.child("kosan").child(keyKosan).child("nearKampus").setValue(nearKampus);

        customToast("Berhasil Diubah");
        progressBar.setVisibility(View.GONE);
        etHarga.setText(harga);
        etNamaKosan.setText(nm);
        etSisaKamar.setText("Sisa kamaar : "+sisaKosong);

        etNamaKosan.setEnabled(false);
        etSisaKamar.setEnabled(false);
        etHarga.setEnabled(false);
        isAc.setEnabled(false);
        isLemari.setEnabled(false);
        isNearKampus.setEnabled(false);
        isKamarMandi.setEnabled(false);
        isKasur.setEnabled(false);
        isWifi.setEnabled(false);
        imgBrowse.setEnabled(false);
        relaAlamat.setVisibility(View.GONE);
        relaSpinnerKampus.setVisibility(View.GONE);
    }

    private void uploadGambar(final Uri uri){

        progressBar.setVisibility(View.VISIBLE);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(DetailKosanActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrlNew = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(DetailKosanActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                // save image to database

                String nm = etNamaKosan.getText().toString();
                String harga = etHarga.getText().toString();
                String alamat = txtAlamat.getText().toString();
                String sisaKosong = etSisaKamar.getText().toString();

                ref.child("kosan").child(keyKosan).child("namaKos").setValue(etNamaKosan.getText().toString());
                ref.child("kosan").child(keyKosan).child("harga").setValue(etHarga.getText().toString());
                ref.child("kosan").child(keyKosan).child("downloadUrl").setValue(downloadUrlNew.toString());
                ref.child("kosan").child(keyKosan).child("sisaKamar").setValue(etSisaKamar.getText().toString());
                ref.child("kosan").child(keyKosan).child("alamat").setValue(txtAlamat.getText().toString());
                ref.child("kosan").child(keyKosan).child("latlon").setValue(latlonKosan);
                ref.child("kosan").child(keyKosan).child("isAc").setValue(SisAc);
                ref.child("kosan").child(keyKosan).child("isDekatKampus").setValue(SisNearKampus);
                ref.child("kosan").child(keyKosan).child("isKasur").setValue(SisKasur);
                ref.child("kosan").child(keyKosan).child("isKmrMandi").setValue(SisKamarMandi);
                ref.child("kosan").child(keyKosan).child("isLemari").setValue(SisLemari);
                ref.child("kosan").child(keyKosan).child("isWifi").setValue(SisWifi);
                ref.child("kosan").child(keyKosan).child("nearKampus").setValue(nearKampus);

                customToast("Berhasil Diubah");
                progressBar.setVisibility(View.GONE);
                etHarga.setText(harga);
                etNamaKosan.setText(nm);
                etSisaKamar.setText("Sisa kamaar : "+sisaKosong);

                etNamaKosan.setEnabled(false);
                etSisaKamar.setEnabled(false);
                etHarga.setEnabled(false);
                isAc.setEnabled(false);
                isLemari.setEnabled(false);
                isNearKampus.setEnabled(false);
                isKamarMandi.setEnabled(false);
                isKasur.setEnabled(false);
                isWifi.setEnabled(false);
                imgBrowse.setEnabled(false);
                relaAlamat.setVisibility(View.GONE);
                relaSpinnerKampus.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);
                //tvPlaceAPI.setText(toastMsg);

                txtAlamat.setText(place.getAddress());
                alamatNow = (String) place.getAddress();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                latlon = lat+","+lon;
                latlonKosan = latlon;
                Toast.makeText(getApplicationContext()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }else

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            final String tipe = GetMimeType(DetailKosanActivity.this,uri);
            //Toast.makeText(TambahKosanActivity.this, "Tipe : !\n" + tipe, Toast.LENGTH_LONG).show();

            imgBrowse.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            imgBrowse.setImageURI(uri);
        }

    }

    public static String GetMimeType(Context context, Uri uriImage)
    {
        String strMimeType = null;

        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[] { MediaStore.MediaColumns.MIME_TYPE },
                null, null, null);

        if (cursor != null && cursor.moveToNext())
        {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }

    public  void customToast(String s){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_error);
        text.setText(s);
        Toast toast = new Toast(getApplicationContext());// Get Toast Context
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);// Set
        toast.setDuration(Toast.LENGTH_SHORT);// Set Duration
        toast.setView(layout); // Set Custom View over toast
        toast.show();// Finally show toast
    }

    public void animateFB(){

        if(isFabOpen){

            fabSetting.startAnimation(rotate_backward);
            fabEdit.startAnimation(fab_close);
            fabDelete.startAnimation(fab_close);
            fabEdit.setClickable(false);
            fabDelete.setClickable(false);
            isFabOpen = false;
            Log.d("fab", "close");

        } else {

            fabSetting.startAnimation(rotate_forward);
            fabEdit.startAnimation(fab_open);
            fabDelete.startAnimation(fab_open);
            fabEdit.setClickable(true);
            fabDelete.setClickable(true);
            isFabOpen = true;
            Log.d("fab","open");

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RC_IMAGE_GALLERY);
            }
        }
    }
}
