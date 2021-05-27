package com.mobdeve.machineproj.greenpokerroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateCards();
        init();

        
    }

    private void init() {
        player1card1 = findViewById(R.id.player1card1);
        player1card2=findViewById(R.id.player1card2);
        player2card1 = findViewById(R.id.player2card1);
        player2card2=findViewById(R.id.player2card2);
        player3card1 = findViewById(R.id.player3card1);
        player3card2=findViewById(R.id.player3card2);
        player4card1 = findViewById(R.id.player4card1);
        player4card2=findViewById(R.id.player4card2);

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

        Card sample = new Card(1, 0, 0, R.drawable.thetwoofclubs);
        cardArrayList.add(sample);
        sample = new Card(1, 1, 1, R.drawable.thetwoofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(1, 2, 2, R.drawable.thetwoofhearts);
        cardArrayList.add(sample);
        sample = new Card(1, 3, 3, R.drawable.thetwoofspades);
        cardArrayList.add(sample);

        sample = new Card(2, 0, 4, R.drawable.thethreeofclubs);
        cardArrayList.add(sample);
        sample = new Card(2, 1, 5, R.drawable.thethreeofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(2, 2, 6, R.drawable.thethreeofhearts);
        cardArrayList.add(sample);
        sample = new Card(2, 3, 7, R.drawable.thethreeofspades);
        cardArrayList.add(sample);

        sample = new Card(3, 0, 8, R.drawable.thefourofclubs);
        cardArrayList.add(sample);
        sample = new Card(3, 1, 9, R.drawable.thefourofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(3, 2, 10, R.drawable.thefourofhearts);
        cardArrayList.add(sample);
        sample = new Card(3, 3, 11, R.drawable.thefourofspades);
        cardArrayList.add(sample);

        sample = new Card(4, 0, 12, R.drawable.thefiveofclubs);
        cardArrayList.add(sample);
        sample = new Card(4, 1, 13, R.drawable.thefiveofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(4, 2, 14, R.drawable.thefiveofhearts);
        cardArrayList.add(sample);
        sample = new Card(4, 3, 15, R.drawable.thefiveofspades);
        cardArrayList.add(sample);

        sample = new Card(5, 0, 16, R.drawable.thesixofclubs);
        cardArrayList.add(sample);
        sample = new Card(5, 1, 17, R.drawable.thesixofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(5, 2, 18, R.drawable.thesixofhearts);
        cardArrayList.add(sample);
        sample = new Card(5, 3, 19, R.drawable.thesixofspades);
        cardArrayList.add(sample);

        sample = new Card(6, 0, 20, R.drawable.thesevenofclubs);
        cardArrayList.add(sample);
        sample = new Card(6, 1, 21, R.drawable.thesevenofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(6, 2, 22, R.drawable.thesevenofhearts);
        cardArrayList.add(sample);
        sample = new Card(6, 3, 23, R.drawable.thesevenofspades);
        cardArrayList.add(sample);

        sample = new Card(7, 0, 24, R.drawable.theeightofclubs);
        cardArrayList.add(sample);
        sample = new Card(7, 1, 25, R.drawable.theeightofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(7, 2, 26, R.drawable.theeightofhearts);
        cardArrayList.add(sample);
        sample = new Card(7, 3, 27, R.drawable.theeightofspades);
        cardArrayList.add(sample);

        sample = new Card(8, 0, 28, R.drawable.thenineofclubs);
        cardArrayList.add(sample);
        sample = new Card(8, 1, 29, R.drawable.thenineofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(8, 2, 30, R.drawable.thenineofhearts);
        cardArrayList.add(sample);
        sample = new Card(8, 3, 31, R.drawable.thenineofspades);
        cardArrayList.add(sample);

        sample = new Card(9, 0, 32, R.drawable.thetenofclubs);
        cardArrayList.add(sample);
        sample = new Card(9, 1, 33, R.drawable.thetenofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(9, 2, 34, R.drawable.thetenofhearts);
        cardArrayList.add(sample);
        sample = new Card(9, 3, 35, R.drawable.thetenofspades);
        cardArrayList.add(sample);

        sample = new Card(10, 0, 36, R.drawable.thejackofclubs);
        cardArrayList.add(sample);
        sample = new Card(10, 1, 37, R.drawable.thejackofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(10, 2, 38, R.drawable.thejackofhearts);
        cardArrayList.add(sample);
        sample = new Card(10, 3, 39, R.drawable.thejackofspades);
        cardArrayList.add(sample);

        sample = new Card(11, 0, 40, R.drawable.thequeenofclubs);
        cardArrayList.add(sample);
        sample = new Card(11, 1, 41, R.drawable.thequeenofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(11, 2, 42, R.drawable.thequeenofhearts);
        cardArrayList.add(sample);
        sample = new Card(11, 3, 43, R.drawable.thequeenofspades);
        cardArrayList.add(sample);

        sample = new Card(12, 0, 44, R.drawable.thekingofclubs);
        cardArrayList.add(sample);
        sample = new Card(12, 1, 45, R.drawable.thekingofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(12, 2, 46, R.drawable.thekingofhearts);
        cardArrayList.add(sample);
        sample = new Card(12, 3, 47, R.drawable.thekingofspades);
        cardArrayList.add(sample);

        sample = new Card(13, 0, 48, R.drawable.theaceofclubs);
        cardArrayList.add(sample);
        sample = new Card(13, 1, 49, R.drawable.theaceofdiamonds);
        cardArrayList.add(sample);
        sample = new Card(13, 2, 50, R.drawable.theaceofhearts);
        cardArrayList.add(sample);
        sample = new Card(13, 3, 51, R.drawable.theaceofspades);
        cardArrayList.add(sample);

    }
}