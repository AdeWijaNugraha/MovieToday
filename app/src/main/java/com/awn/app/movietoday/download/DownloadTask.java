package com.awn.app.movietoday.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.awn.app.movietoday.DetailActivity;
import com.awn.app.movietoday.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask {

    public static final String CHANNEL_ID = "channel";

    public static final String DOWNLOAD_DIRECTORY = "MovieToday";
    private static final String TAG = "Download Task";
    private Context context;
    private Button buttonText;
    private String downloadUrl = "", downloadFileName = "";

    public DownloadTask(Context context, Button buttonText, String downloadUrl, String title) {
        this.context = context;
        this.buttonText = buttonText;
        this.downloadUrl = "http://image.tmdb.org/t/p/w342" + downloadUrl;

        downloadFileName = "Poster " + title;//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, String, Void> {

        File apkStorage = null;
        File outputFile = null;

        NotificationManagerCompat notificationManager;
        NotificationCompat.Builder mBuilder;

        int PROGRESS_MAX = 100;
        int PROGRESS_INIT = 0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonText.setEnabled(false);
            buttonText.setText("Download Start");//Set Button Text when download started


            notificationManager = NotificationManagerCompat.from(context);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            mBuilder.setContentTitle("Poster Download")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.ic_movie)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            // Issue the initial notification with zero progress

            mBuilder.setProgress(PROGRESS_MAX, PROGRESS_INIT, false);
            notificationManager.notify(101, mBuilder.build());
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage

            mBuilder.setProgress(PROGRESS_MAX, Integer.parseInt(progress[0]), false);
            notificationManager.notify(101, mBuilder.build());



// Do the job here that tracks the progress.
// Usually, this should be in a
// worker thread
// To show progress, update PROGRESS_CURRENT and update the notification with:
// mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
// notificationManager.notify(notificationId, mBuilder.build());


            Log.e(TAG, "onProgressUpdate: "+ Integer.parseInt(progress[0]));
        }

        @Override
        protected Void doInBackground(Void... args) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection
                int size = c.getContentLength();

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + DOWNLOAD_DIRECTORY);
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created. "+apkStorage.getPath());
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created" + outputFile.getPath());
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    fos.write(buffer, 0, len1);//Write new file

                    Thread.sleep(1000);

                    publishProgress(""+(int)((total*100)/size));
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    buttonText.setEnabled(true);
                    buttonText.setText("Download Complete");//If Download completed then change button text

                    // When done, update the notification one more time to remove the progress bar
                    mBuilder.setContentText("Download complete")
                            .setProgress(0,0,false);
                    notificationManager.notify(101, mBuilder.build());

                    openDownloadedFolder();
                } else {
                    buttonText.setText("Download Failed");//If download failed change button text
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttonText.setEnabled(true);
                            buttonText.setText("Download Again");//Change button text again after 3sec
                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
                buttonText.setText("Download Failed");
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

        //Open downloaded folder
        private void openDownloadedFolder() {
            //First check if SD Card is present or not
            if (new CheckForSDCard().isSDCardPresent()) {

                //Get Download Directory File
                File apkStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + DownloadTask.DOWNLOAD_DIRECTORY);

                //If file is not present then display Toast
                if (!apkStorage.exists())

                    Toast.makeText(context, "Right now there is no directory. Please download some file first.", Toast.LENGTH_SHORT).show();

                else {

                    //If directory is present Open Folder

                    /** Note: Directory will open only if there is a app to open directory like File Manager, etc.  **/

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                            + "/" + DownloadTask.DOWNLOAD_DIRECTORY);
                    intent.setDataAndType(uri, "file/*");
                    context.startActivity(Intent.createChooser(intent, "Open Download Folder"));
                }

            } else
                Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

        }
    }
}
