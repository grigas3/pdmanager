package com.pdmanager.communication;

import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxFiles;
import com.dropbox.core.v2.DbxSharing;
import com.dropbox.core.v2.DbxUsers;

import java.util.Locale;

/**
 * Singleton instance of {@link DbxClientV2} and friends
 */
public class DropboxClient {

    private static DbxClientV2 sDbxClient;

    public static void init(String accessToken) {
        if (sDbxClient == null) {
            String userLocale = Locale.getDefault().toString();

            try {
                DbxRequestConfig requestConfig = new DbxRequestConfig(
                        "examples-v2-demo",
                        userLocale
                );

                sDbxClient = new DbxClientV2(requestConfig, accessToken, DbxHost.Default);

            } catch (Exception ex) {


            }
        }
    }

    public static DbxFiles files() {
        if (sDbxClient != null)
            return sDbxClient.files;
        else
            return null;
    }

    public static DbxUsers users() {
        if (sDbxClient != null)
            return sDbxClient.users;
        else
            return null;
    }

    public static DbxSharing sharing() {
        if (sDbxClient != null)
            return sDbxClient.sharing;
        else
            return null;
    }
}