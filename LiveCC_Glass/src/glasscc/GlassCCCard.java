package glasscc;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

class GlassCCCard extends CardScrollAdapter
{
	private List<Card> mCards;
	
	public GlassCCCard(List<Card> cards) 
	{
		super();
		this.mCards = cards;
	}
	
	@Override
	public int findIdPosition(Object id) 
	{
		return -1;
	}

	@Override
	public int findItemPosition(Object item) 
	{
		return mCards.indexOf(item);
	}

	@Override
	public int getCount() 
	{
		return mCards.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return mCards.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		return mCards.get(position).toView();
	} 
	
	public void removeMemo(int position)
	{
		mCards.remove(position) ;
		updateMemoNumbers() ;
		notifyDataSetChanged() ;
	}

	public void updateMemoNumbers()
	{
		for (int i  = 0 ; i < mCards.size() ; i++)
		{
			mCards.get(i).setFootnote("Conversation " + (i+1) + " of " +  mCards.size());
		}
	}
}