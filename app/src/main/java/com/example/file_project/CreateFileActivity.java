package com.example.file_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateFileActivity extends AppCompatActivity {

    private EditText editTextFileName, editTextContent;
    private Button buttonSaveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        editTextFileName = findViewById(R.id.editTextFileName);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSaveFile = findViewById(R.id.buttonSaveFile);

        buttonSaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

    }

    private void saveFile() {
        String fileName = editTextFileName.getText().toString().trim();
        String content = editTextContent.getText().toString();

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un nom de fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }

        File file = new File(getFilesDir(), fileName);

        if (file.exists()) {
            Toast.makeText(this, "Le fichier existe déjà", Toast.LENGTH_SHORT).show();
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes());

            // Send the result back to the calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newFileName", fileName);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Fichier créé avec succès", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors de la création du fichier", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
