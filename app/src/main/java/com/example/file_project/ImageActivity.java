package com.example.file_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    private ArrayList<Uri> imageUris;
    private ArrayAdapter<String> adapter;
    private ListView listViewImages;
    private ImageView imageViewPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageUris = new ArrayList<>();

        listViewImages = findViewById(R.id.listViewImages);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        Button buttonImportImage = findViewById(R.id.buttonImportImage);
        Button buttonDeleteImage = findViewById(R.id.buttonDeleteImage);

        // Adapter pour afficher les noms des images dans la liste
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewImages.setAdapter(adapter);

        // Importer une image
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageUris.add(selectedImageUri);
                            adapter.add("Image " + imageUris.size());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );

        buttonImportImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Afficher une image dans l'aperçu lorsqu'elle est sélectionnée
        listViewImages.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Uri selectedUri = imageUris.get(position);
            imageViewPreview.setImageURI(selectedUri);
            Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show();
        });

        // Supprimer une image sélectionnée
        buttonDeleteImage.setOnClickListener(v -> {
            int selectedIndex = listViewImages.getCheckedItemPosition();
            if (selectedIndex != ListView.INVALID_POSITION) {
                imageUris.remove(selectedIndex);
                adapter.remove(adapter.getItem(selectedIndex));
                adapter.notifyDataSetChanged();
                imageViewPreview.setImageURI(null);
                Toast.makeText(this, "Image supprimée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Veuillez sélectionner une image à supprimer", Toast.LENGTH_SHORT).show();
            }
        });
    }
}