package at.schiebung.stefan.schober0018;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;


public class Oracle {

    private Context context;
    private int lastAnswerIndex = -1;

    private int[] statsAnswers;
    private int[] statsOtherAnswers;


    private int answerCount;
    private int answerOtherCount;

    public Oracle(Context context) {
        this.context = context;

        this.answerCount = context.getResources().getStringArray(R.array.schoberAnswers).length;
        this.answerOtherCount = context.getResources().getStringArray(R.array.schoberOtherAnswers).length;

        statsAnswers = new int[answerCount];
        statsOtherAnswers = new int[answerOtherCount];
    }

    private static int[] StringToIntArray(String s) {
        String[] array = s.split(",");
        int[] intArray = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            intArray[i] = Integer.parseInt(array[i]);
        }

        return intArray;
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

    public void loadOracle(Context context) {

        try {
            MasterKey.Builder masterKeyBuilder = new MasterKey.Builder(context);
            MasterKey masterKey = masterKeyBuilder.build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(context, context.getResources().getString(R.string.pref_name), masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);


            this.setStatsAnswers(StringToIntArray(sharedPreferences.getString(context.getResources().getString(R.string.pref_statsAnswers), Arrays.toString(new int[this.getAnswerCount()]))));
            this.setStatsOtherAnswers(StringToIntArray(sharedPreferences.getString(context.getResources().getString(R.string.pref_statsOtherAnswers), Arrays.toString(new int[this.getAnswerOtherCount()]))));

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOracle(Context context) {
        try {
            MasterKey.Builder masterKeyBuilder = new MasterKey.Builder(context);
            MasterKey masterKey = masterKeyBuilder.build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(context, context.getResources().getString(R.string.pref_name), masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(
                    context.getResources().getString(R.string.pref_statsAnswers), Arrays.toString(this.getStatsAnswers()));
            editor.putString(
                    context.getResources().getString(R.string.pref_statsAnswers), Arrays.toString(this.getStatsOtherAnswers()));

            editor.apply();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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

    public int getAnswerCount() {
        return answerCount;
    }

    public int getAnswerOtherCount() {
        return answerOtherCount;
    }
}
