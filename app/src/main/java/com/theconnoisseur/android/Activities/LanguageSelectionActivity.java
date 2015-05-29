package com.theconnoisseur.android.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.theconnoisseur.android.Model.LanguageSelectionListItem;
import com.theconnoisseur.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LanguageSelectionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

       // Link ListView with languages adapter
        setSimpleAdapter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_language_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goBack(View v) {
        startActivity(new Intent(LanguageSelectionActivity.this, MainMenuActivity.class));
    }

    private void setSimpleAdapter() {
        ListView languages = (ListView) findViewById(R.id.languages_list);

        String[] from = new String[] {"language_text"};
        int[] to = new int[] {R.id.language_text};

        //TODO: link to database, query properly. (visual example below)

        List<HashMap<String, String>> map = new ArrayList<HashMap<String, String>>();
        map.add(new LanguageSelectionListItem("FRENCH", 1).getMap());
        map.add(new LanguageSelectionListItem("GERMAN", 2).getMap());
        map.add(new LanguageSelectionListItem("ITALIAN", 3).getMap());
        map.add(new LanguageSelectionListItem("RUSSIAN", 4).getMap());
        map.add(new LanguageSelectionListItem("POLISH", 5).getMap());
        map.add(new LanguageSelectionListItem("WHATEVER", 6).getMap());
        map.add(new LanguageSelectionListItem("ANOTHER", 7).getMap());

        SimpleAdapter adapter = new SimpleAdapter(this, map, R.layout.language_list_item, from, to);
        languages.setAdapter(adapter);
    }
}
