package uni.apps.responsetesting.utils;

import java.util.ArrayList;
import java.util.Random;

import uni.apps.responsetesting.R;

public class CardOperations {

	public static ArrayList<Integer> setUpCards(int maxCards) {
		int[] ids = new int[] {R.drawable.card_club_1, R.drawable.card_club_2, 
				R.drawable.card_club_3, R.drawable.card_club_4, R.drawable.card_club_5,	R.drawable.card_club_6,
				R.drawable.card_club_7, R.drawable.card_club_8, R.drawable.card_club_9, R.drawable.card_club_10,
				R.drawable.card_club_j,	R.drawable.card_club_q, R.drawable.card_club_k, R.drawable.card_joke_b, 
				R.drawable.card_joke_r, R.drawable.card_diam_1, R.drawable.card_diam_2, R.drawable.card_diam_3,
				R.drawable.card_diam_4, R.drawable.card_diam_5, R.drawable.card_diam_6, R.drawable.card_diam_7,
				R.drawable.card_diam_8, R.drawable.card_diam_9, R.drawable.card_diam_10, R.drawable.card_diam_j, 
				R.drawable.card_diam_q, R.drawable.card_diam_k, R.drawable.card_hear_1,  R.drawable.card_hear_2,
				R.drawable.card_hear_3, R.drawable.card_hear_4, R.drawable.card_hear_5, R.drawable.card_hear_6,
				R.drawable.card_hear_7, R.drawable.card_hear_8, R.drawable.card_hear_9, R.drawable.card_hear_10, 
				R.drawable.card_hear_j, R.drawable.card_hear_q, R.drawable.card_hear_k, R.drawable.card_spad_1, 
				R.drawable.card_spad_2, R.drawable.card_spad_3, R.drawable.card_spad_4, R.drawable.card_spad_5, 
				R.drawable.card_spad_6, R.drawable.card_spad_7, R.drawable.card_spad_8, R.drawable.card_spad_9, 
				R.drawable.card_spad_10, R.drawable.card_spad_j, R.drawable.card_spad_q, R.drawable.card_spad_k};
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		Random random = new Random();
		for(int i = 0; i < maxCards; i++){
			int j = random.nextInt(ids.length);
			tmp.add(ids[j]);
		}
		return tmp;
	}

	public static boolean checkCard(int i, ArrayList<Integer> seen, boolean result) {
		if(result)
			return checkCard(i, seen);
		else
			return !checkCard(i, seen);
	}

	private static boolean checkCard(int i, ArrayList<Integer> seen) {
		return seen.contains(i);
	}

}
