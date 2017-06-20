package ca.dal.group5.jukefit;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import ca.dal.group5.jukefit.API.APISpec;
import ca.dal.group5.jukefit.API.MockAPI;
import ca.dal.group5.jukefit.API.RequestHandler;
import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;

public class CompetitionView extends AppCompatActivity {

    ListView leaderboardListView;
    TextView stepsDifferenceTextView;

    APISpec ServerAPI;
    String groupCode = "ABCD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_view);

        ServerAPI = new MockAPI();

        leaderboardListView = (ListView)findViewById(R.id.playerProgress);
        leaderboardListView.setItemsCanFocus(false);
        leaderboardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        stepsDifferenceTextView = (TextView) findViewById(R.id.remainingSteps);

        updateInformation(0);
    }

    void updateInformation(final int currentSteps) {
        ServerAPI.groupInformation(groupCode, new RequestHandler<Group>() {
            @Override
            public void callback(Group result) {
                setLeaderboard(result);
                setStepsDifference(result, currentSteps);
            }
        });
    }

    void setLeaderboard(Group group) {
        Collections.sort(group.getMembers());
        ArrayList<String> memberInformation = new ArrayList<String>();
        for (Member member : group.getMembers()) {
            memberInformation.add(member.getName() + "                                       " + member.getScore());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.playerlistitem, R.id.playerName, memberInformation);
        leaderboardListView.setAdapter(adapter);
    }

    void setStepsDifference(Group group, int currentSteps) {
        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (Member member : group.getMembers()) {
            scores.add(member.getScore());
        }

        Collections.sort(scores);
        if(currentSteps >= (scores.get(scores.size() - 1))) {
            int diff = currentSteps - scores.get(scores.size() - 2);
            stepsDifferenceTextView.setText("You lead by "+ diff +" steps");
            stepsDifferenceTextView.setTextColor(Color.GREEN);
        } else {
            int diff = scores.get(scores.size() - 1) -  currentSteps;
            stepsDifferenceTextView.setText("You trail by "+ diff + " steps");
            stepsDifferenceTextView.setTextColor(Color.RED);
        }
    }
}
