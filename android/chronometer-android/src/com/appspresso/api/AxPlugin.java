/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api;

/**
 * 앱스프레소 플러그인이 구현해야할 인터페이스.
 * <p>
 * {@link AxRuntimeContext}, {@link AxPluginContgext}과 더불어 앱스프레소 플러그인 개발에서 가장 핵심적인 인터페이스.
 * <p>
 * 이 인터페이스를 직접 구현하는 대신 {@link DefaultAxPlugin}을 상속하는 것이 더 간단.
 * <p>
 * 플러그인 구현 예:
 * 
 * <pre>
 * public class MyPlugin implements AxPlugin {
 * 		private AxRuntimeContext *runtimeContext;
 * 
 * 		public void activate(AxRuntimeContext runtimeContext) {
 * 			this.runtimeContext = runtimeContext;
 * 
 * 			// ... allocate resources
 * 		}
 * 
 * 		public void deactivate(AxRuntimeContext runtimeContext) {
 * 			// ... release resources
 * 
 * 			this.runtimeContext = null;
 * 		}
 * 
 * 		public void execute(AxPluginContext context) {
 * 			String method = context.getMethod();
 * 			if("echo".equals(method)) {
 * 				context.sendResult(context.getParamAsString(0));
 * 			} else if("add".eqauls(method)) {
 * 				context.sendResult(new Number(context.getParamAsNumber(0).intValue() + context.getParamAsNumber(1).intValue()));
 * 			} else {
 * 				context.sendError(AxError.UNKNOWN_ERROR);
 * 			}
 * 		}
 * }
 * </pre>
 * 
 * @version 1.0
 * @see AxRuntimeContext
 * @see AxPluginContext
 */
public interface AxPlugin {

    /**
     * 플러그인이 활성화 될 때(일반적으로, 앱이 실행될 때) 호출됨.
     * <p>
     * 여기에서 플러그인 의존성을 해결, 리소스 할당 등을 비롯한 초기화 작업을 수행.
     * <p>
     * 필요하다면
     * {@link AxRuntimeContext#addActivityListener(com.appspresso.api.activity.ActivityListener)} 등을
     * 호출.
     * 
     * @param runtimeContext 앱스프레소 앱 실행 컨텍스트
     * @since 1.0
     */
    void activate(AxRuntimeContext runtimeContext);

    /**
     * 플러그인이 비활성화 될 때(일반적으로, 앱이 종료될 때) 호출됨.
     * <p>
     * 여기에서 플러그인에서 할당한 리소스 해제 등을 비롯한 정리 작업을 수행.
     * <p>
     * 필요하다면
     * {@link AxRuntimeContext#removeActivityListener(com.appspresso.api.activity.ActivityListener)}
     * 등을 호출.
     * 
     * @param runtimeContext 앱스프레소 앱 실행 컨텍스트
     * @since 1.0
     */
    void deactivate(AxRuntimeContext runtimeContext);

    /**
     * 자바스크립트에서 호출됨.
     * <p>
     * 전달 받은 @{link AxPluginContext}를 통해, 메소드 이름과 자바스크립트에서 전달받은 파라메터등을 확인하고, 이를 처리한 다음, 처리 결과 또는 에러를
     * 자바스크립트로 반환.
     * 
     * @param context 플러그인 메소드 호출 컨텍스트
     * @since 1.0
     */
    void execute(AxPluginContext context);

}
