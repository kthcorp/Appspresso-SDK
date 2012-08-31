package com.appspresso.core.runtime.server.kraken;

import java.util.HashMap;
import java.util.Map;

public class BridgeSessionManager {

    private static Map<String, BridgeSession> sessions = new HashMap<String, BridgeSession>();

    private BridgeSessionManager() {}

    public static BridgeSession lookup(String sessionID) {
        synchronized (sessions) {
            BridgeSession session = sessions.get(sessionID);
            if (session == null) {
                session = new BridgeSession(sessionID);
                sessions.put(session.getSessionID(), session);
            }
            return session;
        }
    }

    public static class BridgeSession {

        private String sessionID;
        private boolean initialized;
        private boolean javaScriptEvaluationEnabled;

        private BridgeSession(String sessionID) {
            this.initialized = false;
            this.sessionID = sessionID;
        }

        public String getSessionID() {
            return sessionID;
        }

        public void setJavaScriptEvaluationEnabled(boolean flag) {
            javaScriptEvaluationEnabled = flag;
        }

        public boolean getJavaScriptEvaluationEnabled() {
            return javaScriptEvaluationEnabled;
        }

        public boolean getInitialized() {
            return initialized;
        }

        public void setInitialized() {
            initialized = true;
        }

    }

}
