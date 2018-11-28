package com.example.patryyyk21.test_aplikacji;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends Activity  {

    public TextView tvInfo;
    public ProgressBar progress;
    public int zmienna = 0, i = 0, maks = 20;
    private AsyncTaskTest test;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = (TextView)findViewById(R.id.tv_progrseInfo);
        progress = (ProgressBar)findViewById(R.id.progressBar);
        Button bStart = (Button)findViewById(R.id.b_start);
        Button bCancel = (Button)findViewById(R.id.b_cancel);
        Button bStartThread = (Button)findViewById(R.id.b_startThread);

        test = (AsyncTaskTest)getLastNonConfigurationInstance();
        if(test == null) {
            test = new AsyncTaskTest();
        }
        test.connetcActivity(this);

        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setMax(maks);

                if (test.getStatus().equals(AsyncTask.Status.RUNNING)){
                    Toast.makeText(getApplicationContext(), "Wątek pracuje w tle", Toast.LENGTH_SHORT).show();
                }
                else{
                    test = new AsyncTaskTest();
                    test.execute(maks);
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test.cancel(true);
            }
        });

        bStartThread.setOnClickListener(new View.OnClickListener() {
            // rozwiązania dorazine praca w tle
            @Override
            public void onClick(View view) {
                final int f = 20;
                progress.setMax(f);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            for ( i = 0; i<=f; i++){
                                progress.setProgress(i);
                                zmienna++;
                                tvInfo.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvInfo.setText(i+" / " +f);
                                    }
                                });
                                Thread.sleep(1000);
                            }
                        }catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(getApplicationContext(), zmienna+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        test.disconnetcActivity();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return test;
    }

    private class AsyncTaskTest extends AsyncTask<Integer, Integer, Integer> {

        Activity activity;

        public void connetcActivity(Activity context){
            this.activity = context;
            progress = (ProgressBar)activity.findViewById(R.id.progressBar);
            tvInfo = (TextView)activity.findViewById(R.id.tv_progrseInfo);
            progress.setMax(maks);

        }

        public void disconnetcActivity(){
            this.activity = null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setProgress(0);
            Toast.makeText(getApplicationContext(), "Rozpoczęcie pracy", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int pro = integers[0];
            for(int i = 0; i<=pro; i++){
                try{
                    if(isCancelled()){
                        return 0;
                    }
                    Thread.sleep(1000);
                    publishProgress(i, pro);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress.setProgress(values[0]);
            tvInfo.setText(values[0] + " / "+ values[1]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                Toast.makeText(getApplicationContext(),"Zakończenie pracy",Toast.LENGTH_SHORT).show();
                tvInfo.setText("Zakończenie pracy");
            }
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
            Toast.makeText(getApplicationContext(),"zakończenie wątka", Toast.LENGTH_SHORT).show();
            progress.setProgress(0);
        }
    }
}