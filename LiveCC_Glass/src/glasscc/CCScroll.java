
package glasscc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.glasscc.basis.R;
import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

public class CCScroll extends Activity 
{
	private GestureDetector openMenuGesture;
	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	private ArrayList<String> pastConvo;
	private GlassCCCard adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		createCards();
		openMenuGesture = createGestureDetector(this);
		mCardScrollView = new CardScrollView(this);
		adapter = new GlassCCCard(mCards);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
	}

	@Override
	public void onResume() 
	{
		super.onResume();

		if (Utils.checkForObjectInSharedPrefs(this,getString(R.string.shared_memo_key)))
		{
			pastConvo = new ArrayList<String>(Utils.getStringArrayPref(this, getString(R.string.shared_memo_key)));
		}
		else
		{
			pastConvo = new ArrayList<String>();
		}
	 
		if (adapter.getCount() > pastConvo.size())
		{
			 
			adapter.removeMemo(mCardScrollView.getSelectedItemPosition());
			stopService(new Intent(this , ViewCCRun.class));
			Intent serviceIntent = new Intent(this , ViewCCRun.class);
			serviceIntent.putExtra("update" , true);
			startService(serviceIntent);
		}
		if (pastConvo.size() == 0)
		{
			finish();
		}

		adapter.notifyDataSetChanged();
	}
	private void createCards() 
	{
		mCards = new ArrayList<Card>();
		
		//Get the saved list of memos from shared preferences 
		if (Utils.checkForObjectInSharedPrefs(this,getString(R.string.shared_memo_key)))
		{
			pastConvo = new ArrayList<String>(Utils.getStringArrayPref(this, getString(R.string.shared_memo_key)));
		}
		else
		{
			pastConvo = new ArrayList<String>();
		}

		Card tempMemoCard;
		for (int i = 0 ; i < pastConvo.size() ; i++)
		{
			tempMemoCard = new Card(this);
			tempMemoCard.setText(pastConvo.get(i));
			tempMemoCard.setFootnote("Conversation " + (i+1) + " of " +  pastConvo.size());
			mCards.add(tempMemoCard);
		}
	}

	private GestureDetector createGestureDetector(final Context context)
	{
		GestureDetector gestureDetector = new GestureDetector(context);
		//Create a base listener for generic gestures
		gestureDetector.setBaseListener( new GestureDetector.BaseListener() 
		{
			@Override
			public boolean onGesture(Gesture gesture) 
			{
				if (gesture == Gesture.LONG_PRESS) 
				{
					Intent memoScrollMenuIntent = new Intent(context , CCScrollOptions.class); 
					memoScrollMenuIntent.putExtra("scrollposition", mCardScrollView.getSelectedItemPosition())
										.putExtra("memotext" , pastConvo.get(mCardScrollView.getSelectedItemPosition()));
					startActivity(memoScrollMenuIntent);
					return true;
				}
				return false;
			}
		});
		return gestureDetector;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) 
	{
		if (openMenuGesture != null) 
		{
			return openMenuGesture.onMotionEvent(event);
		}
		return false;
	}

}