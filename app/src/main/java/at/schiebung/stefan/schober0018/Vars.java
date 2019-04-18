package at.schiebung.stefan.schober0018;

class Vars
{
	static final String preferences_name = "schober-orakel-preferences";
	static final String preferences_key = "thisistheworldbestquerschoberinmyschoberorakel";

	static final String sf_statsAnswers = "150";
	static final String sf_statsOtherAnswers = "149";

	static int oldResult = -1;

	static int answerCount = -1;
	static int[] statsAnswers;

	static int answerOtherCount = -1;
	static int[] statsOtherAnswers;

	public static void Werte()
	{
		statsAnswers = new int[answerCount];
		statsOtherAnswers = new int[answerOtherCount];
	}
}
