package at.schiebung.stefan.schober0018;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    private boolean statsOpen = false;
    Oracle oracle;

    private static StringBuilder makeStats(int[] input) {
        StringBuilder stb = new StringBuilder();
        for (int i : input) {
            stb.append(i);
            stb.append("\n");
        }

        return stb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oracle = new Oracle(this);
        oracle.loadOracle(this);

        animations();
        closeStats(0);

        this.findViewById(android.R.id.content)
                .setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
                    public void onSwipeTop() {
                        openStats();
                    }

                    public void onSwipeBottom() {
                        closeStats();
                    }
                });

        findViewById(R.id.btnThink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oracle.generateNewAnswer(findViewById(R.id.textViewResult));
            }
        });

        setTextViewStats();
    }

    @Override
    protected void onPause() {
        super.onPause();
        oracle.saveOracle(this);
    }

    @Override
    public void onBackPressed() {
        if (statsOpen) {
            closeStats();
        } else {
            super.onBackPressed();
        }
    }

    private void openStats() {
        animateStats(1000, false);
    }

    private void openStats(int duration) {
        animateStats(duration, false);
    }

    private void animations() {
        ConstraintLayout myLayout = findViewById(R.id.myLayout);           // Background
        AnimationDrawable animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(getResources().getInteger(R.integer.duration));
        animationDrawable.setExitFadeDuration(getResources().getInteger(R.integer.duration));
        animationDrawable.start();
    }

    private void closeStats(int duration) {
        animateStats(duration, true);
    }

    private void closeStats() {
        animateStats(1000, true);
    }

    private void animateStats(int duration, boolean closing) {
        ConstraintLayout constraintLayoutStats = findViewById(R.id.includeStats);
        ConstraintLayout constraintLayoutMain = findViewById(R.id.includeMain);

        int[] screenSize = getScreenSize();
        int posCLS = 0;
        int posCLM = 0;
        statsOpen = !closing;

        if (closing)
            posCLS = Math.max(screenSize[0], screenSize[1]);
        else
            posCLM = -Math.max(screenSize[0], screenSize[1]);


        startAnimation(posCLS, duration, constraintLayoutStats, closing);
        startAnimation(posCLM, duration, constraintLayoutMain, closing);
    }

    private void startAnimation(int pos, int duration, ConstraintLayout cl, boolean closing) {
        ObjectAnimator objAni = ObjectAnimator.ofFloat(cl, "translationY", pos);
        objAni.setDuration(duration);
        objAni.setInterpolator(new DecelerateInterpolator());
        objAni.start();

        if (!closing)
            setTextViewStats();

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
//        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
//            try {
//                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
//                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        // includes window decorations (statusbar bar/menu bar)
//        if (Build.VERSION.SDK_INT >= 17) {
//            try {
//                Point realSize = new Point();
//                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
//                widthPixels = realSize.x;
//                heightPixels = realSize.y;
//            } catch (Exception ignored) {
//            }
//        }

        return new int[]{widthPixels, heightPixels};
    }

    private void setTextViewStats() {
        TextView statsName = findViewById(R.id.textViewStatsNames);
        TextView statsCount = findViewById(R.id.textViewStatsCount);

        String[] seriousAnswersS = getResources().getStringArray(R.array.schoberAnswers);
        String[] otherAnswersS = getResources().getStringArray(R.array.schoberOtherAnswers);

        StringBuilder names = makeStats(seriousAnswersS);
        names.append("\n");
        names.append(makeStats(otherAnswersS));

        StringBuilder count = makeStats(oracle.getStatsAnswers());
        count.append("\n");
        count.append(makeStats(oracle.getStatsOtherAnswers()));

        statsName.setText(names.toString());
        statsCount.setText(count.toString());
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
