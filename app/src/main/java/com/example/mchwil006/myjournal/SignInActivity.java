package com.example.mchwil006.myjournal;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, OnClickListener {

    private static final String TAG = "SignInTestActivity";

    // A magic number we will use to know that our sign-in error
    // resolution activity has completed.
    private static final int OUR_REQUEST_CODE = 49404;

    // The core Google Play Services client.
    public GoogleApiClient mGoogleApiClient;

    // A progress dialog to display when the user is connecting in
    // case there is a delay in any of the dialogs being ready.
    private ProgressDialog mConnectionProgressDialog;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // First we need to configure the Google Sign In API to ensure we are retrieving
        // the server authentication code as well as authenticating the client locally.
        //String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // We pass through three "this" arguments to the builder, specifying the:
        // 1. Context
        // 2. Object to use for resolving connection errors
        // 3. Object to call onConnectionFailed on
        // We also add the Google Sign in API we previously created.
        mGoogleApiClient = new GoogleApiClient.Builder(this /* Context */)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Connect our sign in, sign out and disconnect buttons.
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.revoke_access_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);


        // Configure the ProgressDialog that will be shown if there is a
        // delay in presenting the user with the next sign in step.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
    }


    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly. We can try and retrieve an
            // authentication code.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently. Cross-device
            // single sign-on will occur in this branch.
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Checking sign in state...");
            progressDialog.show();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    progressDialog.dismiss();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // When we get here in an automanager activity the error is likely not
        // resolvable - meaning Google Sign In and other Google APIs will be
        // unavailable.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        Log.v(TAG, "ActivityResult: " + requestCode);

        if (requestCode == OUR_REQUEST_CODE) {
            // Hide the progress dialog if its showing.
            mConnectionProgressDialog.dismiss();

            // Resolve the intent into a GoogleSignInResult we can process.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                Log.v(TAG, "Tapped sign in");
                // Show the dialog as we are now signing in.
                mConnectionProgressDialog.show();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, OUR_REQUEST_CODE);
                break;
            case R.id.sign_out_button:
                Log.v(TAG, "Tapped sign out");
                // This will clear the default account in order to allow the user
                // to potentially choose a different account from the
                // account chooser.
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // Hide the sign out buttons, show the sign in button.
                                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                                findViewById(R.id.sign_out_button)
                                        .setVisibility(View.INVISIBLE);
                                findViewById(R.id.revoke_access_button).setVisibility(
                                        View.INVISIBLE);
                            }
                        });
                break;
            case R.id.revoke_access_button:
                Log.v(TAG, "Tapped disconnect");
                // Go away and revoke access to this entire application.
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // The GoogleApiClient is now disconnected and access has been
                                // revoked. We should now delete any data we need to comply with
                                // the developer properties.

                                // Hide the sign out buttons, show the sign in button.
                                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                                findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
                                findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);
                            }
                        });
                break;
            default:
                // Unknown id.
                Log.v(TAG, "Unknown button press");
        }
    }

    /**
     * Helper method to trigger retrieving the server auth code if we've signed in.
     */
    private void handleSignInResult(GoogleSignInResult result ) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            // If you don't already have a server session, you can now send this code to your
            // server to authenticate on the backend.
            String authCode = acct.getServerAuthCode();

            // Hide the sign in buttons, show the sign out button.
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            //findViewById(R.id.sign_out_button)
                    //.setVisibility(View.VISIBLE);
            //findViewById(R.id.revoke_access_button).setVisibility(
                    //View.VISIBLE);
            Intent here=new Intent(this,MainActivity.class);
            startActivity(here);
        }
    }
}