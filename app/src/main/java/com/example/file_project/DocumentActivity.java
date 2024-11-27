package com.example.file_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class DocumentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_FILE = 1;
    private static final int REQUEST_CODE_FILE_DETAILS = 2;

    private ListView listViewFiles;
    private Button buttonCreateNewFile;
    private ArrayList<String> fileNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        listViewFiles = findViewById(R.id.listViewFiles);
        buttonCreateNewFile = findViewById(R.id.buttonCreateNewFile);

        fileNames = new ArrayList<>();
        loadFileList();

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listViewFiles.setAdapter(adapter);

        // Navigate to CreateFileActivity to create a new file
        buttonCreateNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentActivity.this, CreateFileActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_FILE);
            }
        });


        // Navigate to FileDetailsActivity when a file is clicked
        listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFile = fileNames.get(position);

                // Navigate to FileDetailsActivity
                Intent intent = new Intent(DocumentActivity.this, FileDetailsActivity.class);
                intent.putExtra("fileName", selectedFile);
                startActivityForResult(intent, REQUEST_CODE_FILE_DETAILS);
            }
        });

    }

    private void loadFileList() {
        File directory = getFilesDir();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
    }

    // Handle results from other activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_FILE && resultCode == RESULT_OK && data != null) {
            String newFileName = data.getStringExtra("newFileName");
            if (newFileName != null) {
                fileNames.add(newFileName);
                adapter.notifyDataSetChanged(); // Refresh the ListView
                Toast.makeText(this, "Nouveau fichier ajouté : " + newFileName, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_FILE_DETAILS && resultCode == RESULT_OK && data != null) {
            boolean fileDeleted = data.getBooleanExtra("fileDeleted", false);
            boolean fileRenamed = data.getBooleanExtra("fileRenamed", false);

            if (fileDeleted) {
                String fileName = data.getStringExtra("fileName");
                if (fileName != null) {
                    fileNames.remove(fileName);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Fichier supprimé de la liste", Toast.LENGTH_SHORT).show();
                }
            }

            if (fileRenamed) {
                String oldFileName = data.getStringExtra("oldFileName");
                String newFileName = data.getStringExtra("newFileName");

                if (oldFileName != null && newFileName != null) {
                    int index = fileNames.indexOf(oldFileName);
                    if (index >= 0) {
                        fileNames.set(index, newFileName);
                        adapter.notifyDataSetChanged();
                    }
                    Toast.makeText(this, "Fichier renommé avec succès", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
