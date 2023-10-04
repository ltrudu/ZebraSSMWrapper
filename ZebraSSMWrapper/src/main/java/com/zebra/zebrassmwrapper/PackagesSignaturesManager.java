package com.zebra.zebrassmwrapper;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class PackagesSignaturesManager implements PackageSignatureReceiver.ISignatureReceiverObserver {

    private Map<String, Pair<String, String>> packagesSignatures = null;
    private Context context = null;

    private PackageSignatureReceiver packageSignatureReceiver = null;


    public PackagesSignaturesManager(Context context)
    {
        this.packagesSignatures = new HashMap<>();
        this.context = context;
        this.packageSignatureReceiver = new PackageSignatureReceiver(context, this);
    }

    public void start()
    {
        if(packageSignatureReceiver != null)
        {
            packageSignatureReceiver.start();
        }
    }

    public void stop()
    {
        if(packageSignatureReceiver != null)
        {
            packageSignatureReceiver.stop();
        }
    }

    @Override
    public void onSignatureReceived(String packageName, String signature, String publicKey) {
        if(packagesSignatures.containsKey(packageName))
        {
            packagesSignatures.replace(packageName, new Pair<>(signature, publicKey));
        }
        else
        {
            packagesSignatures.put(packageName, new Pair<>(signature, publicKey));
        }
        // Tell the destination we have received the signature
        // and send our signature at the same time
        broadcastSignatureAcknowledgeReceived();
    }

    @Override
    public void onSignatureDestinationAcknowledgeReceived(String destinationPackageName, String destinationSignature, String destinationPublicKey) {
        // This means that the destination has received our key
        if(packagesSignatures.containsKey(destinationPackageName))
        {
            packagesSignatures.replace(destinationPackageName, new Pair<>(destinationSignature, destinationPublicKey));
        }
        else
        {
            packagesSignatures.put(destinationPackageName, new Pair<>(destinationSignature, destinationPublicKey));
        }
    }

    public void broadcastCurrentSignature()
    {
        String currentSignature = PackageManagementHelper.getSignature(context, null);
        if(currentSignature != null) {
            Intent intent = new Intent(Constants.ACTION_SIGNATURE_EXCHANGE);
            intent.putExtra(Constants.EXTRA_SIGNATURE_EXCHANGE_PACKAGE_NAME, context.getPackageName());
            intent.putExtra(Constants.EXTRA_SIGNATURE_EXCHANGE_SIGNATURE, currentSignature);
            intent.putExtra(Constants.EXTRA_SIGNATURE_EXCHANGE_PUBLIC_KEY, "mypublickey");
            context.sendBroadcast(intent);
        }
    }

    private void broadcastSignatureAcknowledgeReceived()
    {
        String currentSignature = PackageManagementHelper.getSignature(context, null);
        Intent intent = new Intent(Constants.ACTION_SIGNATURE_DESTINATION_ACKNOWLEDGE_RECEIVED);
        // When we receive a signature, we just send our as an answer.
        intent.putExtra(Constants.EXTRA_SIGNATURE_EXCHANGE_PACKAGE_NAME, context.getPackageName());
        intent.putExtra(Constants.EXTRA_SIGNATURE_EXCHANGE_SIGNATURE, currentSignature);
        intent.putExtra(Constants.EXTRA_SIGNATURE_EXCHANGE_PUBLIC_KEY, "mypublickey");
        context.sendBroadcast(intent);
    }
}
