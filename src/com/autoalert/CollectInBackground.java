package com.autoalert;

import android.util.Log;

public class CollectInBackground implements Runnable {
        public void run() {
        	for(int i = 0; i < 10; i++) {
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		Log.d("Testing", "another one in background");
        	}
        }
}
