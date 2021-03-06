package com.jobmine.adapters;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobmine.R;
import com.jobmine.Activity.BindingActivity;
import com.jobmine.common.Common;
import com.jobmine.common.Logger;
import com.jobmine.models.Interview;

public class InterviewAdapter extends BaseAdapter {
	private static final int INTERVIEW_TYPE = 0;
	private static final int HEADER_TYPE = 1;
	List<Interview> normalInterview = new ArrayList<Interview>();
	List<Interview> groupInterview = new ArrayList<Interview>();
	BindingActivity context;

	public InterviewAdapter(BindingActivity context) {
		this.context = context;
	}

	@Override
	public Interview getItem(int position) {
		if (position > 0 && position < (normalInterview.size() + 1)) {
			return normalInterview.get(position - 1);
		} else if (position > normalInterview.size() + 1) {
			return groupInterview.get(position - normalInterview.size() - 2);
		} else {
			return null;
		}
	}

	@Override
	public int getCount() {
		return normalInterview.size() + (normalInterview.size() == 0 ? 0 : 1) + groupInterview.size() + (groupInterview.size() == 0 ? 0 : 1);
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0 || position == (normalInterview.size() + 1)) {
			return HEADER_TYPE;
		} else {
			return INTERVIEW_TYPE;
		}

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		View v = convertView;
		if (getItemViewType(position) == INTERVIEW_TYPE) {
			Interview i = getItem(position);
			if (convertView == null) {
				v = LayoutInflater.from(context).inflate(R.layout.interview_entry, null);
			}
			TextView employer = (TextView) v.findViewById(R.id.interview_employer);
			TextView title = (TextView) v.findViewById(R.id.interview_title);
			TextView time = (TextView) v.findViewById(R.id.interview_time);
			TextView date = (TextView) v.findViewById(R.id.interview_date);
			TextView length = (TextView) v.findViewById(R.id.interview_length);
			TextView room = (TextView) v.findViewById(R.id.interview_room);
			TextView type = (TextView) v.findViewById(R.id.interview_type);
			TextView instructions = (TextView) v.findViewById(R.id.interview_instructions);

			title.setText(i.title);
			time.setText(Common.trim(i.time).isEmpty() ? "??:??" : i.time);
			date.setText(i.date);
			room.setText(i.room);
			length.setText(i.length + " Minutes");
			type.setText(i.type);
			instructions.setText(i.instructions);
			employer.setText(i.employerName);

			type.setVisibility(View.GONE);
			instructions.setVisibility(View.GONE);

			View sideColour = v.findViewById(R.id.side_tab);

			long nowTime = System.currentTimeMillis() / 1000;
			if (i.unixTime == -1) {
				sideColour.setBackgroundColor(Color.GRAY);
			} else if (nowTime - i.unixTime > 0) {
				sideColour.setBackgroundColor(0xFFF4BABA); // red
			} else {
				sideColour.setBackgroundColor(0xFFA3F57F); // green
			}

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View clicked) {
					TextView type = (TextView) clicked.findViewById(R.id.interview_type);
					TextView instructions = (TextView) clicked.findViewById(R.id.interview_instructions);
					final ListView p = (ListView) parent;

					Logger.d("last " + p.getLastVisiblePosition() + "pos " + position);

					context.delayedRunOnUIThread(new Runnable() {

						@Override
						public void run() {
							if (p.getLastVisiblePosition() == position) {
								p.smoothScrollToPosition(position);
							}
						}
					}, 10);
					if (type.getVisibility() == View.GONE) {
						type.setVisibility(View.VISIBLE);

						if (instructions.getText().toString().trim().length() > 3) {
							instructions.setVisibility(View.VISIBLE);
						}
					} else {
						type.setVisibility(View.GONE);
						instructions.setVisibility(View.GONE);
					}
				}
			});

		} else {
			if (convertView == null) {
				v = LayoutInflater.from(context).inflate(R.layout.interview_header, null);
			}
			if (position == 0) {
				((TextView) v).setText("Interviews");
			} else if (position == normalInterview.size() + 1) {
				((TextView) v).setText("Group Interviews");
			}

		}

		return v;

	}

	public void setContent(List<Interview> interviews) {
		groupInterview.clear();
		normalInterview.clear();
		for (Interview interview : interviews) {
			if (interview.type.equals("Group")) {
				groupInterview.add(interview);
			} else {
				this.normalInterview.add(interview);
			}
		}
		notifyDataSetChanged();
	}

}
