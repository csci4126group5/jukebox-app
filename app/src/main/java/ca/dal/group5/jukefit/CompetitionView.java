package ca.dal.group5.jukefit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ca.dal.group5.jukefit.API.MockAPI;

public class CompetitionView extends AppCompatActivity {

    ListView listview;
    private String[] playerNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_view);
        MockAPI objMockAPI = new MockAPI();
        listview = (ListView)findViewById(R.id.SongListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.songlistitem, R.id.SongName, objMockAPI.GetPlayerNames());
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
