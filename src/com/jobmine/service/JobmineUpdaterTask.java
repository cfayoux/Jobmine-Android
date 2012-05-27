package com.jobmine.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.jobmine.providers.JobmineContentProvider;
import com.someguy.jobmine.Common;
import com.someguy.jobmine.Job;

public class JobmineUpdaterTask implements Runnable {

	private JobmineService service = null;
	
	public JobmineUpdaterTask(JobmineService context) {
		this.service = context;
	}
	
	@Override
	public void run() {
		JobmineNotificationManager.showUpdatingNotification(service);
		
		//Get current jobs and new jobs
		HashMap<Integer, Job> oldJobsMap = JobmineContentProvider.getApplications(service.getContentResolver());
		ArrayList<Job> newJobs = Common.getJobmine(service);
		
		if (newJobs != null && oldJobsMap != null && newJobs.size() > 0) {
			
			int newJobCount = 0;

			//Compare all jobs
			for (Job j : newJobs) {
				//Logger.d ("Got new job: " + j.job + ", Employer: " + j.emplyer + ", ID: " + j.id);
				
				try {
					//Only check if the job existed in the old data as well
					if (oldJobsMap.containsKey(Integer.parseInt(j.id))) {
						Job old = oldJobsMap.get(Integer.parseInt(j.id));
						
						//We went from applied to selected or scheduled
						if (old.appStatus.equals("Applied") && 
								(j.appStatus.equals("Selected") || j.appStatus.equals("Scheduled"))) {
							newJobCount++;
							
							if (newJobCount == 1) {
								JobmineNotificationManager.showSingleInterviewNotification(service, j.emplyer, j.title);
							} else {
								JobmineNotificationManager.showMultipleInterviewNotification(service, newJobCount);
							}
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		//Replace provider with new data
		if (newJobs != null && newJobs.size() > 0) {
			JobmineContentProvider.deleteAll(service.getContentResolver());
			JobmineContentProvider.addApplications(newJobs, service.getContentResolver());
		}
		
		//Cancel the current notification and stop the service
		JobmineNotificationManager.cancelUpdatingNotification(service);
		service.stopSelf();
	}

}