package com.example.android.popularmoviesapp.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Manages "Authentication" to backend service.  The SyncAdapter framework
 * requires an authenticator object, so syncing to a service that doesn't need authentication
 * typically means creating a stub authenticator like this one.
 * Note: modified from https://github.com/udacity/Sunshine-Version-2
 */
public class PopMoviesAuthenticator extends AbstractAccountAuthenticator {

    public PopMoviesAuthenticator(Context context) {
        super(context);
    }

    @Override
    // No properties to edit.
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    // Because we're not actually adding an account to the device, just return null.
    public Bundle addAccount(
            AccountAuthenticatorResponse response, String accountType, String authTokenType,
            String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    // Ignore attempts to confirm credentials
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response, Account account, Bundle options)
            throws NetworkErrorException {
        return null;
    }

    @Override
    // Getting an authentication token is not supported
    public Bundle getAuthToken(
            AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    // Getting a label for the auth token is not supported
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    // Updating user credentials is not supported
    public Bundle updateCredentials(
            AccountAuthenticatorResponse response, Account account, String authTokenType,
            Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    // Checking features for the account is not supported
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
