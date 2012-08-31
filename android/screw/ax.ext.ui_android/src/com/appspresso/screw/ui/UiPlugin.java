package com.appspresso.screw.ui;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.screw.ui.orientation.AxOrientationController;

/**
 * ax.ext.ui
 */
public class UiPlugin extends DefaultAxPlugin {
    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        orientationController = OrientationControllerFactory.create(runtimeContext.getActivity());

        Object timeout = runtimeContext.getAttribute("orientation.timeout");
        if (timeout != null) {
            try {
                orientationTimeout = (Integer) timeout;
            }
            catch (ClassCastException e) {
                // Nothing to do.
            }
        }

        super.activate(runtimeContext);
    }

    public void alert(AxPluginContext context) {
        try {
            runtimeContext.getWebView().pauseTimers();

            String message = context.getParamAsString(0, "");
            String title = context.getNamedParamAsString(1, "title", null);
            String positive = context.getNamedParamAsString(1, "positive", null);
            Boolean cancelable = context.getNamedParamAsBoolean(1, "cancelable", true);

            UiUtils.alert(runtimeContext.getActivity(), title, message, positive, cancelable);
            context.sendResult();
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
        finally {
            runtimeContext.getWebView().resumeTimers();
        }
    }

    public void confirm(AxPluginContext context) {
        try {
            runtimeContext.getWebView().pauseTimers();

            String message = context.getParamAsString(0, "");
            String title = context.getNamedParamAsString(1, "title", null);
            String positive = context.getNamedParamAsString(1, "positive", null);
            String negative = context.getNamedParamAsString(1, "negative", null);
            Boolean cancelable = context.getNamedParamAsBoolean(1, "cancelable", true);

            boolean result =
                    UiUtils.confirm(runtimeContext.getActivity(), title, message, positive,
                            negative, cancelable);
            context.sendResult(result);
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
        finally {
            runtimeContext.getWebView().resumeTimers();
        }
    }

    public void prompt(AxPluginContext context) {
        try {
            runtimeContext.getWebView().pauseTimers();

            String message = context.getParamAsString(0, "");
            String value = context.getParamAsString(1, "");
            String title = context.getNamedParamAsString(2, "title", null);
            String positive = context.getNamedParamAsString(2, "positive", null);
            String negative = context.getNamedParamAsString(2, "negative", null);
            Boolean cancelable = context.getNamedParamAsBoolean(1, "cancelable", true);

            String result =
                    UiUtils.prompt(runtimeContext.getActivity(), title, message, positive,
                            negative, value, cancelable);
            context.sendResult(result);
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
        finally {
            runtimeContext.getWebView().resumeTimers();
        }
    }

    public void pick(AxPluginContext context) {
        try {
            runtimeContext.getWebView().pauseTimers();

            String[] items = context.getParamAsStringArray(0);
            String title = context.getNamedParamAsString(1, "title", null);
            Boolean cancelable = context.getNamedParamAsBoolean(1, "cancelable", true);

            int result = UiUtils.pick(runtimeContext.getActivity(), title, items, cancelable);
            context.sendResult(result);
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
        finally {
            runtimeContext.getWebView().resumeTimers();
        }
    }

    public void open(AxPluginContext context) {
        try {
            String url = context.getParamAsString(0, "");
            // Map opts = context.getParamAsMap(1);

            boolean result = UiUtils.open(runtimeContext.getActivity(), url);
            context.sendResult(result);
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void showProgress(AxPluginContext context) {
        try {
            String message = context.getParamAsString(0, "");
            String title = context.getNamedParamAsString(1, "title", null);

            UiUtils.showProgress(runtimeContext.getActivity(), title, message);
            context.sendResult();
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void hideProgress(AxPluginContext context) {
        try {
            UiUtils.hideProgress(runtimeContext.getActivity());
            context.sendResult();
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void showStatusBar(AxPluginContext context) {
        try {
            UiUtils.showStatusBar(runtimeContext.getActivity());
            context.sendResult();
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void hideStatusBar(AxPluginContext context) {
        try {
            UiUtils.hideStatusBar(runtimeContext.getActivity());
            context.sendResult();
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void addWebView(AxPluginContext context) {
        try {
            String url = context.getParamAsString(0, "");
            if (url.length() < 1) {
                context.sendError(AxError.INVALID_VALUES_ERR, "url parameter error");
                return;
            }
            int top = context.getNamedParamAsNumber(1, "top", 0).intValue();
            int left = context.getNamedParamAsNumber(1, "left", 0).intValue();
            int width = context.getNamedParamAsNumber(1, "width", 100).intValue();
            int height = context.getNamedParamAsNumber(1, "height", 100).intValue();
            int start = context.getNamedParamAsNumber(1, "start", -1).intValue();
            int finish = context.getNamedParamAsNumber(1, "finish", -1).intValue();
            int error = context.getNamedParamAsNumber(1, "error", -1).intValue();
            int load = context.getNamedParamAsNumber(1, "load", -1).intValue();
            String zoomDensity = context.getNamedParamAsString(1, "zoomDensity");

            WebViewManager wvMgr = WebViewManager.getInstance();
            // althjs. 웹뷰 생성 전 일단 handle 리턴하고 나머지는 watch listener를 통해서 전달~
            context.sendResult(wvMgr.getHandle());
            wvMgr.addWebView(runtimeContext, url, top, left, width, height, start, finish, error,
                    load, zoomDensity);
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void removeWebView(AxPluginContext context) {
        try {
            WebViewManager wvMgr = WebViewManager.getInstance();
            wvMgr.removeWebView(runtimeContext);
            context.sendResult();
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    RelativeLayout textLayout = null;
    EditText et = null;
    TextView tv = null; // 현재 입력가능한(남은) 글자수를 표시
    final private int InputTextSize = 16; // Text 입력창 폰트 사이
    final private int countTextSize = 16; // 하단 글자수 폰트 사이즈
    final private int charHeight = 40; // 하단 글자수 영역 높이.

    public void addTextView(final AxPluginContext context) {
        try {
            if (et != null) {
                context.sendError(AxError.INVALID_ACCESS_ERR, "No more Text View");
                return;
            }

            final Activity activity = runtimeContext.getActivity();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        // android device(ICS : API 14+) throws exception with
                        // follow messge...
                        // A WebView method was called on thread 'thread-0'.
                        // All WebView methods must be called on the UI thread.
                        // Future versions of WebView may not support use on
                        // other threads.
                        // see also : jira [#PBMBINDV20110600257-19]
                        float scale = runtimeContext.getWebView().getScale();
                        final String text = context.getParamAsString(0);
                        final int top =
                                Math.round(context.getNamedParamAsNumber(1, "top", 0).intValue()
                                        * scale);
                        final int left =
                                Math.round(context.getNamedParamAsNumber(1, "left", 0).intValue()
                                        * scale);
                        int w =
                                Math.round(context.getNamedParamAsNumber(1, "width", -1).intValue()
                                        * scale);
                        int h =
                                Math.round(context.getNamedParamAsNumber(1, "height", -1)
                                        .intValue() * scale);
                        final int maxLength =
                                context.getNamedParamAsNumber(1, "maxLength", -1).intValue();

                        if (w == -1) {
                            w = runtimeContext.getWebView().getWidth();
                        }
                        if (h == -1) {
                            h = runtimeContext.getWebView().getHeight();
                        }
                        final int width = w;
                        final int height = h;

                        if (textLayout == null && et == null) {
                            textLayout = new RelativeLayout(activity);

                            et = new EditText(activity);

                            et.setBackgroundColor(Color.WHITE);
                            et.setGravity(Gravity.TOP | Gravity.LEFT);
                            et.setTextSize(InputTextSize);
                            // Text Auto Correct not used
                            // et.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                            et.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                            et.setWidth(width);
                            et.setHeight(height);
                            et.setFocusable(true);
                            et.setFocusableInTouchMode(true);
                            et.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before,
                                        int count) {
                                    // Log.i("onTextChanged", s.toString());
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count,
                                        int after) {
                                    // Log.i("beforeTextChanged", s.toString());
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (maxLength > 0) {
                                        String str =
                                                String.valueOf(maxLength - et.getText().length());
                                        tv.setText(str);
                                    }
                                    // Log.i("afterTextChanged", s.toString());
                                }
                            });
                            RelativeLayout.LayoutParams lParam1 =
                                    new RelativeLayout.LayoutParams(width,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            lParam1.topMargin = top;
                            lParam1.leftMargin = left;
                            textLayout.addView(et, lParam1);

                            tv = new TextView(activity);
                            tv.setWidth((int) (width));
                            tv.setHeight(charHeight);
                            tv.setBackgroundColor(Color.WHITE);
                            tv.setTextSize(countTextSize);
                            tv.setGravity(Gravity.CENTER | Gravity.RIGHT);
                            RelativeLayout.LayoutParams lParam2 =
                                    new RelativeLayout.LayoutParams(width,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            lParam2.topMargin = (int) (top + height - charHeight);
                            lParam2.leftMargin = (int) (left);
                            lParam2.rightMargin = 0;
                            textLayout.addView(tv, lParam2);

                            FrameLayout.LayoutParams params =
                                    new FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.FILL_PARENT,
                                            ViewGroup.LayoutParams.FILL_PARENT);
                            ((ViewGroup) runtimeContext.getWebView().getParent()).addView(
                                    textLayout, params);
                            if (maxLength > 0) {
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                et.setFilters(FilterArray);
                                tv.setText(String.valueOf(maxLength));
                            }
                            et.setText(text);
                            et.requestFocus();

                            InputMethodManager mgr =
                                    (InputMethodManager) activity
                                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }

            });
            context.sendResult("_textView");
        }
        catch (AxError e) {
            context.sendError(e.getCode(), e.getMessage());
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, e.getMessage());
        }
    }

    public void removeTextView(AxPluginContext context) {
        String str = et.getText().toString();
        context.sendResult(str);
        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                runtimeContext.getWebView().requestFocus();
                if (textLayout != null) {
                    InputMethodManager mgr =
                            (InputMethodManager) runtimeContext.getActivity().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(et.getApplicationWindowToken(), 0);
                    if (et != null) {
                        textLayout.removeView(et);
                        et = null;
                    }
                    if (tv != null) {
                        textLayout.removeView(tv);
                        tv = null;
                    }
                    ((ViewGroup) runtimeContext.getWebView().getParent()).removeView(textLayout);
                    textLayout = null;
                }
                System.gc();
            }
        });
    }

    // Orientation
    //
    // 어플리케이션의 방향을 설정하기 위한 메소드들이다.
    // 이 메소드들은 비동기로 불리워지기 때문에 호출순서를 보장해주기 어렵다.

    private AxOrientationController orientationController;
    private int orientationTimeout = 10000;

    /**
     * 현재 설정된 방향값을 가져온다. 방향을 설정하기 위한 값일 뿐 적용된 값을 의미하는 것이 아니기 때문에 실제 단말 어플리케이션의 방향과 차이가 날 수도 있다.
     * 
     * @param context AxPluginContext
     */
    public void getOrientation(AxPluginContext context) {
        int orientation = orientationController.getOrientation();
        context.sendResult(orientation);
    }

    /**
     * 새 방향값을 설정한다.
     * 
     * @param context AxPluginContext
     */
    public void setOrientation(AxPluginContext context) {
        final int orientation = context.getParamAsNumber(0).intValue();
        final Semaphore lock = new Semaphore(0);

        // TODO 이 부분은 다음 버전에서 수정. 동작에는 문제없음.
        if (orientation < 0 || orientation > 4) {
            context.sendError(AxError.UNKNOWN_ERR, orientation + " is an invalid orientation.");
            return;
        }

        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orientationController.setOrientation(orientation);
                if (lock != null) {
                    lock.release();
                }
            }
        });

        try {
            lock.tryAcquire(orientationTimeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            context.sendError(AxError.UNKNOWN_ERR, "An unknown error occurred.");
        }
        finally {
            context.sendResult();
        }
    }

    /**
     * 처음 어플리케이션이 가지고 있던 방향값으로 설정한다.
     * 
     * @param context AxPluginContext
     */
    public void resetOrientation(AxPluginContext context) {
        final Semaphore lock = new Semaphore(0);

        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orientationController.resetOrientation();
                if (lock != null) {
                    lock.release();
                }
            }
        });

        try {
            lock.tryAcquire(orientationTimeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            context.sendError(AxError.UNKNOWN_ERR, "An unknown error occurred.");
        }
        finally {
            context.sendResult();
        }
    }
}
