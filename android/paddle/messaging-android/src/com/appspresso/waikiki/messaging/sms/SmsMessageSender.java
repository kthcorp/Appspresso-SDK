package com.appspresso.waikiki.messaging.sms;

import java.util.List;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import com.appspresso.waikiki.messaging.Message;
import com.appspresso.waikiki.messaging.MessagePackage;
import com.appspresso.waikiki.messaging.MessageSenderImpl;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

public class SmsMessageSender extends MessageSenderImpl {
    public static final Log L = AxLog.getLog(MessagePackage.class);
    public static final String ACTION = "waikiki_messaging_sendmessage";
    public static final String SMS_DATA_SCHEMA = "content";
    public static final String SMS_DATA_TYPE = "vnd.android.cursor.item/sms";
    public static final String SMS_DELIVERY_REPORT_MODE = "pref_key_sms_delivery_reports";
    private int timeout = 60000;

    private SmsSentReceiver receiver;

    public SmsMessageSender(Activity activity) {
        super(activity);
    }

    @Override
    public void activate(Activity activity) {

    }

    @Override
    public void deactivate(Activity activity) {
        deleteObservers();
        unregisterReceiver();
    }

    @Override
    public synchronized void sendMessagesImpl(final MessagePackage messagePackage) {
        Message[] messages = messagePackage.getMessages();
        SmsManager smsManager = SmsManager.getDefault();

        long handle = messagePackage.getHandle();
        int length = messages.length;

        Message message = null;
        Intent intent = null;
        PendingIntent sentIntent = null;

        /*
         * 현재 갤럭시 넥서스에서는 장문메시지를 전송할 경우 sendTextMessage에 예외가 발생하지 않으면서 결과를 받아오지 않고 있다.
         * 
         * 그래서 일정 시간동안 결과를 받아오지 않으면 무조건 실패로 간주하도록 한다.
         */
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (messagePackage.isAllMessageSent()) { return; }

                List<Integer> indexes = messagePackage.getRemainingMessages();
                for (int index : indexes) {
                    messagePackage.setMessageFailed(index);
                }

                setChanged();
                notifyObservers(null);
                clearChanged();
            }
        };
        timer.schedule(timerTask, timeout);

        for (int i = 0; i < length; i++) {
            message = messages[i];
            try {
                intent = new Intent(ACTION);
                intent.putExtra("handle", handle);
                intent.putExtra("index", i);
                sentIntent =
                        PendingIntent.getBroadcast(activity, (int) System.currentTimeMillis(),
                                intent, 0);

                smsManager.sendTextMessage(message.getTo(), null, message.getBody(), sentIntent,
                        null);
            }
            catch (Exception e) {
                if (L.isErrorEnabled()) {
                    L.error("fail to send the message.", e);
                }

                messagePackage.setMessageFailed(i);
            }
        }
    }

    @Override
    public void addObserver(Observer observer) {
        if (countObservers() == 0) registerReceiver();
        super.addObserver(observer);
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
        if (countObservers() == 0) unregisterReceiver();
    }

    private synchronized void registerReceiver() {
        if (receiver == null) receiver = new SmsSentReceiver();
        activity.registerReceiver(receiver, new IntentFilter(ACTION));
    }

    private synchronized void unregisterReceiver() {
        if (activity != null && receiver != null) {
            activity.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public class SmsSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ACTION.equals(intent.getAction())) return;

            Object[] data = new Object[3];
            data[0] = intent.getLongExtra("handle", -1);
            data[1] = intent.getIntExtra("index", -1);
            data[2] = (Activity.RESULT_OK == getResultCode());

            setChanged();
            notifyObservers(data);
            clearChanged();
        }
    }
}
