package iqbal.mykost;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iqbal.mykost.Adapter.GaleryAdapter;
import iqbal.mykost.Kelas.Galery;
import iqbal.mykost.Kelas.GambarKosan;

public class UploadGambarKosan extends AppCompatActivity {

    Intent i;
    private String keyKosan;
    FloatingActionButton btnTambahGambar;
    Button btnSimpan;
    DatabaseReference ref;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    RecyclerView recycler_view;

    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    static final int C_VISIBILITY_VISIBLE = 0;
    static final int C_VISIBILITY_GONE = 8;
    FirebaseUser fbUser;
    DialogInterface.OnClickListener listener;
    Uri uri,file,uri2,uri3,uri4,uri5;
    public static ProgressBar progressBar;
    int gambarCount = 1;
    int gambarFrom = 0;
    GambarKosan gambarKosan;
    ArrayList<Uri> uriArray = new ArrayList<>();
    private final int PICK_IMAGE_MULTIPLE =1;
    private List<Galery> galeryList;
    GaleryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gambar_kosan);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(UploadGambarKosan.this);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }

        i = getIntent();
        keyKosan = i.getStringExtra("keyKosan");
        Log.d("keykosan : ",keyKosan);
        galeryList = new ArrayList<>();
        adapter = new GaleryAdapter(this,galeryList);

        ref = FirebaseDatabase.getInstance().getReference();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnTambahGambar = (FloatingActionButton) findViewById(R.id.btnTambahGambar);
        btnSimpan = (Button) findViewById(R.id.signUpBtn);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);


        btnTambahGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadGambarKosan.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                }
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_MULTIPLE) {
                Log.e("++data", "" + data.getClipData().getItemCount());// Get count of image here.

                Log.e("++count", "" + data.getClipData().getItemCount());

                if (data.getClipData().getItemCount() > 5) {
                    customToast("Tidak bisa memilih lebih dari 5 gambar, silakan pilih gambar kembali");
                }else {
                    uriArray.clear();
                    galeryList.clear();

                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        uriArray.add(data.getClipData().getItemAt(i).getUri());
                        Galery galery = new Galery(data.getClipData().getItemAt(i).getUri());
                        galeryList.add(galery);
                    }
                    adapter.notifyDataSetChanged();

                    Log.e("SIZE", uriArray.size() + "");

                }

            }
        }
    }

    private void checkValidation(){


        if (uriArray.isEmpty()) {

            customToast("Pilih gambar dahulu");
        }else {

            uploadGambar();
            Log.d("uri : ",uriArray.get(0).toString());
        }
    }

    private void uploadGambar(){

       // progressBar.setVisibility(View.VISIBLE);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");


        Uri[] uriMauDiUpload =new Uri[uriArray.size()];
        for (int a=0;a<uriArray.size();a++){
            uriMauDiUpload[a] = uriArray.get(a);
        }
        int itung = 0;
        for (int c=0;c < uriArray.size(); c++){

            String filename = fbUser.getUid() + "_" + timeStamp+"_"+c;
            StorageReference fileRef = userRef.child(filename);

            progressDialog.show();
            progressBar.setVisibility(View.VISIBLE);
            UploadTask uploadTask = fileRef.putFile(uriArray.get(c));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    progressDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadGambarKosan.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Toast.makeText(UploadGambarKosan.this, "Upload ke "+c+" finish!", Toast.LENGTH_SHORT).show();

                    // save image to database
                    String key = ref.child("kosan").child(keyKosan).child("gambarList").push().getKey();
                    gambarKosan = new GambarKosan(SharedVariable.userID,downloadUrl.toString());

                    ref.child("kosan").child(keyKosan).child("gambarList").child(key).setValue(gambarKosan);
                    progressDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    customToast("Upload gambar berhasil");

                    Intent intent = new Intent(getApplicationContext(),PemilikKosActivity.class);
                    startActivity(intent);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
            itung++;

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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
