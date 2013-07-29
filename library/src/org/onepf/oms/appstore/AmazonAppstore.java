/*******************************************************************************
 * Copyright 2013 One Platform Foundation
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 ******************************************************************************/

package org.onepf.oms.appstore;

import org.onepf.oms.Appstore;
import org.onepf.oms.AppstoreInAppBillingService;
import org.onepf.oms.DefaultAppstore;
import org.onepf.oms.OpenIabHelper;

import android.content.Context;
import android.util.Log;

/**
 * Author: Ruslan Sayfutdinov
 * Date: 16.04.13
 */
public class AmazonAppstore extends DefaultAppstore {
    private static final String TAG = AmazonAppstore.class.getSimpleName();
    private static volatile boolean IS_SANDBOX_MODE = false;
    private static volatile boolean IS_SANDBOX_MODE_CHECKED = true;
    private final Context mContext;
    private AmazonAppstoreBillingService mBillingService;

    public AmazonAppstore(Context context) {
        mContext = context;
    }

    @Override
    public boolean isPackageInstaller(String packageName) {
        if (IS_SANDBOX_MODE_CHECKED) {
            return !IS_SANDBOX_MODE;
        }
        synchronized (AmazonAppstore.class) {
            if (IS_SANDBOX_MODE_CHECKED) {
                return !IS_SANDBOX_MODE;
            }
            try {
                ClassLoader localClassLoader = AmazonAppstore.class.getClassLoader();
                localClassLoader.loadClass("com.amazon.android.Kiwi");
                IS_SANDBOX_MODE = false;
            } catch (Throwable localThrowable) {
                IS_SANDBOX_MODE = true;
            }
            IS_SANDBOX_MODE_CHECKED = true;
        }
        Log.d(TAG, "IS_SANDBOX_MODE: " + IS_SANDBOX_MODE);
        return !IS_SANDBOX_MODE;
    }

    /**
     * Cannot assume any app is published in Amazon, so say YES only if Amazon is installer
     */
    @Override
    public boolean isBillingAvailable(String packageName) {
        return isPackageInstaller(packageName);
    }
    
    @Override
    public int getPackageVersion(String packageName) {
        return Appstore.PACKAGE_VERSION_UNDEFINED;
    }
    
    @Override
    public AppstoreInAppBillingService getInAppBillingService() {
        if (mBillingService == null) {
            mBillingService = new AmazonAppstoreBillingService(mContext);
        }
        return mBillingService;
    }

    @Override
    public String getAppstoreName() {
        return OpenIabHelper.NAME_AMAZON;
    }


}
