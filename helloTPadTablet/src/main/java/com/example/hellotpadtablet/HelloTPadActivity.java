package com.example.hellotpadtablet;

import nxr.tpadnexus.lib.TPadNexusActivity;
import nxr.tpadnexus.lib.views.BlackWhiteView;
import nxr.tpadnexus.lib.views.DepthMapView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HelloTPadActivity extends TPadNexusActivity {

    // Define 'View' classes that will link to the .xml file
//	View basicView;
//	View timeView;
//	FrictionMapView fricView;
    DepthMapView depthView;
    SeekBar freqSeekBar;
    TextView freqView;
    Bitmap defaultBitmap;

    RelativeLayout buttonsLayout;
    RelativeLayout gameCounterLayout;
    RelativeLayout coverLayout;
    RelativeLayout answersLayout;

    TextView tvGameCounter;
    TextView tvTimer;

    ArrayList<String> answers_orig = new ArrayList<String>(8);

    int gameCounter = 0;
    int gameTimer = 15;
    int timePerRound = 15;
    int rightAnswerIndex = 0;
    String rightAnswerString;
    int minFreq = 10000;
    boolean roundOngoing = false;

    private SeekBar.OnSeekBarChangeListener frequencySeekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress >= minFreq) {
                        setFreq(progress);
                    } else {
                        setFreq(minFreq);
                        seekBar.setProgress(minFreq);
                    }
                    freqView.setText(String.valueOf(Integer.valueOf(getFreq())));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the screen to the .xml file that is in the layout folder
        setContentView(R.layout.activity_hello_tpad);

        // Initialize the TPad to the correct driving frequency
        setFreq(42400);

        //Initalize frequency text view
        freqView = (TextView) findViewById(R.id.freqText);
        freqView.setText(String.valueOf(Integer.valueOf(getFreq())));

        //Initialize frequency slider
        freqSeekBar = (SeekBar) findViewById(R.id.seekBar);
        freqSeekBar.setOnSeekBarChangeListener(frequencySeekBarListener);

        buttonsLayout = (RelativeLayout) findViewById(R.id.rlButtons);
        buttonsLayout.setVisibility(View.INVISIBLE);

        gameCounterLayout = (RelativeLayout) findViewById(R.id.rlCounter);
        coverLayout = (RelativeLayout) findViewById(R.id.rlCover);
        tvGameCounter = (TextView) findViewById(R.id.tvCounter);
        tvGameCounter.setText(String.valueOf(gameCounter));
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvTimer.setText(String.valueOf(gameTimer));

        answersLayout = (RelativeLayout) findViewById(R.id.rlAnswers);
        answers_orig.add(0, "Dummy");
        answers_orig.add(1, "Triangle");
        answers_orig.add(2, "Circle");
        answers_orig.add(3, "Square");
        answers_orig.add(4, "Half Circle");
        answers_orig.add(5, "Gem Stone");
        answers_orig.add(6, "Star");
        answers_orig.add(7, "Clover");


//		// Link the first 'View' called basicView to the view with the id=view1
//		basicView = (View) findViewById(R.id.view1);
//		// Set the background color of the view to blue
//		basicView.setBackgroundColor(Color.BLUE);
//
//		basicView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View view, MotionEvent event) {
//				// Grab the x coordinate of the touch event, and the width of the view the event was in
//				float x = event.getX();
//				int width = view.getWidth();
//
//				// The switch case below looks at the event's properties and specifies what type of touch it was
//				switch (event.getAction()) {
//
//				case MotionEvent.ACTION_DOWN:
//					// If the initial touch was on the left half of the view, turn off the TPad, else turn it on to 80%
//					if (x < width / 2f) {
//						sendTPad(0f);
//					} else {
//						sendTPad(.8f);
//					}
//					break;
//
//				case MotionEvent.ACTION_MOVE:
//					// If the user moves to the left half of the view, turn off the TPad, else turn it on to 80%
//					if (x < width / 2f) {
//						sendTPad(0f);
//					} else {
//						sendTPad(.8f);
//					}
//					break;
//
//				case MotionEvent.ACTION_UP:
//					// If the user lifts up their finger from the screen, turn the TPad off (0%)
//					sendTPad(0f);
//					break;
//				}
//
//				return true;
//			}
//		});
//
//		// Same linking as before, only with a different view
//		timeView = (View) findViewById(R.id.view2);
//		timeView.setBackgroundColor(Color.RED);
//
//		timeView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View view, MotionEvent event) {
//				switch (event.getAction()) {
//
//				case MotionEvent.ACTION_DOWN:
//					// Turn on a time-based texture when the view is touched
//					sendTPadTexture(TPadTexture.SAWTOOTH, 35, 1.0f);
//					break;
//
//				case MotionEvent.ACTION_UP:
//					// Turn off the TPad when the user lifts up (set to 0%)
//					sendTPad(0f);
//					break;
//				}
//
//				return true;
//			}
//		});
//
//		// Link friction view to .xml file
//		fricView = (FrictionMapView) findViewById(R.id.view3);
//		// Load in the image stored in the drawables folder
//		Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testimage);
//		// Set the friction data bitmap to the test image
//		fricView.setDataBitmap(defaultBitmap);

        // Same process as for the friction view
        depthView = (DepthMapView) findViewById(R.id.touchView);
        defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.triangle);
        setDepthViewBitmap(R.drawable.triangle);
        depthView.setDataBitmap(defaultBitmap);
    }

    private void setDepthViewBitmap(int picture) {
        defaultBitmap = BitmapFactory.decodeResource(getResources(), picture);
        depthView.setDataBitmap(defaultBitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.hello_tpad_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGame:
                gameCounter = 0;
                newRound();
                return true;

            case R.id.explore:
                coverLayout.setVisibility(View.INVISIBLE);
                gameCounterLayout.setVisibility(View.INVISIBLE);
                buttonsLayout.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void newRound(View view) {
        (findViewById(R.id.btnNext)).setVisibility(View.INVISIBLE);
        newRound();
    }

    public void newRound() {

        coverLayout.setVisibility(View.VISIBLE);
        gameCounterLayout.setVisibility(View.VISIBLE);
        buttonsLayout.setVisibility(View.INVISIBLE);

        rightAnswerIndex = loadRandomObject();
        Log.w("NewRound", "current choice: " + rightAnswerIndex);
        ArrayList<String> tmpAnswers = new ArrayList<String>(8);
        tmpAnswers = (ArrayList<String>) answers_orig.clone();
        ArrayList<String> answersSelection = new ArrayList<String>(4);

        rightAnswerString = tmpAnswers.remove(rightAnswerIndex);
        answersSelection.add(rightAnswerString);

        tmpAnswers.remove(0);

        Collections.shuffle(tmpAnswers);
        answersSelection.add(tmpAnswers.remove(0));
        answersSelection.add(tmpAnswers.remove(1));
        answersSelection.add(tmpAnswers.remove(2));

        Collections.shuffle(answersSelection);
        rightAnswerIndex = answersSelection.indexOf(rightAnswerString);
        Log.w("NewRound", "Right answer \"" + rightAnswerString + "\" index: " + rightAnswerIndex);

        ((Button) findViewById(R.id.btnAnswer1)).setText(answersSelection.get(0));
        ((Button) findViewById(R.id.btnAnswer2)).setText(answersSelection.get(1));
        ((Button) findViewById(R.id.btnAnswer3)).setText(answersSelection.get(2));
        ((Button) findViewById(R.id.btnAnswer4)).setText(answersSelection.get(3));

        // http://stackoverflow.com/questions/6810416/android-countdowntimer-shows-1-for-two-seconds
        new CountDownTimer(timePerRound * 1000, 100) {
            int secondsLeft = 0;

            public void onTick(long millisUntilFinished) {
                if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                    tvTimer.setText(String.valueOf(secondsLeft));
                }
            }

            public void onFinish() {
                tvTimer.setText("0");
                answersLayout.setVisibility(View.VISIBLE);
                coverLayout.setVisibility(View.INVISIBLE);
                (findViewById(R.id.btnNext)).setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public int loadRandomObject() {
        Random rand = new Random();
        int choice = rand.nextInt(7) + 1;

        switch (choice) {
            case 1:
                loadBitmap("triangle");
                break;
            case 2:
                loadBitmap("circle");
                break;
            case 3:
                loadBitmap("square");
                break;
            case 4:
                loadBitmap("half_circle");
                break;
            case 5:
                loadBitmap("gemstone");
                break;
            case 6:
                loadBitmap("star");
                break;
            case 7:
                loadBitmap("clover");
                break;
        }
        return choice;
    }

    public void loadTriangle(View view) {
        loadBitmap("triangle");
    }

    public void loadCircle(View view) {
        loadBitmap("circle");
    }

    public void loadSquare(View view) {
        loadBitmap("square");
    }

    public void loadHalfCircle(View view) {
        loadBitmap("half_circle");
    }


    public void loadGemStone(View view) {
        loadBitmap("gemstone");
    }

    public void loadStar(View view) {
        loadBitmap("star");
    }

    public void loadClover(View view) {
        loadBitmap("clover");
    }

    public void loadBitmap(String bitmapName) {
        int id = getResources().getIdentifier(bitmapName, "drawable", getPackageName());
        defaultBitmap = BitmapFactory.decodeResource(getResources(), id);
        depthView.setDataBitmap(defaultBitmap);
    }

    public void afterAnswers() {
        tvGameCounter.setText(String.valueOf(gameCounter));
        answersLayout.setVisibility(View.INVISIBLE);
        coverLayout.setVisibility(View.INVISIBLE);
    }

    public void checkAnswer1(View view) {
        if (rightAnswerIndex == 0) {
            gameCounter++;
        }
        afterAnswers();
    }

    public void checkAnswer2(View view) {
        if (rightAnswerIndex == 1) {
            gameCounter++;
        }
        afterAnswers();
    }

    public void checkAnswer3(View view) {
        if (rightAnswerIndex == 2) {
            gameCounter++;
        }
        afterAnswers();
    }

    public void checkAnswer4(View view) {
        if (rightAnswerIndex == 3) {
            gameCounter++;
        }
        afterAnswers();
    }
}
