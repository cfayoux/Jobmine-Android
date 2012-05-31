package com.jobmine.interview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobmine.R;
import com.jobmine.Activity.BindingActivity;
import com.jobmine.Activity.JobDetailsActivity;
import com.jobmine.Activity.MainActivity;
import com.jobmine.Activity.MainActivity.getData;
import com.jobmine.common.Constants;
import com.jobmine.common.JobmineNetworkRequest;
import com.jobmine.models.Interview;
import com.jobmine.models.Job;
import com.jobmine.providers.JobmineProvider;
import com.jobmine.service.JobmineAlarmManager;
import com.jobmine.service.JobmineInterface;

public class InterviewActivity extends BindingActivity {
	InterviewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobinterview);
		adapter = new InterviewAdapter(getApplicationContext());
		ListView lv = (ListView) this.findViewById(R.id.interview_list);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		getTask(false).execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_interview, menu);
		return true;
	}
	
	
	private AsyncTask<Void, Void, List<Interview>> getTask(final boolean isForced){
		return new AsyncTask<Void, Void, List<Interview>>() {
			private ProgressDialog dialog = null;
			
			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(InterviewActivity.this);
				dialog = ProgressDialog.show(InterviewActivity.this, "", "Loading...", true, false);
				dialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.cancel();
					}
				});
				dialog.show();
			}
			
			@Override
			protected List<Interview> doInBackground(Void... params) {
				JobmineInterface jobmineInterface = getServiceinterface();
				List<Interview> interviews = new ArrayList<Interview>();
				
				try {
					jobmineInterface.getInterviews(isForced);
					interviews = JobmineProvider.getInterviews(getContentResolver());
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				return interviews;
			}
			
			@Override
			protected void onPostExecute(List<Interview> interviews) {
				dialog.dismiss();
				if (interviews != null) {
					adapter.setContent(interviews);
				} else {
					Toast.makeText(InterviewActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
				}
				
			};

		};
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.refresh:
			getTask(true).execute();
			break;
		case R.id.applications:
			Intent intent = new Intent(InterviewActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}
