/*
 * Copyright (c) 2015 PocketHub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.igor.gitapiexample.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.igor.gitapiexample.auth.LoginActivity;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.accounts.AccountManager.KEY_INTENT;
import static android.provider.ContactsContract.Directory.ACCOUNT_TYPE;
import static com.example.igor.gitapiexample.auth.LoginActivity.AUTHTOKEN_TYPE;
import static com.example.igor.gitapiexample.auth.LoginActivity.USERNAME;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private Context context;

    public AccountAuthenticator(final Context context) {
        super(context);

        this.context = context;
    }

    /**
     * The user has requested to add a new account to the system. We return an
     * intent that will launch our login screen if the user has not logged in
     * yet, otherwise our activity will just pass the user's credentials on to
     * the account manager.
     */
    @Override
    public Bundle addAccount(final AccountAuthenticatorResponse response,
            final String accountType, final String authTokenType,
            final String[] requiredFeatures, final Bundle options)
            throws NetworkErrorException {
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
            final AccountAuthenticatorResponse response, final Account account,
            final Bundle options) {
        return null;
    }

    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response,
            final String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response,
            final Account account, final String authTokenType,
            final Bundle options) throws NetworkErrorException {
        return null;

    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        if (ACCOUNT_TYPE.equals(authTokenType)) {
            return authTokenType;
        } else {
            return null;
        }
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse response,
            final Account account, final String[] features)
            throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(
            final AccountAuthenticatorResponse response, final Account account,
            final String authTokenType, final Bundle options) {

        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        if (!TextUtils.isEmpty(account.name)) {
            intent.putExtra(USERNAME, account.name);
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        return bundle;
    }
}
