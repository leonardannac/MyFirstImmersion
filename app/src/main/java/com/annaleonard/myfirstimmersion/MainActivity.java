package com.annaleonard.myfirstimmersion;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.NoRouteToHostException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

//    I am writing new stuff so I can push this!
//    more more more
//    push push push

    private CardScrollView mCardScrollView; //horizontal view that shows scrolling cards
    private CardScrollAdapter mCardScrollAdapter; //supplies cards from mCards to mCardScrollView
    private List<CardBuilder> mCards;   //a list to store cards made with CardBuilder
    private Thread thread;

    private EditText joint1pos, joint2pos, joint3pos, joint4pos, joint5pos, joint6pos, joint7pos;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Log.i("MainAct super.onCreate", " ");

        createCards();

        publishCards();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScrollView.activate();
    }

    @Override
    protected void onPause() {
        mCardScrollView.deactivate();
        super.onPause();
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();
        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT).setText("Quick-Know"));
    }

    private void publishCards() {
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
                // Plays tap sound to indicate that TAP actions are supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
            }
        });
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.measuring_menu_item:
//                // Alternative way to start class. Does not require a new Action.
//                Intent intent = new Intent(this, InformationActivity.class);
//                this.startActivity(intent);
//                break;
                startActivity(new Intent(this, MeasuringActivity.class));
                return true;
            case R.id.information_menu_item:
                startActivity(new Intent(this, InformationActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
