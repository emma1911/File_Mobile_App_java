package com.example.file_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileDetailsActivity extends AppCompatActivity {

    private EditText editTextFileContent;
    private EditText editTextRenameFile;
    private Button buttonSaveChanges;
    private Button buttonDeleteFile;
    private Button buttonRenameFile;
    private Button buttonCompressFile;  // Button to compress the file
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);

        editTextFileContent = findViewById(R.id.editTextFileContent);
        editTextRenameFile = findViewById(R.id.editTextRenameFile);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonDeleteFile = findViewById(R.id.buttonDeleteFile);
        buttonRenameFile = findViewById(R.id.buttonRenameFile);
        buttonCompressFile = findViewById(R.id.buttonCompressFile);  // Initialize the Compress button

        String fileName = getIntent().getStringExtra("fileName");
        file = new File(getFilesDir(), fileName);

        if (file.exists()) {
            loadFileContent();
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Save changes to the file
        buttonSaveChanges.setOnClickListener(v -> saveFileContent());

        // Delete the file
        buttonDeleteFile.setOnClickListener(v -> {
            if (file.delete()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("fileDeleted", true);
                resultIntent.putExtra("fileName", file.getName());
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error deleting file", Toast.LENGTH_SHORT).show();
            }
        });

        // Rename the file
        buttonRenameFile.setOnClickListener(v -> renameFile());

        // Compress the file
        buttonCompressFile.setOnClickListener(v -> compressFile());
    }

    private void loadFileContent() {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            editTextFileContent.setText(contentBuilder.toString().trim());
        } catch (IOException e) {
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveFileContent() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(editTextFileContent.getText().toString());
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void renameFile() {
        String newFileName = editTextRenameFile.getText().toString().trim();
        if (newFileName.isEmpty()) {
            Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        File newFile = new File(getFilesDir(), newFileName);
        if (newFile.exists()) {
            Toast.makeText(this, "File with this name already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        if (file.renameTo(newFile)) {
            Toast.makeText(this, "File renamed successfully", Toast.LENGTH_SHORT).show();

            // Send the new file name back to DocumentActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("fileRenamed", true);
            resultIntent.putExtra("oldFileName", file.getName());
            resultIntent.putExtra("newFileName", newFileName);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Error renaming file", Toast.LENGTH_SHORT).show();
        }
    }

    private void compressFile() {
        // Create the archive directory if it doesn't exist
        File archiveDir = new File(getFilesDir(), "Archives");
        if (!archiveDir.exists()) {
            archiveDir.mkdirs();
        }

        // Compress the file
        try {
            File zipFile = new File(archiveDir, file.getName() + ".zip");

            // Check if the .zip file already exists to avoid overwriting
            if (zipFile.exists()) {
                Toast.makeText(this, "Compressed file already exists in Archives", Toast.LENGTH_SHORT).show();
            } else {
                // Create the .zip file in the Archives directory
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);

                        // Copy the original file content into the zip file
                        Files.copy(file.toPath(), zipOut);
                        zipOut.closeEntry();
                    }
                }

                // Notify the user
                Toast.makeText(this, "File compressed and saved in Archives", Toast.LENGTH_SHORT).show();
            }

            // Send the result back to DocumentActivity (optional)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("compressedFileName", zipFile.getName());
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (IOException e) {
            Toast.makeText(this, "Error compressing file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
