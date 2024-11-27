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

public class FileDetailsActivity extends AppCompatActivity {

    private EditText editTextFileContent;
    private EditText editTextRenameFile;
    private Button buttonSaveChanges;
    private Button buttonDeleteFile;
    private Button buttonRenameFile;
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

        String fileName = getIntent().getStringExtra("fileName");
        file = new File(getFilesDir(), fileName);

        if (file.exists()) {
            loadFileContent();

            // Set the file name as the hint in the Rename EditText
            editTextRenameFile.setHint(fileName);  // Set file name as hint
        } else {
            Toast.makeText(this, "Fichier non trouvé", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Save changes to the file
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFileContent();
            }
        });


        // Delete the file
        buttonDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.delete()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("fileDeleted", true);
                    resultIntent.putExtra("fileName", file.getName());
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(v.getContext(), "Fichier supprimé", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(v.getContext(), "Erreur lors de la suppression du fichier", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Rename the file
        buttonRenameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameFile();
            }
        });
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
            Toast.makeText(this, "Erreur de lecture du fichier", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveFileContent() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(editTextFileContent.getText().toString());
            Toast.makeText(this, "Modifications enregistrées", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors de l'enregistrement du fichier", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void renameFile() {
        String newFileName = editTextRenameFile.getText().toString().trim();
        if (newFileName.isEmpty()) {
            Toast.makeText(this, "Veuillez d'abord renommer votre fichier", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newFileName.endsWith(".txt")) {
            newFileName += ".txt";
        }

        File newFile = new File(file.getParent(), newFileName);
        if (file.renameTo(newFile)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("fileRenamed", true);
            resultIntent.putExtra("oldFileName", file.getName());
            resultIntent.putExtra("newFileName", newFileName);
            setResult(RESULT_OK, resultIntent);
            file = newFile;
            Toast.makeText(this, "Fichier renommé à " + newFileName, Toast.LENGTH_SHORT).show();
//          finish();
        } else {
            Toast.makeText(this, "Erreur lors du renommage du fichier", Toast.LENGTH_SHORT).show();
        }
    }
}
