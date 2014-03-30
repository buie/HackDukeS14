package glasscc;

import java.util.ArrayList;

import com.glasscc.basis.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;


public class CCGenerator extends Activity 
{
	private static final int SPEECH_REQUEST = 0;
	private String convoGen;
	private ArrayList<String> convoNam;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		displaySpeechRecognizer(); 
	}

	private void displaySpeechRecognizer() 
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data) 
	{
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) 
		{
		
			convoGen = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
	
			if (Utils.checkForObjectInSharedPrefs(this,getString(R.string.shared_memo_key)))
			{
				convoNam = new ArrayList<String>(Utils.getStringArrayPref(this, getString(R.string.shared_memo_key)));
			}
			else
			{
				convoNam = new ArrayList<String>();
			}
	  
			convoNam.add(convoGen);
	
			Utils.commitNewMemoList(this, getString(R.string.shared_memo_key), convoNam);

			stopService(new Intent(this , ViewCCRun.class));
			Intent serviceIntent = new Intent(this , ViewCCRun.class);
			serviceIntent.putExtra("update" , true);
			startService(serviceIntent);
		}
		super.onActivityResult(requestCode, resultCode, data);
		finish() ;
	}

	@Override
	public void onResume() 
	{
		super.onResume();
	}
}