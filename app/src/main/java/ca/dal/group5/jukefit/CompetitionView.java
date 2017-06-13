package ca.dal.group5.jukefit;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ca.dal.group5.jukefit.API.MockAPI;

public class CompetitionView extends AppCompatActivity {

    ListView listview;
    private String[] playerNameList;
    TextView Difference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_view);
        MockAPI objMockAPI = new MockAPI();
        objMockAPI.mockMembers(10);
        listview = (ListView)findViewById(R.id.playerProgress);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.playerlistitem, R.id.playerName, objMockAPI.playerInfo);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        PlaylistAndWorkoutActivity PWAObj = new PlaylistAndWorkoutActivity();
        int [] OrderedScores = objMockAPI.Scores;
        java.util.Arrays.sort(OrderedScores);
        Difference = (TextView) findViewById(R.id.remainingSteps);
        if(Integer.parseInt(PWAObj.Steps) >= OrderedScores[OrderedScores.length - 1])
        {
            int diff = Integer.parseInt(PWAObj.Steps) - OrderedScores[OrderedScores.length - 2];
            Difference.setText("You lead by "+diff+" steps");
            Difference.setTextColor(Color.GREEN);
        }
        else
        {
            int diff = OrderedScores[OrderedScores.length - 1] -  Integer.parseInt(PWAObj.Steps);
            Difference.setText("You trail by "+diff+ " steps");
            Difference.setTextColor(Color.RED);
        }

    }
}
