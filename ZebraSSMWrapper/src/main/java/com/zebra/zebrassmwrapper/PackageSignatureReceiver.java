package com.zebra.zebrassmwrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

public class PackageSignatureReceiver {
    private static String TAG = "DWStatusScanner";
    private Context mContext;
    private packageSignatureBroadcastReceiver mStatusBroadcastReceiver = null;
    private Handler broadcastReceiverHandler = null;
    private HandlerThread broadcastReceiverThread = null;
    private Looper broadcastReceiverThreadLooper = null;

    private boolean mUseSeparateThread = false;

    public interface ISignatureReceiverObserver
    {
        void onSignatureReceived(String packageName, String signature, String destinationPublicKey);
        void onSignatureDestinationAcknowledgeReceived(String destinationPackageName, String destinationSignature, String destinationPublicKey);
    }

    private ISignatureReceiverObserver iSignatureReceiverObserver;

    public PackageSignatureReceiver(Context aContext, ISignatureReceiverObserver iSignatureReceiverObserver) {
        mContext = aContext;
        mStatusBroadcastReceiver = new packageSignatureBroadcastReceiver();
        this.iSignatureReceiverObserver = iSignatureReceiverObserver;
        mUseSeparateThread = false;
    }

    public PackageSignatureReceiver(Context aContext, boolean useSeparateThread, ISignatureReceiverObserver iSignatureReceiverObserver) {
        mContext = aContext;
        mStatusBroadcastReceiver = new packageSignatureBroadcastReceiver();
        this.iSignatureReceiverObserver = iSignatureReceiverObserver;
        mUseSeparateThread = useSeparateThread;
    }

    public void start()
    {
        Log.d(TAG, "Start Status Scanner Receiver");
        /*
        Register notification broadcast receiver
         */
        registerNotificationReceiver();


    }

    public void stop()
    {
        Log.d(TAG, "Stop Status Scanner Receiver");
        unRegisterNotificationReceiver();
    }

    protected class packageSignatureBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.ACTION_SIGNATURE_EXCHANGE)){
                // handle notification
                Bundle extras = intent.getExtras();
                if(extras != null && !extras.isEmpty()) {
                    String packageName = extras.getString(Constants.EXTRA_SIGNATURE_EXCHANGE_PACKAGE_NAME);
                    String signature = extras.getString(Constants.EXTRA_SIGNATURE_EXCHANGE_SIGNATURE);
                    String publicKey = extras.getString(Constants.EXTRA_SIGNATURE_EXCHANGE_PUBLIC_KEY);
                    if(publicKey != null)
                    {
                        //Decrypt signature before sending it to the Observer
                    }
                    if(iSignatureReceiverObserver != null)
                    {
                        iSignatureReceiverObserver.onSignatureReceived(packageName, signature, publicKey);
                    }
                }
            }
            else if(action.equals(Constants.ACTION_SIGNATURE_DESTINATION_ACKNOWLEDGE_RECEIVED)){
                // handle notification
                Bundle extras = intent.getExtras();
                if(extras != null && !extras.isEmpty()) {
                    String packageName = extras.getString(Constants.EXTRA_SIGNATURE_EXCHANGE_PACKAGE_NAME);
                    String signature = extras.getString(Constants.EXTRA_SIGNATURE_EXCHANGE_SIGNATURE);
                    String publicKey = extras.getString(Constants.EXTRA_SIGNATURE_EXCHANGE_PUBLIC_KEY);
                    if(publicKey != null)
                    {
                        //Decrypt signature before sending it to the Observer
                    }
                    if(iSignatureReceiverObserver != null)
                    {
                        iSignatureReceiverObserver.onSignatureDestinationAcknowledgeReceived(packageName, signature, publicKey);
                    }
                }
            }
        }
    };


    void registerNotificationReceiver() {
        if(mUseSeparateThread) {
            // Ensure that no thread was left running
            QuitReceiverThreadNicely();

            Log.d(TAG, "registerNotificationReceiver()");
            broadcastReceiverThread = new HandlerThread(mContext.getPackageName() + ".NOTIFICATION.THREAD");//Create a thread for BroadcastReceiver

            broadcastReceiverThread.start();

            broadcastReceiverThreadLooper = broadcastReceiverThread.getLooper();
            broadcastReceiverHandler = new Handler(broadcastReceiverThreadLooper);

            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_FILE_DEPLOYMENT_NOTIFICATION);
            mContext.registerReceiver(mStatusBroadcastReceiver, filter, null, broadcastReceiverHandler);
        }
        else
        {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_FILE_DEPLOYMENT_NOTIFICATION);
            mContext.registerReceiver(mStatusBroadcastReceiver, filter);
        }
    }

    void unRegisterNotificationReceiver() {
        if(mUseSeparateThread) {
            //to unregister the broadcast receiver
            try {
                mContext.unregisterReceiver(mStatusBroadcastReceiver); //Android method
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "registerNotificationReceiver(): Trying to unregister a receiver that has not been previously released..");
                Log.d(TAG, "registerNotificationReceiver(): Status receiver should be started before trying to stop it.");
                //e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            QuitReceiverThreadNicely();
        }
        else
        {
            try {
                mContext.unregisterReceiver(mStatusBroadcastReceiver); //Android method
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "registerNotificationReceiver(): Trying to unregister a receiver that has not been previously released..");
                Log.d(TAG, "registerNotificationReceiver(): Status receiver should be started before trying to stop it.");
                //e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void QuitReceiverThreadNicely() {
        Log.d(TAG, "QuitReceiverThreadNicely()");
        if(broadcastReceiverHandler != null)
        {
            {
                try {
                    Log.d(TAG, "QuitReceiverThreadNicely():broadcastReceiverHandler.removeCallbacksAndMessages(null)");
                    broadcastReceiverHandler.removeCallbacksAndMessages(null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                broadcastReceiverHandler = null;
            }

            if(broadcastReceiverThreadLooper != null)
            {
                try {
                    Log.d(TAG, "QuitReceiverThreadNicely():broadcastReceiverThreadLooper.quit()");
                    broadcastReceiverThreadLooper.quit();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                broadcastReceiverThreadLooper = null;
            }

            if(broadcastReceiverThread != null)
            {
                try {
                    Log.d(TAG, "QuitReceiverThreadNicely():broadcastReceiverThread.quit()");
                    broadcastReceiverThread.quit();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                broadcastReceiverThread = null;
            }
        }
    }
}
