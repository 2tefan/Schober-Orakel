package at.schiebung.stefan.schober0018;

import android.content.Context;

import static at.schiebung.stefan.schober0018.Vars.sf_statsAnswers;
import static at.schiebung.stefan.schober0018.Vars.sf_statsOtherAnswers;

class Saves
{
	public static void loadSaves(Context context)
	{
		SecurePreferences preferences = new SecurePreferences(context,
		                                                      Vars.preferences_name,
		                                                      Vars.preferences_key,
		                                                      true);
		String s1 = preferences.getString(sf_statsAnswers);
		String s2 = preferences.getString(sf_statsOtherAnswers);

		try
		{
			if (s1 != null)
			{
				int[] stats = StringToIntArray(s1);

				System.arraycopy(stats, 0, Vars.statsAnswers, 0, stats.length);
			}

			if (s2 != null)
			{
				int[] stats = StringToIntArray(s1);

				System.arraycopy(stats, 0, Vars.statsOtherAnswers, 0, stats.length);
			}
		}
		catch (Exception e)
		{
			Vars.Werte();
		}
	}

	private static int[] StringToIntArray(String s)
	{
		String[] array    = s.split(",");
		int[]    intArray = new int[array.length];

		for (int i = 0; i < array.length; i++)
		{
			intArray[i] = Integer.parseInt(array[i]);
		}

		return intArray;
	}

	public static void saveSaves(Context context)
	{
		SecurePreferences preferences = new SecurePreferences(context,
		                                                      Vars.preferences_name,
		                                                      Vars.preferences_key,
		                                                      true);
		StringBuilder stb1 = new StringBuilder();
		StringBuilder stb2 = new StringBuilder();

		for (int i = 0; i < Vars.statsAnswers.length; i++)
		{
			stb1.append(String.valueOf(Vars.statsAnswers[i] + ","));
		}

		for (int i = 0; i < Vars.statsOtherAnswers.length; i++)
		{
			stb2.append(String.valueOf(Vars.statsOtherAnswers[i] + ","));
		}

		preferences.put(sf_statsAnswers, stb1.toString());
		preferences.put(sf_statsOtherAnswers, stb2.toString());
	}
}
