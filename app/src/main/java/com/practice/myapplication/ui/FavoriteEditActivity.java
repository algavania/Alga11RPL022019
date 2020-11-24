package com.practice.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practice.myapplication.R;
import com.practice.myapplication.RealmHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class FavoriteEditActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Bundle extras;
    String imageUrl, title, description, releaseDate, voteAverage, formerDescription;
    Button btn_edit;
    EditText txt_title, txt_releaseDate, txt_rating, txt_description;
    Uri imageUri, downloadUri;
    int id;
    ImageView img_poster;
    final int IMG_REQUEST = 1000;
    ProgressDialog progressDialog;

    Realm realm;
    RealmHelper realmHelper;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference fileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "Grant the permission to fully access this apps", 10, permissions);
        }

        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

        realmHelper = new RealmHelper(realm);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("photos");

        progressDialog = new ProgressDialog(this);

        txt_title = findViewById(R.id.txt_editTitle);
        txt_releaseDate = findViewById(R.id.txt_editDate);
        txt_rating = findViewById(R.id.txt_editVote);
        txt_description = findViewById(R.id.txt_editDescription);
        img_poster = findViewById(R.id.img_editImage);
        btn_edit = findViewById(R.id.btn_edit);

        extras = getIntent().getExtras();

        if (extras != null) {
            title = extras.getString("title");
            formerDescription = extras.getString("description");
            voteAverage = extras.getString("vote");
            releaseDate = extras.getString("date");
            imageUrl = extras.getString("imageUrl");
            id = extras.getInt("id");
            getSupportActionBar().setTitle(title);
        }

        txt_title.setText(title);
        txt_description.setText(formerDescription);
        txt_rating.setText(voteAverage);
        txt_releaseDate.setText(releaseDate);
        Picasso.get().load(imageUrl).placeholder(R.drawable.icon).fit().into(img_poster);

        img_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editData();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageUrl = data.getDataString();
            Picasso.get().load(imageUri).into(img_poster);
        }
    }

    private void editData() {
        title = txt_title.getText().toString();
        voteAverage = txt_rating.getText().toString();
        releaseDate = txt_releaseDate.getText().toString();
        description = txt_description.getText().toString();
        if (title.isEmpty() || voteAverage.isEmpty() || releaseDate == null ||
                description.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fill the data", Toast.LENGTH_SHORT).show();
        } else {
            double vote = Double.parseDouble(voteAverage);
            if (vote > 10) {
                Toast.makeText(getApplicationContext(), "Rating can't be higher than 10", Toast.LENGTH_SHORT).show();
            } else {
                if (imageUri != null) {
                    progressDialog.setMessage("Updating your changes...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                    fileReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult();
                                imageUrl = downloadUri.toString();
                                realmHelper.update(formerDescription, imageUrl, title, description, releaseDate, voteAverage);
                                Toast.makeText(getApplicationContext(), "Edit successful", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Edit failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    realmHelper.update(formerDescription, imageUrl, title, description, releaseDate, voteAverage);
                    Toast.makeText(getApplicationContext(), "Edit successful", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FavoriteActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Request Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}