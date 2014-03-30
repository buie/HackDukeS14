package glasscc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.glass.widget.CardScrollView;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils 
{
	public static CardScrollView cardScroll;

	public static boolean checkForObjectInSharedPrefs(final Context context, final String key)
	{
		return  PreferenceManager.getDefaultSharedPreferences(context).contains(key);
	}

	public static void commitNewMemoList(final Context context, final String key, final List<String> memoList) 
	{
		SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

		if (! memoList.isEmpty()) 
		{
			sharedPrefEditor.putString(key, new JSONArray(memoList).toString());
		} 
		else 
		{
			sharedPrefEditor.putString(key, null);
		}

		sharedPrefEditor.commit();
	}

	public static ArrayList<String> getStringArrayPref(final Context context, String key) 
	{

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String json = sharedPref.getString(key, null);
		ArrayList<String> conversationIndex = new ArrayList<String>();

		if (json != null) 
		{
			try 
			{
				JSONArray a = new JSONArray(json);

				for (int i = 0; i < a.length(); i++) 
				{
					conversationIndex.add(a.optString(i));
				}
			} catch (JSONException e) { }
		}

		return conversationIndex;
	}

	public static void deleteMemoAtIndex(int index , Context context , String key)
	{
		ArrayList<String> memoList = new ArrayList<String>(getStringArrayPref(context , key));
		memoList.remove(index);
		commitNewMemoList(context , key , memoList);
	}

}