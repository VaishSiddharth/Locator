package com.example.siddharthvaish.locator;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {

    public UploadWorker(
        @NonNull Context context,
        @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public ListenableWorker.Result doWork() {
      // Do the work here--in this case, upload the images.
        //getApplicationContext().startService(new Intent(getApplicationContext(), LocationUpdateService.class));

      // Indicate whether the task finished successfully with the Result
      return ListenableWorker.Result.success();
    }
}