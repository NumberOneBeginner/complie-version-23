package fi.iki.elonen;

import java.io.IOException;

import android.util.Log;

public class ServerRunner {
    private static final String TAG = "ServerRunner";

    public static void run(Class serverClass) {
        try {
            executeInstance((NanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(NanoHTTPD server) {
        try {
            server.start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        Log.d(TAG,"Server started, Hit Enter to stop.\n");

        try {
            System.in.read();
        } catch (Throwable ignored) {
        }

//        server.stop();
//        Log.d(TAG,"Server stopped.\n");
    }
}
