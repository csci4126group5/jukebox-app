package ca.dal.group5.jukefit.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import ca.dal.group5.jukefit.R;

/**
 * Created by lockhart on 2017-06-14.
 */
public class CreateGroupDialog extends DialogFragment {

    CreateGroupDialogListener listener;

    public interface CreateGroupDialogListener {
        public void onGroupCreated(String nickname);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (CreateGroupDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CreateGroupDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create Group")
                .setView(R.layout.creategroup)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nickname = (EditText) ((Dialog) dialog).findViewById(R.id.groupNickname);
                        listener.onGroupCreated(nickname.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, do nothing
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}