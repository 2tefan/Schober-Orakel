package at.schiebung.stefan.schober0018;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;


public class Oracle {

    private final Context context;
    private int lastAnswerIndex = -1;

    private int[] statsAnswers;
    private int[] statsOtherAnswers;


    private final int answerCount;
    private final int answerOtherCount;

    public Oracle(Context context) {
        this.context = context;

        this.answerCount = context.getResources().getStringArray(R.array.schoberAnswers).length;
        this.answerOtherCount = context.getResources().getStringArray(R.array.schoberOtherAnswers).length;

        statsAnswers = new int[answerCount];
        statsOtherAnswers = new int[answerOtherCount];
    }

    public void generateNewAnswer(TextView TextViewResult) {
        Random rng = new Random();
        int answerProbability = rng.nextInt(100);
        String[] answerSize;
        int answerProbabilityCap = 95;

        if (answerProbability <= answerProbabilityCap)
            answerSize = context.getResources().getStringArray(R.array.schoberAnswers); // Useful answers
        else
            answerSize = context.getResources().getStringArray(R.array.schoberOtherAnswers); // Not so much

        String answer;
        int answerIndex;
        do {
            answerIndex = rng.nextInt(answerSize.length);
            answer = answerSize[answerIndex];
        } while (answerIndex == lastAnswerIndex);
        lastAnswerIndex = answerIndex;

        if (answerProbability <= answerProbabilityCap) {
            statsAnswers[answerIndex]++;
        } else {
            statsOtherAnswers[answerIndex]++;
        }
        TextViewResult.setText(answer);
    }

    private static int[] stringToIntArray(String s) {
        String[] array = s.split(",");
        int[] intArray = new int[array.length];

        try {
            for (int i = 1; i < array.length - 1; i++) {
                intArray[i] = Integer.parseInt(array[i]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        return intArray;
    }

    public void loadOracle(Context context) {
        try {
            MasterKey.Builder masterKeyBuilder = new MasterKey.Builder(context);
            masterKeyBuilder.setKeyScheme(MasterKey.KeyScheme.AES256_GCM);
            MasterKey masterKey = masterKeyBuilder.build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(context, context.getResources().getString(R.string.pref_name), masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);


            String statsAnswersS = sharedPreferences.getString(context.getResources().getString(R.string.pref_statsAnswers), null);
            String statsOtherAnswersS = sharedPreferences.getString(context.getResources().getString(R.string.pref_statsOtherAnswers), null);

            if (statsAnswersS != null && statsOtherAnswersS != null) {
                this.setStatsAnswers(stringToIntArray(statsAnswersS));
                this.setStatsOtherAnswers(stringToIntArray(statsOtherAnswersS));
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOracle(Context context) {
        try {
            MasterKey.Builder masterKeyBuilder = new MasterKey.Builder(context);
            masterKeyBuilder.setKeyScheme(MasterKey.KeyScheme.AES256_GCM);
            MasterKey masterKey = masterKeyBuilder.build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(context, context.getResources().getString(R.string.pref_name), masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(context.getResources().getString(R.string.pref_statsAnswers), intArrayToString(this.getStatsAnswers()));
            editor.putString(context.getResources().getString(R.string.pref_statsOtherAnswers), intArrayToString(this.getStatsOtherAnswers()));

            editor.apply();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private String intArrayToString(int[] array) {
        StringBuilder stb = new StringBuilder();

        for (int element : array) {
            stb.append(element);
            stb.append(",");
        }

        return stb.toString();
    }

    public int[] getStatsAnswers() {
        return statsAnswers;
    }

    public void setStatsAnswers(int[] statsAnswers) {
        this.statsAnswers = statsAnswers;
    }

    public int[] getStatsOtherAnswers() {
        return statsOtherAnswers;
    }

    public void setStatsOtherAnswers(int[] statsOtherAnswers) {
        this.statsOtherAnswers = statsOtherAnswers;
    }

    public int getAnswerOtherCount() {
        return answerOtherCount;
    }
}
