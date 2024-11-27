package com.example.file_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class ArchivesActivity extends AppCompatActivity {

    private ListView listViewArchives;
    private ArrayList<String> archiveFileNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archives);

        listViewArchives = findViewById(R.id.listViewArchives);
        archiveFileNames = new ArrayList<>();

        // Load the files in the Archives folder
        loadArchivedFiles();

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, archiveFileNames);
        listViewArchives.setAdapter(adapter);

        // Handle the result from DocumentActivity if a new file was added
        Intent intent = getIntent();
        String compressedFileName = intent.getStringExtra("compressedFileName");
        if (compressedFileName != null) {
            archiveFileNames.add(compressedFileName);  // Add the new compressed file to the list
            adapter.notifyDataSetChanged();
        }

        // Handle item click in the archives (if needed, add functionality)
        listViewArchives.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFile = archiveFileNames.get(position);
            Toast.makeText(ArchivesActivity.this, "Selected: " + selectedFile, Toast.LENGTH_SHORT).show();
            // Add additional actions like opening the file if needed
        });
    }

    private void loadArchivedFiles() {
        File archivesDir = new File(getFilesDir(), "Archives");
        File[] files = archivesDir.listFiles();

        if (files != null) {
            for (File file : files) {
                archiveFileNames.add(file.getName());  // Add file names to the list
            }
        }
    }
}
