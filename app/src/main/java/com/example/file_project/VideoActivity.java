package com.example.file_project;




import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.FrameLayout;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private Button btnImport;
    private ListView videoListView;
    private VideoView videoView;
    private Button btnExitFullscreen;

    private ArrayList<Uri> videoUris;
    private ArrayList<String> videoNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio);

        btnImport = findViewById(R.id.btnImport);
        videoListView = findViewById(R.id.videoListView);
        videoView = findViewById(R.id.videoView);
        btnExitFullscreen = findViewById(R.id.btnExitFullscreen);

        videoUris = new ArrayList<>();
        videoNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, videoNames);
        videoListView.setAdapter(adapter);

        btnImport.setOnClickListener(v -> openVideoPicker());

        // Lire la vidéo au clic sur l'élément
        videoListView.setOnItemClickListener((parent, view, position, id) -> playVideo(position));

        // Afficher le menu contextuel au clic long
        videoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showPopupMenu(view, position);
            return true;
        });

        // Bouton pour quitter le mode plein écran
        btnExitFullscreen.setOnClickListener(v -> stopFullscreen());
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            if (videoUri != null) {
                videoUris.add(videoUri);
                videoNames.add(videoUri.getLastPathSegment());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void playVideo(int position) {
        Uri videoUri = videoUris.get(position);
        videoView.setVideoURI(videoUri);
        videoView.setVisibility(View.VISIBLE);
        btnExitFullscreen.setVisibility(View.VISIBLE);  // Afficher le bouton "X"
        videoView.start();

        // Rendre le VideoView plein écran en définissant la largeur et la hauteur à "match_parent"
        videoView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private void stopFullscreen() {
        videoView.stopPlayback();
        videoView.setVisibility(View.GONE);
        btnExitFullscreen.setVisibility(View.GONE);  // Masquer le bouton "X"
    }

    private void showPopupMenu(View view, int position) {
        // Créer un menu contextuel
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Ajouter des éléments au menu dynamiquement
        android.view.Menu menu = popupMenu.getMenu();
        menu.add(0, 1, 0, "Renommer");
        menu.add(0, 2, 1, "Supprimer");


        // Configurer l'action lorsqu'une option est sélectionnée
        popupMenu.setOnMenuItemClickListener(item -> handleMenuItemClick(item, position));

        // Afficher le menu
        popupMenu.show();
    }

    private boolean handleMenuItemClick(MenuItem item, int position) {
        int id = item.getItemId();
        if (id == 1) { // Renommer
            renameVideo(position);
            return true;
        } else if (id == 2) { // Supprimer
            deleteVideo(position);
            return true;
        }
        return false;
    }

    private void renameVideo(int position) {
        // Afficher une boîte de dialogue pour entrer un nouveau nom
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Renommer la vidéo");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Entrez le nouveau nom");
        builder.setView(input);

        builder.setPositiveButton("Renommer", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                videoNames.set(position, newName);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Vidéo renommée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteVideo(int position) {
        // Supprimer la vidéo de la liste
        videoUris.remove(position);
        videoNames.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Vidéo supprimée", Toast.LENGTH_SHORT).show();
    }

}