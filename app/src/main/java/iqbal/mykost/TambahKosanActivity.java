package iqbal.mykost;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import iqbal.mykost.Kelas.Kosan;
import iqbal.mykost.Kelas.KosanList;

public class TambahKosanActivity extends AppCompatActivity {

    Switch isAc,isLemari,isKasur,isWifi,isNearKampus,isKamarMandi;
    RelativeLayout relaSpinnerKampus;
    EditText etNamaKosan,etHarga,etSisaKamar;
    private String latlon,alamat;
    ImageView imgBrowse;
    private Double lat,lon, userLon,userLat;
    Button btnSimpan,btnCariAlamat;
    Spinner spinKampus;
    private int PLACE_PICKER_REQUEST = 1;
    TextView txtAlamat;
    public static ProgressBar progressBar;

    DatabaseReference ref;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;

    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    FirebaseUser fbUser;
    DialogInterface.OnClickListener listener;
    Uri uri,file;
    private String nearKampus,SisAc,SisLemari,SisKasur,SisWifi,SisNearKampus,SisKamarMandi;
    Kosan kosan;
    KosanList kosanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kosan);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(TambahKosanActivity.this);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }
        ref = FirebaseDatabase.getInstance().getReference();
        nearKampus = "off";
        SisAc = "off";
        SisLemari = "off";
        SisKasur = "off";
        SisWifi = "off";
        SisNearKampus = "off";
        SisKamarMandi = "off";

        relaSpinnerKampus = (RelativeLayout) findViewById(R.id.relaSpinnerKampus);
        isAc = (Switch) findViewById(R.id.toogleAC);
        isLemari = (Switch) findViewById(R.id.toogleLemari);
        isKasur = (Switch) findViewById(R.id.toogleKasur);
        isWifi = (Switch) findViewById(R.id.toogleWifi);
        isNearKampus = (Switch) findViewById(R.id.toogleNearKampus);
        isKamarMandi = (Switch) findViewById(R.id.toogleWC);
        spinKampus = (Spinner) findViewById(R.id.sp_kampus);
        btnCariAlamat = (Button) findViewById(R.id.btnPilihAlamat);
        btnSimpan = (Button) findViewById(R.id.signUpBtn);
        etHarga = (EditText) findViewById(R.id.etHargaKos);
        etNamaKosan = (EditText) findViewById(R.id.etNamaKos);
        etSisaKamar = (EditText) findViewById(R.id.etSisaKamar);
        txtAlamat = (TextView) findViewById(R.id.createAccount);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgBrowse = (ImageView) findViewById(R.id.img_browse);

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

        imgBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TambahKosanActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });

        btnCariAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(TambahKosanActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
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
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
             //   customToast(nearKampus);
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

    }

    private void matikanKomponen(){
        progressBar.setVisibility(View.VISIBLE);
        etHarga.setEnabled(false);
        etNamaKosan.setEnabled(false);
        imgBrowse.setEnabled(false);
        etSisaKamar.setEnabled(false);
        btnCariAlamat.setEnabled(false);
        isNearKampus.setEnabled(false);
        isAc.setEnabled(false);
        isKamarMandi.setEnabled(false);
        isKasur.setEnabled(false);
        isLemari.setEnabled(false);
        isWifi.setEnabled(false);
        btnSimpan.setEnabled(false);
        spinKampus.setEnabled(false);
    }

    private void hidupkanKomponen(){
        progressBar.setVisibility(View.GONE);
        etHarga.setEnabled(true);
        etNamaKosan.setEnabled(true);
        imgBrowse.setEnabled(true);
        etSisaKamar.setEnabled(true);
        btnCariAlamat.setEnabled(true);
        isNearKampus.setEnabled(true);
        isAc.setEnabled(true);
        isKamarMandi.setEnabled(true);
        isKasur.setEnabled(true);
        isLemari.setEnabled(true);
        isWifi.setEnabled(true);
        btnSimpan.setEnabled(true);
        spinKampus.setEnabled(true);
    }

    private void checkValidation(){
        String getNama = etNamaKosan.getText().toString();
        String getHarga = etHarga.getText().toString();
        String getSisa = etSisaKamar.getText().toString();
        String getAlamat = txtAlamat.getText().toString();
        matikanKomponen();

        if (getNama.equals("") || getNama.length() == 0
                || getHarga.equals("") || getHarga.length() == 0
                || getSisa.equals("") || getSisa.length() == 0
                || getAlamat.equals("Alamat") || getAlamat.length() == 0
                ) {

            customToast("Semua Field harus diiisi harus diisi");
            hidupkanKomponen();
        }else if (uri == null){
            customToast("Pilih Gambar Utama Kost dahulu");
            hidupkanKomponen();
        }else if (SisNearKampus.equals("on") && nearKampus.equals("off")){
            customToast("Pilih Kampus terdekat");
            hidupkanKomponen();
        }else {

            uploadGambar(uri);
            hidupkanKomponen();
        }
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
                Toast.makeText(TambahKosanActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(TambahKosanActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                // save image to database


                String key = ref.child("kosan").push().getKey();
                kosan = new Kosan(etNamaKosan.getText().toString(),
                        txtAlamat.getText().toString(),
                        latlon,
                        etHarga.getText().toString(),
                        SisAc,SisKamarMandi,SisKasur,SisLemari,SisNearKampus,nearKampus,
                        downloadUrl.toString(),
                        etSisaKamar.getText().toString(),
                        SisWifi,
                        SharedVariable.userID
                        );
                ref.child("kosan").child(key).setValue(kosan);

                kosanList = new KosanList(key,etNamaKosan.getText().toString());
                ref.child("pemilik").child(SharedVariable.userID).child("kosanList").child(key).setValue(kosanList);

                etHarga.setText("");
                etNamaKosan.setText("");
                etHarga.setText("");
                etSisaKamar.setText("");
                txtAlamat.setText("Alamat");
                imgBrowse.setImageResource(R.drawable.empty_image);
                customToast("Berhasil Menambahkan Kosan, Silakan upload gambar lain tentang Kosan");

                Intent i = new Intent(getApplicationContext(),UploadGambarKosan.class);
                i.putExtra("keyKosan",key);
                startActivity(i);
                finish();
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
                alamat = (String) place.getAddress();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                latlon = lat+","+lon;
                Toast.makeText(getApplicationContext()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }else

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            final String tipe = GetMimeType(TambahKosanActivity.this,uri);
            //Toast.makeText(TambahKosanActivity.this, "Tipe : !\n" + tipe, Toast.LENGTH_LONG).show();

            imgBrowse.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            imgBrowse.setImageURI(uri);
        }
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
