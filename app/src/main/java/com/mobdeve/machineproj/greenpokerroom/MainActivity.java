package com.mobdeve.machineproj.greenpokerroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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
    private SeekBar seekBar;

    private String user_name;
    private int player_pos;
    private int currentTurn;

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
        socket.on("PLAYER_ACTION", playerAction);
        socket.on("NEW_GAME", resetGame);
        socket.on("JOIN_CONFIRM", join_confirmed);
        socket.connect();
        init();

        
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
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject player = new JSONObject();
                    player.put("name", "Sean");
                    player.put("stack", 100);
                    socket.emit("PLAYER_JOIN", player);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        btn_raise = findViewById(R.id.btn_raise);
        btn_raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject blinds = new JSONObject();
                    blinds.put("sb", 1);
                    blinds.put("bb", 2);
                    socket.emit("START_GAME", blinds);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        btn_fold = findViewById(R.id.btn_fold);

        community1 = findViewById(R.id.communitycard1);
        community2 = findViewById(R.id.communitycard2);
        community3 = findViewById(R.id.communitycard3);
        community4 = findViewById(R.id.communitycard4);
        community5 = findViewById(R.id.communitycard5);
        seekBar = findViewById(R.id.seekBar);

        player1card1.setImageResource(cardArrayList.get(0).getImage());
        player1card2.setImageResource(cardArrayList.get(1).getImage());

        player1name.setText(playerArrayList.get(0).getName());
        player1stack.setText("" + playerArrayList.get(0).getStack());
        player1action.setText(playerArrayList.get(0).getAction());

        player2name.setText(playerArrayList.get(1).getName());
        player2stack.setText(""+ playerArrayList.get(1).getStack());
        player2action.setText(playerArrayList.get(1).getAction());

        player3name.setText(playerArrayList.get(2).getName());
        player3stack.setText("" + playerArrayList.get(2).getStack());
        player3action.setText(playerArrayList.get(2).getAction());

        player4name.setText(playerArrayList.get(3).getName());
        player4stack.setText("" + playerArrayList.get(3).getStack());
        player4action.setText(playerArrayList.get(3).getAction());

        seekBar.setVisibility(View.GONE);
    }

    private void updateGame(JSONObject game) throws JSONException  {
        // update game state with object here
        // set game state variables

        currentTurn = game.getInt("currentBet");

        // update player names and chip counts

        JSONArray players_json = game.getJSONArray("players");
        JSONObject curplayer;

        curplayer = players_json.getJSONObject(0);
        player1name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 0)
            player1name.append(" (D)");
        if (game.getInt("smallblind") == 0)
            player1name.append(" (SB)");
        if (game.getInt("bigblind") == 0)
            player1name.append(" (BB)");
//        player1stack.setText(String.valueOf(curplayer.getInt("stack")));
        curplayer = players_json.getJSONObject(1);
        player2name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 1)
            player2name.append(" (D)");
        if (game.getInt("smallblind") == 1)
            player2name.append(" (SB)");
        if (game.getInt("bigblind") == 1)
            player2name.append(" (BB)");
        curplayer = players_json.getJSONObject(2);
        player3name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 2)
            player3name.append(" (D)");
        if (game.getInt("smallblind") == 2)
            player3name.append(" (SB)");
        if (game.getInt("bigblind") == 2)
            player3name.append(" (BB)");
        curplayer = players_json.getJSONObject(3);
        player4name.setText(curplayer.getString("name"));
        if (game.getInt("button") == 3)
            player4name.append(" (D)");
        if (game.getInt("smallblind") == 3)
            player4name.append(" (SB)");
        if (game.getInt("bigblind") == 3)
            player4name.append(" (BB)");

        // display current player hand
        curplayer = players_json.getJSONObject(0); // change to playerpos
        player1card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
        player1card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );

//        curplayer = players_json.getJSONObject(1);
//        player2card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
//        player2card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );
//        curplayer = players_json.getJSONObject(2);
//        player3card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
//        player3card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );
//        curplayer = players_json.getJSONObject(3);
//        player4card1.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand1").getInt("numval") ).getImage() );
//        player4card2.setImageResource( cardArrayList.get( curplayer.getJSONObject("hand2").getInt("numval") ).getImage() );


        Log.i(TAG, "Players info updated" + players_json.toString());

        // update board and last action

        Log.i(TAG, "Updating game info" + game.toString());

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



    // event handlers
    private Emitter.Listener playerAction = new Emitter.Listener(){
        @Override
        public void call(Object... args){
            JSONObject object = (JSONObject)args[0];
            try {
                updateGame(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener resetGame = new Emitter.Listener(){
        @Override
        public void call(Object... args){
            JSONObject object = (JSONObject)args[0];
            try {
                updateGame(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener join_confirmed = new Emitter.Listener(){
        @Override
        public void call(Object... args){
            JSONObject object = (JSONObject)args[0];
            try {
                player_pos = object.getInt("playerPos");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // setting card images for the layout
    private void populateCards() {
        cardArrayList = new ArrayList<>();
        playerArrayList = new ArrayList<>();

        Player player = new Player("Rasheed", 100, " ");
        playerArrayList.add(player);
        player = new Player("Sean", 100, " ");
        playerArrayList.add(player);
        player = new Player("Jolo", 100, " ");
        playerArrayList.add(player);
        player = new Player("Gabriel", 100, " ");
        playerArrayList.add(player);

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