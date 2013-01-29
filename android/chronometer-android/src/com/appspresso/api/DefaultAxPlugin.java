/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 리플렉션을 사용해서 메소드별로 분기해주는 {@link AxPlugin} 구현체.
 * <p>
 * {@link AxPlugin} 인터페이스를 직접 구현하는 대신 이 클래스를 상속하는 것이 더 간단.
 * <p>
 * 플러그인 구현 예:
 * 
 * <pre>
 * public class MyPlugin extends DefaultAxPlugin {
 * 	public void activate(AxRuntimeContext runtimeContext) {
 * 		super.activate(runtimeContext);
 * 
 * 		// ... 리소스 할당... 초기화...
 * 	}
 * 
 * 	public void deactivate(AxRuntimeContext runtimeContext) {
 * 		// ... 리소스 해제... 정리...
 * 
 * 		super.deactivate(runtimeContext);
 * 	}
 * 
 * 	public void echo(AxPluginContext context) {
 * 		context.sendResult(context.getParamAsString(0));
 * 	}
 * 
 * 	public void add(AxPluginContext context) {
 * 		context.sendResult(new Number(context.getParamAsNumber(0).intValue()
 * 				+ context.getParamAsNumber(1).intValue()));
 * 	}
 * }
 * </pre>
 * 
 * @version 1.0
 */
public class DefaultAxPlugin implements AxPlugin {

    /**
     * 앱스프레소 앱 실행 컨텍스트
     */
    protected AxRuntimeContext runtimeContext;

    /**
     * 메소드 캐시
     */
    private Map<String, Method> methods = new HashMap<String, Method>();

    /**
     * {@inheritDoc}
     * <p>
     * {@link AxPluginContext#getMethod()}에 따라 해당 메소드로 분기.
     * <p>
     * 해당 메소드가 없으면 {@link AxError#NOT_FOUND_ERR} 발생. 해당 메소드 실행 중에 {@link AxError}가 발생하면 자바스크립트로 에러를
     * 반환. 해당 메소드 실행 중에 알 수 없는 예외가 발생하면 {@link AxError#UNKNOWN_ERR}를 발생.
     */
    @Override
    public final void execute(AxPluginContext context) {
        String reqMethod = context.getMethod();
        if (reqMethod == null) { throw new AxError(AxError.NOT_AVAILABLE_ERR); }

        // e.g. reqMthd - 'getPropertyValue'
        Method mthdObject = this.methods.get(reqMethod);
        if (mthdObject == null) {
            try {
                mthdObject = this.getClass().getMethod(reqMethod, AxPluginContext.class);
                this.methods.put(reqMethod, mthdObject);
            }
            catch (Exception e) {
                // SecurityException
                // NoSuchMethodException
            }
        }

        if (mthdObject == null) { throw new AxError(AxError.NOT_FOUND_ERR); }

        try {
            mthdObject.invoke(this, context);
        }
        catch (AxError e) {
            context.sendError(e);
        }
        catch (Exception e) {
            // IllegalAccessException
            // IllegalArgumentException
            // InvocationTargetException
            Throwable cause = e.getCause();
            if (cause == null) {
                context.sendError(AxError.UNKNOWN_ERR, "Uncaught exception: " + e.getMessage());
                return;
            }
            if (cause instanceof AxError) {
                AxError err = (AxError) cause;
                context.sendError(err.getCode(), err.getMessage());
                return;
            }
            context.sendError(AxError.UNKNOWN_ERR, "Uncaught exception: " + cause.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: 오버라이드할때는 반드시 <code>super.activate(runtimeContext)</code>를 호출해야 함.
     */
    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: 오버라이드할때는 반드시 <code>super.deactivate(runtimeContext)</code>를 호출해야 함.
     */
    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {}
}
