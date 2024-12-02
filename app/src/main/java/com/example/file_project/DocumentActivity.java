package com.example.file_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class DocumentActivity extends AppCompatActivity {


    private ArrayList<String> masterFileNames; // Original complete list

    private static final int REQUEST_CODE_CREATE_FILE = 1;
    private static final int REQUEST_CODE_FILE_DETAILS = 2;

    private ListView listViewFiles;
    private Button buttonCreateNewFile;
    private EditText editTextSearch;
    private ImageView imageViewSort;

    private ArrayList<String> fileNames;
    private ArrayAdapter<String> adapter;

    private boolean isSortedAscending = true; // Toggle for sorting order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        // Initialize UI elements
        listViewFiles = findViewById(R.id.listViewFiles);
        buttonCreateNewFile = findViewById(R.id.buttonCreateNewFile);
        editTextSearch = findViewById(R.id.editTextSearch);
        imageViewSort = findViewById(R.id.imageViewSort);

        fileNames = new ArrayList<>();
        loadFileList();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listViewFiles.setAdapter(adapter);

        // Create new file button
        buttonCreateNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentActivity.this, CreateFileActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_FILE);
            }
        });

        // Navigate to FileDetailsActivity on item click
        listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFile = fileNames.get(position);
                Intent intent = new Intent(DocumentActivity.this, FileDetailsActivity.class);
                intent.putExtra("fileName", selectedFile);
                startActivityForResult(intent, REQUEST_CODE_FILE_DETAILS);
            }
        });

        // Filter files as user types in search bar
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFileList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sort files when sort icon is clicked
        imageViewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFileList();
            }
        });
    }

    // Load files into the list
    private void loadFileList() {
        File directory = getFilesDir();
        File[] files = directory.listFiles();

        masterFileNames = new ArrayList<>();
        fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                masterFileNames.add(file.getName());
            }
        }
        fileNames.addAll(masterFileNames); // Initialize `fileNames` with all files
    }


    // Filter file list based on search query
    private void filterFileList(String query) {
        if (query.isEmpty()) {
            // If search is cleared, restore the full list
            fileNames.clear();
            fileNames.addAll(masterFileNames); // Reset to the original list
        } else {
            fileNames.clear(); // Start with an empty list
            for (String fileName : masterFileNames) {
                if (fileName.toLowerCase().contains(query.toLowerCase())) {
                    fileNames.add(fileName);
                }
            }
        }
        adapter.notifyDataSetChanged(); // Refresh the ListView
    }



    // Sort file list alphabetically (toggle between A-Z and Z-A)
    private void sortFileList() {
        if (isSortedAscending) {
            Collections.sort(fileNames, Collections.reverseOrder()); // Z to A
            isSortedAscending = false;
        } else {
            Collections.sort(fileNames); // A to Z
            isSortedAscending = true;
        }
        adapter.notifyDataSetChanged(); // Refresh ListView
        Toast.makeText(this, "List sorted: " + (isSortedAscending ? "A to Z" : "Z to A"), Toast.LENGTH_SHORT).show();
    }

    // Handle results from other activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_FILE && resultCode == RESULT_OK && data != null) {
            // Adding a new file
            String newFileName = data.getStringExtra("newFileName");
            if (newFileName != null) {
                masterFileNames.add(newFileName); // Add to master list
                fileNames.add(newFileName);      // Add to current filtered list
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "New file added: " + newFileName, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_FILE_DETAILS && resultCode == RESULT_OK && data != null) {
            boolean fileDeleted = data.getBooleanExtra("fileDeleted", false);
            boolean fileRenamed = data.getBooleanExtra("fileRenamed", false);

            if (fileDeleted) {
                // Deleting a file
                String fileName = data.getStringExtra("fileName");
                if (fileName != null) {
                    masterFileNames.remove(fileName); // Remove from master list
                    fileNames.remove(fileName);      // Remove from current filtered list
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "File removed from list", Toast.LENGTH_SHORT).show();
                }
            }

            if (fileRenamed) {
                // Renaming a file
                String oldFileName = data.getStringExtra("oldFileName");
                String newFileName = data.getStringExtra("newFileName");

                if (oldFileName != null && newFileName != null) {
                    int masterIndex = masterFileNames.indexOf(oldFileName);
                    int currentIndex = fileNames.indexOf(oldFileName);

                    if (masterIndex >= 0) {
                        masterFileNames.set(masterIndex, newFileName); // Update in master list
                    }
                    if (currentIndex >= 0) {
                        fileNames.set(currentIndex, newFileName);      // Update in current filtered list
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "File renamed successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
