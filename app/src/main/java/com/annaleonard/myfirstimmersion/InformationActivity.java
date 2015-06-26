package com.annaleonard.myfirstimmersion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;


public class InformationActivity extends Activity {

    private CardScrollView mCardScrollView; //horizontal view that shows scrolling cards
    private CardScrollAdapter mCardScrollAdapter; //supplies cards from mCards to mCardScrollView
    private List<CardBuilder> mCards;   //a list to store cards made with CardBuilder

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        publishCards();


    }

    private void createCards(){
        //create new instance of array of cards
        mCards = new ArrayList<CardBuilder>();
        //get data for cards to display from the background thread

        //add cards to array
        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT).setText("User Info: 1.Speak to select joint. 2.Tap for options. 3. Wink for screen-shot."));
        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT).setText("Troubleshooting: Contact Anna Leonard at leonarda@mit.edu or view the github."));
    }

    private void publishCards(){
        mCardScrollView = new CardScrollView(this);
        mCardScrollAdapter = new CardScrollAdapter() {
            @Override
            public int getCount() {
                return mCards.size();
            }

            @Override
            public Object getItem(int position) {
                return mCards.get(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mCards.get(position).getView(convertView, parent);
            }

            @Override
            public int getPosition(Object item) {
                return mCards.indexOf(item);
            }

            @Override
            public int getItemViewType(int position){
                return mCards.get(position).getItemViewType();
            }

            @Override
            public int getViewTypeCount() {
                return CardBuilder.getViewTypeCount();
            }
        };
        mCardScrollView.setAdapter(mCardScrollAdapter);
        // Handle the TAP event.
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays TAP sound to indicate that TAP actions are now supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }

}
