package com.mobdeve.machineproj.greenpokerroom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Card> cardArrayList;
    private ArrayList<Player> playerArrayList;
    private ImageView player1card1, player1card2, player2card1, player2card2, player3card1, player3card2, player4card1, player4card2;
    private ImageView community1, community2, community3, community4, community5;
    private TextView player1name, player1stack, player1action;
    private TextView player2name, player2stack, player2action;
    private TextView player3name, player3stack, player3action;
    private TextView player4name, player4stack, player4action;
    private TextView pot;
    private TextView raiseamount;
    private Button btn_call, btn_raise, btn_fold;

    private String user_name;
    private int player_pos = -1; // index of player who has connected
    private int currentBet; // value to bet
    private int toAct; // index of player to act
    private int sb; // small blind
    private int bb; // big blind
    private int minraise;
    private int playerStack;

    AlertDialog.Builder alert_set_name;

    public final String TAG = "SOCKET";



    private URI uri = URI.create("https://greenpokerroom.herokuapp.com/"); // live URI
//    private URI uri = URI.create("http://10.0.2.2:3000"); // test URI
    private Socket socket = IO.socket(uri);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateCards();

        // event listens
        socket.on("PLAYER_ACTION", player_action);
        socket.on("UPDATE_GAME", update_game);
        socket.on("JOIN_CONFIRM", join_confirm);
        socket.on("TOAST", game_toast);
        socket.connect();
        init();

//        socket.emit("test");
//
    }

    private void init() {
        player1card1 = findViewById(R.id.player1card1);
        player1card2 = findViewById(R.id.player1card2);
        player2card1 = findViewById(R.id.player2card1);
        player2card2 = findViewById(R.id.player2card2);
        player3card1 = findViewById(R.id.player3card1);
        player3card2 = findViewById(R.id.player3card2);
        player4card1 = findViewById(R.id.player4card1);
        player4card2 = findViewById(R.id.player4card2);
        player1card1.setImageResource(R.drawable.playingcardback);
        player1card2.setImageResource(R.drawable.playingcardback);
        player2card1.setImageResource(R.drawable.playingcardback);
        player2card2.setImageResource(R.drawable.playingcardback);
        player3card1.setImageResource(R.drawable.playingcardback);
        player3card2.setImageResource(R.drawable.playingcardback);
        player4card1.setImageResource(R.drawable.playingcardback);
        player4card2.setImageResource(R.drawable.playingcardback);

        player1name = findViewById(R.id.player1name);
        player1stack = findViewById(R.id.player1stack);
        player1action = findViewById(R.id.player1action);

        player2name = findViewById(R.id.player2name);
        player2stack = findViewById(R.id.player2stack);
        player2action = findViewById(R.id.player2action);

        player3name = findViewById(R.id.player3name);
        player3stack = findViewById(R.id.player3stack);
        player3action = findViewById(R.id.player3action);

        player4name = findViewById(R.id.player4name);
        player4stack = findViewById(R.id.player4stack);
        player4action = findViewById(R.id.player4action);

        pot = findViewById(R.id.pot);
        raiseamount = findViewById(R.id.raiseamount);

        btn_call = findViewById(R.id.btn_call);
        btn_raise = findViewById(R.id.btn_raise);
        btn_fold = findViewById(R.id.btn_fold);

        community1 = findViewById(R.id.communitycard1);
        community2 = findViewById(R.id.communitycard2);
        community3 = findViewById(R.id.communitycard3);
        community4 = findViewById(R.id.communitycard4);
        community5 = findViewById(R.id.communitycard5);

        player1action.setText("");
        player2action.setText("");
        player3action.setText("");
        player4action.setText("");

        // Game not started yet, buttons become start game and join game
//        btn_raise.setText("START GAME");
//        btn_raise.setOnClickListener(start_game);



        alert_set_name = new AlertDialog.Builder(this);
        final EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        alert_set_name.setView(nameInput);
        alert_set_name.setTitle("Enter your name for the game: ");

        // set up name buttons
        alert_set_name.setPositiveButton("Join/Rejoin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user_name = nameInput.getText().toString();
                try {
                    JSONObject player = new JSONObject();
                    player.put("name", user_name);
                    socket.emit("PLAYER_JOIN", player);
                    Log.i(TAG, "JOIN REQUEST for " + user_name);
                    socket.emit("UPDATE_CLIENT", user_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        alert_set_name.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert_set_name.show();

//        socket.emit("test");
    }

    @Override
    protected void onResume(){
        super.onResume();
        socket.emit("UPDATE_CLIENT", user_name);
    }

    private View.OnClickListener start_game = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            socket.emit("START_GAME");
        }
    };

    private View.OnLongClickListener reset_game = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    };

    private View.OnClickListener click_check = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            try {
                JSONObject action_json = new JSONObject();
                action_json.put("playerIndex", player_pos);
                action_json.put("action", "CHECK");
                action_json.put("value", 0);
                socket.emit("PLAYER_ACTION", action_json);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    };



    private View.OnClickListener click_call = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            try {
                JSONObject action_json = new JSONObject();
                action_json.put("playerIndex", player_pos);
                action_json.put("action", "CALL");
                action_json.put("value", currentBet);
                socket.emit("PLAYER_ACTION", action_json);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener click_raise = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert_raise = new AlertDialog.Builder(getApplicationContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.alert_raise_layout, null);
            SeekBar seekBar = dialoglayout.findViewById(R.id.seekBarRaise);
            TextView editRaise = dialoglayout.findViewById(R.id.editTextRaise);;

            editRaise.setText(""+minraise);
            seekBar.setMax(playerStack);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) editRaise.setText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (seekBar.getProgress() < minraise){
                        seekBar.setProgress(minraise);
                        editRaise.setText(String.valueOf(minraise));
                    }
                }
            });


            alert_raise.setView(dialoglayout);
            alert_set_name.setView(dialoglayout);
            alert_set_name.setTitle("Enter raise amount: ");

            // set up raise buttons
            alert_set_name.setPositiveButton("Raise", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int localRaise = seekBar.getProgress();
                    if (localRaise < minraise) {
                        showToast("Invalid Raise Amount");
                    } else {
                        try {
                            JSONObject action_json = new JSONObject();
                            action_json.put("playerIndex", player_pos);
                            action_json.put("action", "RAISE");
                            action_json.put("value", localRaise);
                            socket.emit("PLAYER_ACTION", action_json);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                }
            });
            alert_set_name.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert_set_name.show();
        }
    };

    private View.OnClickListener click_fold = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            try {
                JSONObject action_json = new JSONObject();
                action_json.put("playerIndex", player_pos);
                action_json.put("action", "FOLD");
                action_json.put("value", 0);
                socket.emit("PLAYER_ACTION", action_json);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    };


    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show());
    }

    private final Emitter.Listener game_toast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showToast( (String) args[0] );
        }
    };

    // event handlers
    private final Emitter.Listener player_action = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = (JSONObject) args[0];

            try {
                JSONObject game = object.getJSONObject("game");
                JSONObject action_res = object.getJSONObject("gamestate");
                if (action_res.getBoolean("isValid")) {
                    updateGame(object.getJSONObject("game"));
                    playerAction(object.getJSONObject("gamestate"));
                } else {
                    Toast.makeText(getApplicationContext(), "Player Action Error", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void playerAction(JSONObject actionResult) throws JSONException{
        int actor = actionResult.getInt("playerIndex");
        String action = actionResult.getString("result");
        int value = actionResult.getInt("value");
        if (value > 0) { action.concat(" " + value); }
        if (toAct == actor) { action = "To Act"; }

        switch (actor){
            case 0:
                player1action.setText(action);
                break;
            case 1:
                player2action.setText(action);
                break;
            case 2:
                player3action.setText(action);
                break;
            case 3:
                player4action.setText(action);
                break;
        }
    }

    private final Emitter.Listener update_game = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = (JSONObject)args[0];
            try {
                if (object.has("notstarted")){
                    pot.setText("Game not started yet.");
                }
//                else if (player_pos == -1){
//                    pot.setText("You didn't join the game");
//                }
                else
                    updateGame(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void updateGame(JSONObject game) throws JSONException  {



        // update game state with object here
        // set game state variables
        Log.i(TAG, "Updating game info" + game.toString());

        // display current player hand
        JSONArray players_json = game.getJSONArray("players");
        JSONObject curplayer;

        // save curplayer info to class and set image in his spot
        curplayer = players_json.getJSONObject(player_pos); // change to playerpos
        playerStack = curplayer.getInt("stack");
        currentBet = game.getInt("currentBet");
        toAct = game.getInt("toact");
        sb = game.getInt("sb");
        bb = game.getInt("bb");
        minraise = currentBet + bb;

        // set click listeners based on game state

        btn_call.setText("CALL");
        btn_raise.setText("RAISE");
        btn_fold.setText("FOLD");
        btn_call.setOnClickListener(click_call);
        btn_raise.setOnClickListener(click_raise);
        btn_fold.setOnClickListener(click_fold);

        // show the player's cards

        switch(player_pos){
            case 0:
                player1card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
                player1card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );
                break;
            case 1:
                player2card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
                player2card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );
                break;
            case 2:
                player3card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
                player3card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );
                break;
            case 3:
                player4card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
                player4card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );
                break;
        }


        // set button, sb, and bb indicators
        curplayer = players_json.getJSONObject(0);
        player1name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 0)
            player1name.append(" (D)");
        if (game.getInt("smallblind") == 0) {
            player1name.append(" (SB)");
        }
        if (game.getInt("bigblind") == 0) {
            player2name.append(" (BB)");
        }
        if (toAct == 0){
            player1action.setText("To Act");
        }
        curplayer = players_json.getJSONObject(1);
        player2name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 1)
            player2name.append(" (D)");
        if (game.getInt("smallblind") == 1) {
            player2name.append(" (SB)");
        }
        if (game.getInt("bigblind") == 1) {
            player2name.append(" (BB)");
        }
        if (toAct == 1){
            player2action.setText("To Act");
        }
        curplayer = players_json.getJSONObject(2);
        player3name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 2)
            player3name.append(" (D)");
        if (game.getInt("smallblind") == 2) {
            player3name.append(" (SB)");
        }
        if (toAct == 2){
            player3action.setText("To Act");
        }
        if (game.getInt("bigblind") == 2) {
            player3name.append(" (BB)");
        }
        curplayer = players_json.getJSONObject(3);
        player4name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 3)
            player4name.append(" (D)");
        if (game.getInt("smallblind") == 3) {
            player4name.append(" (SB)");
        }
        if (game.getInt("bigblind") == 3) {
            player4name.append(" (BB)");
        }
        if (toAct == 3){
            player4action.setText("To Act");
        }
        Log.i(TAG, "Players info updated" + players_json.toString());

        // set stacks for each player

        player1stack.setText( String.valueOf(players_json.getJSONObject(0).getInt("stack")) );
        player2stack.setText( String.valueOf(players_json.getJSONObject(1).getInt("stack")) );
        if (players_json.length() > 2)
            player3stack.setText( String.valueOf(players_json.getJSONObject(2).getInt("stack")) );
        if (players_json.length() > 3)
            player4stack.setText( String.valueOf(players_json.getJSONObject(3).getInt("stack")) );




        // update board and last action

        JSONArray board = game.getJSONArray("board");
        // update pot and last raise
        pot.setText("Pot: " + game.getInt("pot") + " chips");
        raiseamount.setText(game.getInt("currentBet") + " chips to call");

        // update community cards
        // flop
        if (game.getInt("phase") > 0){
            community1.setImageResource(cardArrayList.get(board.getJSONObject(0).getInt("numval")).getImage());
            community2.setImageResource(cardArrayList.get(board.getJSONObject(1).getInt("numval")).getImage());
            community3.setImageResource(cardArrayList.get(board.getJSONObject(2).getInt("numval")).getImage());
        } else {
            community1.setImageResource(R.drawable.playingcardback);
            community2.setImageResource(R.drawable.playingcardback);
            community3.setImageResource(R.drawable.playingcardback);
        }
        // turn
        if (game.getInt("phase") > 1){
            community4.setImageResource(cardArrayList.get(board.getJSONObject(3).getInt("numval")).getImage());
        } else {
            community4.setImageResource(R.drawable.playingcardback);
        }
        // river
        if (game.getInt("phase") > 2){
            community5.setImageResource(cardArrayList.get(board.getJSONObject(4).getInt("numval")).getImage());
        } else {
            community5.setImageResource(R.drawable.playingcardback);
        }
        // showdown
        if (game.getInt("phase") > 3){
            // show cards of unfolded players for showdown
            JSONArray unfolded = game.getJSONArray("unfolded");
            for (int i=0;i<unfolded.length();i++){
                int playerINT  = unfolded.getInt(i);
                if (playerINT == 0){
                    JSONObject player1 = players_json.getJSONObject(playerINT);
                    player1card1.setImageResource(cardArrayList.get(player1.getJSONObject("hand1").getInt("numval")).getImage());
                    player1card2.setImageResource(cardArrayList.get(player1.getJSONObject("hand2").getInt("numval")).getImage());
                }
                if (playerINT == 1){
                    JSONObject player2 = players_json.getJSONObject(playerINT);
                    player2card1.setImageResource(cardArrayList.get(player2.getJSONObject("hand1").getInt("numval")).getImage());
                    player2card2.setImageResource(cardArrayList.get(player2.getJSONObject("hand2").getInt("numval")).getImage());
                }
                if (playerINT == 2){
                    JSONObject player3 = players_json.getJSONObject(playerINT);
                    player3card1.setImageResource(cardArrayList.get(player3.getJSONObject("hand1").getInt("numval")).getImage());
                    player3card2.setImageResource(cardArrayList.get(player3.getJSONObject("hand2").getInt("numval")).getImage());
                }
                if (playerINT == 3){
                    JSONObject player4 = players_json.getJSONObject(playerINT);
                    player4card1.setImageResource(cardArrayList.get(player4.getJSONObject("hand1").getInt("numval")).getImage());
                    player4card2.setImageResource(cardArrayList.get(player4.getJSONObject("hand2").getInt("numval")).getImage());
                }
            }
        }

    }

    private final Emitter.Listener join_confirm = new Emitter.Listener(){
        @Override
        public void call(Object... args){
            JSONObject object = (JSONObject)args[0];
            try {
//                if (user_name == object.getString("name")){
                    user_name = object.getString("name");
                    player_pos = object.getInt("playerPos");
                    socket.emit("UPDATE_CLIENT", user_name);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // setting card images for the layout
    private void populateCards() {
        cardArrayList = new ArrayList<>();
        playerArrayList = new ArrayList<>();

//        Player player = new Player("Drake", 100, " ");
//        playerArrayList.add(player);
//        player = new Player("J Cole", 100," ");
//        playerArrayList.add(player);
//        player = new Player("XXX", 100, " ");
//        playerArrayList.add(player);
//        player = new Player("BlueFace", 100, " ");
//        playerArrayList.add(player);

        // clubs, index 0-12
        Card sample = new Card(1, 0, 0, R.drawable.thetwoofclubs);
        cardArrayList.add(sample);
        sample = new Card(2, 0, 1, R.drawable.thethreeofclubs);
        cardArrayList.add(sample);
        sample = new Card(3, 0, 2, R.drawable.thefourofclubs);
        cardArrayList.add(sample);
        sample = new Card(4, 0, 3, R.drawable.thefiveofclubs);
        cardArrayList.add(sample);
        sample = new Card(5, 0, 4, R.drawable.thesixofclubs);
        cardArrayList.add(sample);
        sample = new Card(6, 0, 5, R.drawable.thesevenofclubs);
        cardArrayList.add(sample);
        sample = new Card(7, 0, 6, R.drawable.theeightofclubs);
        cardArrayList.add(sample);
        sample = new Card(8, 0, 7, R.drawable.thenineofclubs);
        cardArrayList.add(sample);
        sample = new Card(9, 0, 8, R.drawable.thetenofclubs);
        cardArrayList.add(sample);
        sample = new Card(10, 0, 9, R.drawable.thejackofclubs);
        cardArrayList.add(sample);
        sample = new Card(11, 3, 10, R.drawable.thequeenofclubs);
        cardArrayList.add(sample);
        sample = new Card(12, 3, 11, R.drawable.thekingofclubs);
        cardArrayList.add(sample);
        sample = new Card(13, 0, 12, R.drawable.theaceofclubs);
        cardArrayList.add(sample);
        // diamonds, index 13-25
        sample = new Card(1, 1, 13, R.drawable.thetwoofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(2, 1, 14, R.drawable.thethreeofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(3, 1, 15, R.drawable.thefourofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(4, 1, 16, R.drawable.thefiveofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(5, 1, 17, R.drawable.thesixofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(6, 1, 18, R.drawable.thesevenofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(7, 1, 19, R.drawable.theeightofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(8, 1, 20, R.drawable.thenineofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(9, 1, 21, R.drawable.thetenofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(10, 1, 22, R.drawable.thejackofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(11, 1, 23, R.drawable.thequeenofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(12, 1, 24, R.drawable.thekingofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(13, 1, 25, R.drawable.theaceofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(7, 2, 26, R.drawable.theeightofhearts);
        cardArrayList.add(sample);
        sample = new Card(2, 2, 27, R.drawable.thethreeofhearts);
        cardArrayList.add(sample);
        sample = new Card(3, 2, 28, R.drawable.thefourofhearts);
        cardArrayList.add(sample);
        sample = new Card(4, 2, 29, R.drawable.thefiveofhearts);
        cardArrayList.add(sample);
        sample = new Card(5, 2, 30, R.drawable.thesixofhearts);
        cardArrayList.add(sample);
        sample = new Card(6, 2, 31, R.drawable.thesevenofhearts);
        cardArrayList.add(sample);
        sample = new Card(7, 2, 32, R.drawable.theeightofhearts);
        cardArrayList.add(sample);
        sample = new Card(8, 2, 33, R.drawable.thenineofhearts);
        cardArrayList.add(sample);
        sample = new Card(9, 2, 34, R.drawable.thetenofhearts);
        cardArrayList.add(sample);
        sample = new Card(10, 2, 35, R.drawable.thejackofhearts);
        cardArrayList.add(sample);
        sample = new Card(11, 2, 36, R.drawable.thequeenofhearts);
        cardArrayList.add(sample);
        sample = new Card(12, 2, 37, R.drawable.thekingofhearts);
        cardArrayList.add(sample);
        sample = new Card(13, 2, 38, R.drawable.theaceofhearts);
        cardArrayList.add(sample);
        // spades, index 39-51
        sample = new Card(1, 3, 39, R.drawable.thetwoofspades);
        cardArrayList.add(sample);
        sample = new Card(2, 3, 40, R.drawable.thethreeofspades);
        cardArrayList.add(sample);
        sample = new Card(3, 3, 41, R.drawable.thefourofspades);
        cardArrayList.add(sample);
        sample = new Card(4, 3, 42, R.drawable.thefiveofspades);
        cardArrayList.add(sample);
        sample = new Card(5, 3, 43, R.drawable.thesixofspades);
        cardArrayList.add(sample);
        sample = new Card(6, 3, 44, R.drawable.thesevenofspades);
        cardArrayList.add(sample);
        sample = new Card(7, 3, 45, R.drawable.theeightofspades);
        cardArrayList.add(sample);
        sample = new Card(8, 3, 46, R.drawable.thenineofspades);
        cardArrayList.add(sample);
        sample = new Card(9, 3, 47, R.drawable.thetenofspades);
        cardArrayList.add(sample);
        sample = new Card(10, 3, 48, R.drawable.thejackofspades);
        cardArrayList.add(sample);
        sample = new Card(11, 3, 49, R.drawable.thequeenofspades);
        cardArrayList.add(sample);
        sample = new Card(12, 3, 50, R.drawable.thekingofclubs);
        cardArrayList.add(sample);
        sample = new Card(13, 3, 51, R.drawable.theaceofspades);
        cardArrayList.add(sample);

    }
}