package com.btds.app.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.btds.app.R;

public class GaleryActivity extends BasicActivity {

    private ImageView selectedImage;
    private Bitmap currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery);

        selectedImage = findViewById(R.id.selectedImage);
        Button openGallery = findViewById(R.id.opengallery);

        openGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 5);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 5) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    System.out.println("TEST imagen");
                    System.out.println("RUTA IMAGEN: "+photoUri.getPath());
                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    selectedImage.setImageBitmap(currentImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("NO ESTA ENTRANDO");
        }
    }

}
