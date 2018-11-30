package com.awn.app.movietoday.download;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.awn.app.movietoday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {
    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 101;
    private static String DOWNLOAD_DIRECTORY = "";
    private static final String TAG = "Download Task";

    private Context context;
    private Button buttonText;
    private String downloadUrl, downloadFileName, movieTitle;

    private SharedPreferences sharedPreferences;

    public DownloadTask(Context context, Button buttonText, String urlPoster, String title) {
        this.context = context;
        this.buttonText = buttonText;
        this.movieTitle = title;
        this.downloadUrl = "http://image.tmdb.org/t/p/w342" + urlPoster;
        this.downloadFileName = "Poster" + title.replace(" ", "") + ".jpg";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        DOWNLOAD_DIRECTORY = sharedPreferences.getString(context.getString(R.string.key_gallery_name), DOWNLOAD_DIRECTORY);
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Integer, Void> {

        File downloadDirectory = null;
        File downloadFile = null;

        boolean isAlreadyDownloaded = false;

        NotificationManagerCompat notificationManager;
        NotificationCompat.Builder mBuilder;

        int PROGRESS_MAX = 100;
        int PROGRESS_INIT = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonText.setEnabled(false);
            buttonText.setText("Download Start");

            notificationManager = NotificationManagerCompat.from(context);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            mBuilder.setContentTitle("Poster " + movieTitle)
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.camera)
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            mBuilder.setProgress(PROGRESS_MAX, PROGRESS_INIT, false);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

        @Override
        protected Void doInBackground(Void... args) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int downloadFileSize = httpURLConnection.getContentLength();

                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Server returned HTTP " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
                }

                downloadDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + DOWNLOAD_DIRECTORY);

                if (!downloadDirectory.exists()) {
                    downloadDirectory.mkdirs();
                    Log.d(TAG, "Directory Created. " + downloadDirectory.getPath());
                }

                downloadFile = new File(downloadDirectory, downloadFileName);

                if (!downloadFile.exists()) {
                    downloadFile.createNewFile();
                    Log.d(TAG, "File Created" + downloadFile.getPath());

                    FileOutputStream fos = new FileOutputStream(downloadFile);

                    InputStream is = httpURLConnection.getInputStream();

                    byte[] buffer = new byte[1024];
                    int length = 0;
                    long total = 0;
                    while ((length = is.read(buffer)) != -1) {
                        total += length;
                        fos.write(buffer, 0, length);

                        Thread.sleep(1000);

                        publishProgress((int) ((total * 100) / downloadFileSize));
                    }

                    fos.close();
                    is.close();
                } else {
                    isAlreadyDownloaded = true;

                }

            } catch (Exception e) {
                e.printStackTrace();
                downloadFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mBuilder.setProgress(PROGRESS_MAX, progress[0], false);
            mBuilder.setContentText("Download in progress " + progress[0] + "%");
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (isAlreadyDownloaded) {
                    buttonText.setEnabled(true);
                    buttonText.setText("Download Complete");
                    Toast.makeText(context, "The file has already been downloaded", Toast.LENGTH_LONG).show();
                } else {
                    if (downloadFile != null) {
                        buttonText.setEnabled(true);
                        buttonText.setText("Download Complete");

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + DownloadTask.DOWNLOAD_DIRECTORY);

                        intent.setDataAndType(uri, "file/*");

                        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentText("Download complete")
                                .setProgress(PROGRESS_INIT, PROGRESS_INIT, false)
                                .setContentIntent(notificationPendingIntent);
                        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                    } else {
                        buttonText.setText("Download Failed");

                        mBuilder.setContentText("Download failed")
                                .setProgress(PROGRESS_INIT, PROGRESS_INIT, false);
                        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buttonText.setEnabled(true);
                                buttonText.setText("Download Again");
                            }
                        }, 3000);
                        Log.e(TAG, "Download Failed");

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                buttonText.setText("Download Failed");

                mBuilder.setContentText("Download failed")
                        .setProgress(PROGRESS_INIT, PROGRESS_INIT, false);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonText.setEnabled(true);
                        buttonText.setText("Download Again");
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
            }

            super.onPostExecute(result);
        }
    }
}
