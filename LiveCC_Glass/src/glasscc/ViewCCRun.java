package glasscc;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.glasscc.basis.R;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;


public class ViewCCRun extends Service 
{
	private TimelineManager mTimelineManager;
	private LiveCard mLiveCard;
	private String cardID = "MemoGlassLiveCard";
	private String cardText = "";
	private List<String> pastConvAgr;
	private boolean update;
	@Override
	public void onCreate() 
	{
		super.onCreate();
		mTimelineManager = TimelineManager.from(this);
		if (Utils.checkForObjectInSharedPrefs(this,getString(R.string.shared_memo_key)))
		{
			pastConvAgr = new ArrayList<String>(Utils.getStringArrayPref(this, getString(R.string.shared_memo_key)));
		}
		else
		{
			pastConvAgr = new ArrayList<String>();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{	
		update = intent.getBooleanExtra("update", false);

		if (mLiveCard ==  null)
		{

			mLiveCard = mTimelineManager.createLiveCard(cardID);
			RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.card_layout);

			if (pastConvAgr.size() > 0)
			{

				for (int i = 0 ; i < pastConvAgr.size() && i < 5; i++)
				{
					cardText += "" + (i+1) + ") " + pastConvAgr.get(i) + "\n";
				}
 
				remoteViews.setTextViewText(R.id.memo_list_text_view , cardText);
				remoteViews.setTextViewText(R.id.no_memos_text_view, "");
			}

			else 
			{
		
				remoteViews.setTextViewText(R.id.memo_list_text_view , "");
				remoteViews.setTextViewText(R.id.no_memos_text_view, "No memos!");
			}
	
			mLiveCard.setViews(remoteViews);

			Intent menu;

			if (pastConvAgr.size() == 0)
			{
				menu = new Intent(this, ViewCCs.class);
			}

			else 
			{
				menu = new Intent(this, ViewCCMenu.class);
			}
			mLiveCard.setAction(PendingIntent.getActivity(this, 0, menu, 0));

			if (update)
			{
				mLiveCard.publish(LiveCard.PublishMode.SILENT);
			}
			else
			{
				mLiveCard.publish(LiveCard.PublishMode.REVEAL);
			}
		}

		else 
		{
			
			mLiveCard.unpublish();
			mLiveCard.publish(LiveCard.PublishMode.REVEAL);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{

		if (mLiveCard != null && mLiveCard.isPublished()) 
		{
			mLiveCard.unpublish();
			mLiveCard = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
}