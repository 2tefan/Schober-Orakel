package at.schiebung.stefan.schober0018;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private boolean statsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vars.answerCount = getResources().getStringArray(R.array.schoberAnwsers).length;
        Vars.answerOtherCount = getResources().getStringArray(R.array.schoberOtherAnwers).length;
        Vars.Werte();
        Saves.loadSaves(this);

        animations();

        closeStats(0);


        this.findViewById(android.R.id.content)
                .setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
                    public void onSwipeTop() {
                        openStats();
                    }

                    public void onSwipeRight() {
                        //Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                    }

                    public void onSwipeLeft() {
                        //Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                    }

                    public void onSwipeBottom() {
                        closeStats(1000);
                    }

                });

        findViewById(R.id.btnThink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thinking();
            }
        });
        setTextViewStats();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Saves.saveSaves(this);
    }

    @Override
    public void onBackPressed() {
        if (statsOpen) {
            closeStats(1000);
        } else {
            super.onBackPressed();
        }
    }

    private void Thinking() {
        TextView TextViewResult = findViewById(R.id.textViewResult);
        String result;
        int newResult;
        Random rng = new Random();
        int seriousAnswer = rng.nextInt(100);
        String[] answersS;
        int answerInt;

        if (seriousAnswer <= 95) {
            answersS = getResources().getStringArray(R.array.schoberAnwsers);
        } else {
            answersS = getResources().getStringArray(R.array.schoberOtherAnwers);
        }

        do {
            answerInt = rng.nextInt(answersS.length);
            newResult = answerInt;
            result = answersS[newResult];
        } while (newResult == Vars.oldResult);


        Vars.oldResult = newResult;

        if (seriousAnswer <= 95) {
            Vars.statsAnswers[answerInt]++;
        } else {
            Vars.statsOtherAnswers[answerInt]++;
        }
        TextViewResult.setText(result);
    }

    private void animations() {
        ConstraintLayout myLayout = findViewById(R.id.myLayout);           //Hintergrund
        AnimationDrawable animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(getResources().getInteger(R.integer.duration));
        animationDrawable.setExitFadeDuration(getResources().getInteger(R.integer.duration));
        animationDrawable.start();
    }

    private void openStats() {
        animateStats(1000, false);
    }

    private void openStats(int duration) {
        animateStats(duration, false);
    }

    private void closeStats(int duration) {
        animateStats(duration, true);
    }

    private void animateStats(int duration, boolean closing) {
        ConstraintLayout clLSA = findViewById(R.id.includeStats);
        ConstraintLayout clLMA = findViewById(R.id.includeMain);
        int[] koordinaten = getScreenSize();
        int posLSA;
        int posLMA;
        statsOpen = !closing;

        if (closing) {
            posLSA = Stuff.biggerNumber(koordinaten[0], koordinaten[1]);
            posLMA = 0;
        } else {
            posLSA = 0;
            posLMA = 0 - Stuff.biggerNumber(koordinaten[0], koordinaten[1]);
        }

        animateOA(posLSA, duration, clLSA, closing);
        animateOA(posLMA, duration, clLMA, closing);
    }

    private void animateOA(int pos, int duration, ConstraintLayout cl, boolean closing) {
        ObjectAnimator objAni = ObjectAnimator.ofFloat(cl, "translationY", pos);
        objAni.setDuration(duration);
        objAni.setInterpolator(new DecelerateInterpolator());
        objAni.start();

        if (!closing) {
            setTextViewStats();
        }
    }

    private int[] getScreenSize() {
        WindowManager w = this.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }

        return new int[]{widthPixels, heightPixels};
    }

    private void setTextViewStats() {
        TextView statsName = findViewById(R.id.textViewStatsNames);
        TextView statsCount = findViewById(R.id.textViewStatsCount);

        String[] seriousAnswersS = getResources().getStringArray(R.array.schoberAnwsers);
        String[] otherAnswersS = getResources().getStringArray(R.array.schoberOtherAnwers);

        StringBuilder names = makeStats(seriousAnswersS);
        names.append("\n");
        names.append(makeStats(otherAnswersS));

        StringBuilder count = makeStats(Vars.statsAnswers);
        count.append("\n");
        count.append(makeStats(Vars.statsOtherAnswers));

        statsName.setText(names.toString());
        statsCount.setText(count.toString());
    }

    private static StringBuilder makeStats(int[] s) {
        StringBuilder stb = new StringBuilder();
        for (int i1 : s) {
            stb.append(i1);
            stb.append("\n");
        }

        return stb;
    }

    private static StringBuilder makeStats(String[] s) {
        StringBuilder stb = new StringBuilder();
        for (String s1 : s) {
            stb.append(s1);
            stb.append("\n");
        }

        return stb;
    }

}
