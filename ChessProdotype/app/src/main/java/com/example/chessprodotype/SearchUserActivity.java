package com.example.chessprodotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SearchUserActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    AutoCompleteTextView etSearchUser;
    Button btnSearch;
    ArrayList<String> usersNames;
    ArrayAdapter<String> suggestsAdapter, resultAdapter;
    ArrayList<String> searchedUsers;
    ListView lvSearchedUsers;
    Dialog userDialog;
    ImageView ivProfileImage;
    TextView tvFirstName, tvLastName, tvUserRank;
    Button btnAddFriend;
    User selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        etSearchUser = findViewById(R.id.etSearchUser);
        lvSearchedUsers = findViewById(R.id.lvSearchedUsers);
        searchedUsers = new ArrayList<>();
        resultAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedUsers);
        lvSearchedUsers.setAdapter(resultAdapter);
        lvSearchedUsers.setOnItemClickListener(this);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        usersNames = new ArrayList<>();
        for (User user : AppData.users.values()) {
            if (!user.getUserName().equals(AppData.user.getUserName())) {
                usersNames.add(user.getFirstName());
                usersNames.add(user.getLastName());
                usersNames.add(user.getUserName());
            }
        }
        suggestsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, usersNames);
        etSearchUser.setAdapter(suggestsAdapter);
        etSearchUser.setThreshold(1);
    }







    @Override
    public void onClick(View view) {
        if (view == btnSearch){
            searchedUsers.clear();
            String name = etSearchUser.getText().toString();
            if (name != null && name.length() > 1) {
                for (User user :
                        AppData.users.values()) {
                    if (!user.getUserName().equals(AppData.user.getUserName())) {
                        if (user.getUserName().equals(name) || user.getFirstName().equals(name) || user.getLastName().equals(name)) {
                            searchedUsers.add(user.getUserName());
                        }
                    }
                }
            }
            else Toast.makeText(this, "invalid search, try again", Toast.LENGTH_LONG);
            resultAdapter.notifyDataSetChanged();
        }
        else if (view == btnAddFriend){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("select option");
            builder.setMessage("do you sure you want to send this user to a friend request?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes, I'm sure", (DialogInterface.OnClickListener) (dialog, which) -> {
                // When the user click yes button then app will close
                String uName = selected.getUserName();
                dialog.cancel();
                userDialog.cancel();
                userDialog.dismiss();
                AppData.fbRef.child("users").child(uName).child("friend requests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isRequestSent = false;
                        for (DataSnapshot child :
                                snapshot.getChildren()) {
                            if (child.getKey().equals(AppData.user.getUserName()))
                                isRequestSent = true;
                        }
                        if (!isRequestSent)
                            AppData.sendFriendRequest(SearchUserActivity.this, uName);
                        else
                            Toast.makeText(SearchUserActivity.this, "friend request has already been sent.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchUserActivity.this, "friend request sending has failed", Toast.LENGTH_SHORT).show();
                    }
                });

            });
            builder.setNegativeButton("No, cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });
            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            userDialog = new Dialog(this);
            userDialog.setContentView(R.layout.user_profile_dialog);
        selected = AppData.getUser(searchedUsers.get(i));
            userDialog.setTitle(selected.getUserName());
            ivProfileImage = userDialog.findViewById(R.id.ivProfileImage);
            tvFirstName = userDialog.findViewById(R.id.tvFirstName);
            tvLastName = userDialog.findViewById(R.id.tvLastName);
            tvUserRank = userDialog.findViewById(R.id.tvUserRank);
            btnAddFriend = userDialog.findViewById(R.id.btnAddFriend);
            btnAddFriend.setOnClickListener(this);
            selected.putMyImageIntoFrame(this, ivProfileImage);
            tvFirstName.setText("first name:\n " + selected.getFirstName());
            tvLastName.setText("last name:\n " + selected.getLastName());
            tvUserRank.setText("rank: " + selected.getRank());
            if (AppData.user.getFriendsUserNames().contains(selected.getUserName())
            || AppData.friendRequests.containsKey(selected.getUserName())) {
                btnAddFriend.setEnabled(false);
                btnAddFriend.setVisibility(View.INVISIBLE);
            }
            else{
                btnAddFriend.setEnabled(true);
                btnAddFriend.setVisibility(View.VISIBLE);
            }
            userDialog.show();
    }
}