package com.example.file_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private ListView listViewCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listViewCategories = findViewById(R.id.listViewCategories);

        // Define categories and corresponding icons (without Archives)
        String[] categories = {"Documents", "Images", "Videos"};
        int[] icons = {
                R.drawable.ic_documents,  // Replace with actual drawable names
                R.drawable.ic_images,
                R.drawable.ic_videos
        };

        // Prepare data for SimpleAdapter
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("icon", icons[i]);
            item.put("text", categories[i]);
            data.add(item);
        }

        // SimpleAdapter to bind data to the ListView
        String[] from = {"icon", "text"};
        int[] to = {R.id.imageViewIcon, R.id.textViewCategory};

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.list_item_with_icon, from, to);
        listViewCategories.setAdapter(adapter);

        // Handle item clicks
        listViewCategories.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = categories[position];

            if (selectedCategory.equals("Documents")) {
                Intent intent = new Intent(HomeActivity.this, DocumentActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomeActivity.this, selectedCategory + " section is not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
