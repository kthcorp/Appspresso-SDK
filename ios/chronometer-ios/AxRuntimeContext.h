/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@protocol AxRuntimeContext;
@protocol W3Widget;
@protocol AxFileSystemManager;
@protocol AxPlugin;
@protocol AxViewControllerDelegate;

/*!
 * 앱스프레소 앱을 실행하는 동안 필요한 정보를 보유하는 인터페이스.
 * <p>
 * @link //apple_ref/occ/intf/AxPluginContext @/link, @link //apple_ref/occ/intf/AxPlugin @/link 과
 * 더불어 앱스프레소 플러그인 개발에서 가장 핵심적인 인터페이스.
 * <p>
 * 앱스프레소 앱은 오직 하나의 AxRuntimeContext 인스턴스를 갖고 있으며, AxPlugin의 activate /
 * deactivate 메소드를 통해 개별 플러그인에 주입(inject)된다.
 * <p>
 * <ul>
 * <li>주요 네이티브 객체(WebView, Activity 등) 접근.</li>
 * <li>웹뷰와 액티비티의 기본 동작을 가로채서 미세 조정할 수 있는 리스너(WebViewListener,
 * WebChromeClientListener, WebViewClientListener, ActivityListener 등).</li>
 * <li>웹뷰와 통신(자바스크립트 호출 등).</li>
 * <li>앱을 실행하는 동안 유효한 범용 속성(attribute) 집합.</li>
 * <li>플러그인 의존성 해결.</li>
 * </ul>
 * <p>
 * 
 * @version 1.0
 * @see //apple_ref/occ/intf/AxPluginContext  AxPluginContext protocol
 * @see //apple_ref/occ/intf/AxPlugin AxPlugin protocol
 * @see //apple_ref/occ/intfm/AxPlugin/activate: AxPlugin의 activate 메소드
 * @see //apple_ref/occ/intfm/AxPlugin/deactivate: AxPlugin의 deactivate 메소드
 */
@protocol AxRuntimeContext
 
/*!
 * 웹앱을 실행하고 있는 웹뷰(UIWebView) 인스턴스를 얻음.
 * 
 * @return WebView 인스턴스
 * @since 1.0
 */
-(UIWebView*)getWebView;

/*!
 * 웹앱을 실행하고 있는 웹뷰의 컨트롤러(UIViewController) 인스턴스를 얻음.
 * 
 * @return UIViewController 인스턴스
 * @since 1.0
 */
-(UIViewController*)getViewController;

/*!
 * W3C 위젯 인스턴스를 얻음.
 * 
 * @return W3Widget 인스턴스
 * @since 1.0
 */
-(id<W3Widget>)getWidget;

/*!
 * 파일 시스템 관리자 인스턴스를 얻음.
 * <p>
 * <code>deviceapis.filesystem</code>의 가상 루트를 제공하기 위해서 사용.
 * 
 * @return AxFileSystemManager 인스턴스
 * @since 1.0
 */
-(id<AxFileSystemManager>)getFileSystemManager;

/*!
 * 지정한 이름(일반적으로 URI 형식)을 가진 피쳐의 활성 상태 확인.
 * 
 * @param feature
 *            피쳐 이름
 * @return 활성 상태면 YES, 아니면 NO
 * @since 1.0
 */
-(bool)isActivatedFeature:(NSString*)featureUri;

/*!
 * 활성화된 피쳐의 목록을 얻음.
 * 
 * @return 피쳐 목록
 * @since 1.0
 */
-(NSArray*)getActivatedFeatures;

/*!
 * 지정한 ID(일반적으로, reverse domain 형식)를 가진 플러그인을 적재.
 * <p>
 * 지정한 ID를 가진 플러그인을 적재하지 못했으면 nil을 반환.
 * 
 * @param pluginId
 *            플러그인 ID
 * @return AxPlugin 인스턴스 또는 nil
 * @since 1.0
 */
-(id<AxPlugin>)requirePlugin:(NSString*)pluginId;

/*!
 * 지정한 이름의 피쳐(일반적으로, URL 형식)를 가진 플러그인을 적재.
 * <p>
 * 지정한 이름의 피쳐를 가진 플러그인을 적재하지 못했으면 nil을 반환.
 * 
 * @param featureUri
 *            피쳐 이름
 * @return AxPlugin 인스턴스 또는 nil
 * @since 1.0
 */
-(id<AxPlugin>)requirePluginWithFeature:(NSString*)featureUri;

/*!
 * 자바스크립트 코드를 실행.
 * <p>
 * 에러 검사 없음. 리턴값 받을 수 없음.
 * 
 * @param script
 *            자바스크립트 문장
 * @since 1.0
 */
- (void)executeJavaScript:(NSString*)script;


/*!
 * 자바스크립트 함수를 호출.
 * <p>
 * NSString, NSNumber, NSArray, NSDictionary 객체 타입을 제외한 인자는 지원하지 않음.
 * 
 * @param functionName
 *            자바스크립트 함수 이름
 * @param args
 *            인자들. 인자가 하나 이상일 경우에는 마지막는 nil이 있어야 함.
 * @since 1.0
 */
- (void)executeJavaScriptFunction:(NSString*)functionName, ... NS_REQUIRES_NIL_TERMINATION;

/*!
 * 자바스크립트 주기적인 성공 콜백을 호출.
 * <p>
 * 자바스크립트에서 <code>ax.Plugin#watch(method,callback,errback,params)</code>
 * 메소드를 호출했을 때, 자바스크립트로 "결과"를 전달하기 위해서 사용.
 * 
 * @param identifier
 *            자바스크립트에서 부여한 watch 식별자
 * @param result
 *            자바스크립트로 전달할 결과
 * @since 1.0
 * @deprecated since 1.2. You should now use @link //apple_ref/occ/intfm/AxPluginContext/sendWatchResult: @/link
 */
- (void)invokeWatchSuccessListener:(NSInteger)identifier result:(id)result;

/*!
 * 자바스크립트 주기적인 에러 콜백을 호출.
 * <p>
 * 자바스크립트에서 <code>ax.Plugin#watch(method,callback,errback,params)</code>
 * 메소드를 호출했을 때, 자바스크립트로 "에러"를 전달하기 위해서 사용.
 * 
 * @param identifier
 *            자바스크립트에서 부여한 watch 식별자
 * @param code
 *            자바스크립트로 전달할 에러 코드
 * @param message
 *            자바스크립트로 전달할 에러 메시지
 * @since 1.0
 * @deprecated since 1.2. You should now use @link //apple_ref/occ/intfm/AxPluginContext/sendWatchError:message: @/link
 */
- (void)invokeWatchErrorListener:(NSInteger)identifier code:(NSInteger)code message:(NSString *)message;

/*!
 * 속성 설정.
 * <p>
 * 같은 이름을 가진 속성이 이미 있을 경우에는 기존 속성 값을 변경하고, 그렇지 않을 경우에는 새 속성이 추가됨.
 * <p>
 * 이 속성은 앱이 실행되는 동안 유효하므로, 플러그인 간의 통신, 상태 유지 등의 다양한 용도로 활용할 수 있음.
 * 
 * @param key
 *            속성 이름
 * @param value
 *            속성 값
 * @since 1.0
 */
-(void)setAttribute:(NSString*)key value:(id)value;

/*!
 * 속성 값을 얻음.
 * <p>
 * 지정한 이름을 가진 속성이 없을 경우 nil을 반환.
 * 
 * @param key
 *            속성 이름
 * @return 속성 값
 * @since 1.0
 */
-(id)getAttribute:(NSString*)key;

/*!
 * 속성 삭제.
 * <p>
 * 지정한 이름을 가진 속성이 없을 경우 아무런 효과 없음(무시).
 * 
 * @param key
 *            속성 이름
 * @since 1.0
 */
-(void)removeAttribute:(NSString*)key;

/*!
 * UIWebViewDelegate 추가.
 * <p>
 * 네이티브 iOS 앱 개발에서, UIWebViewDelegate를 구현하는 것과 유사하지만,
 * 여러 개의 delegate를 가질 수 있음.
 * 
 * @param delegate
 *            UIWebViewDelegate 인스턴스
 * @since 1.0
 * @link //apple_ref/occ/intf/UIWebViewDelegate @/link
 */
- (void)addWebViewDelegate:(id<UIWebViewDelegate>)delegate;

/*!
 * WebViewDelegate 제거.
 * 
 * @param delegate
 *            UIWebViewDelegate 인스턴스
 * @since 1.0
 */
- (void)removeWebViewDelegate:(id<UIWebViewDelegate>)delegate;

/*!
 * UIApplicationDelegate 추가.
 * <p>
 * 네이티브 iOS 앱 개발에서, UIApplicationDelegate를 구현하는 것과 유사하지만,
 * 여러 개의 delegate를 가질 수 있음.
 * 
 * @param delegate
 *            UIApplicationDelegate 인스턴스
 * @since 1.0
 * @see //apple_ref/occ/intf/UIApplicationDelegate UIApplicationDelegate
 */
- (void)addApplicationDelegate:(id<UIApplicationDelegate>)delegate;

/*!
 * UIApplicationDelegate 제거.
 * 
 * @param delegate
 *            UIApplicationDelegate 인스턴스
 * @since 1.0
 */
- (void)removeApplicationDelegate:(id<UIApplicationDelegate>)delegate;

/*!
 * AxViewControllerDelegate 추가.
 * <p>
 * 네이티브 iOS 앱 개발에서, UIViewController를 상속하여 일부 메소드를 오버라이드 하는 것과 유사하지만,
 * 여러 개의 delegate를 가질 수 있음.
 * 
 * @param delegate
 *            AxViewControllerDelegate 인스턴스
 * @since 1.0
 * @link //apple_ref/occ/intf/UIViewController @/link
 */
- (void)addViewControllerDelegate:(id<AxViewControllerDelegate>)delegate;

/*!
 * AxViewControllerDelegate 제거.
 * 
 * @param delegate
 *            AxViewControllerDelegate 인스턴스
 * @since 1.0
 */
- (void)removeViewControllerDelegate:(id<AxViewControllerDelegate>)delegate;

/*!
 * 앱을 실행할 때 주어진 추가 옵션을 얻음.
 *
 * @return 실행 옵션
 * @since 1.0
 * @link //apple_ref/occ/intfm/UIApplicationDelegate/application:didFinishLaunchingWithOptions: @/link
 */
- (NSDictionary*)getLaunchOptions;

@end
