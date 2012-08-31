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


/*!
 * 앱스프레소 플러그인을 위한 로깅 지원 클래스.<br>
 * 앱스프레소 스튜디오를 통해서 소스 코드 수정없이 로그 레벨 설정 가능.<br>
 * 사용 예:
 * <pre>
 * @textblock
 *   AxLog *log = [AxLog log:@"TRACE" level:1];
 *   if ([log isTraceEnabled]){
 *     [log trace:@"traceMessage",nil];
 *   }
 * @/textblock
 * </pre>
 * 
 * @version 1.0
 */

@interface AxLog : NSObject {
@private
    NSString *_category;
    int _level;
}

/*!
 * 로그 객체 생성
 * @return AxLog 인스턴스
 */
+(AxLog*) log;
/*!
 * 지정한 태그를 통해 로그 객체 생성
 * @param category
 *          카테고리
 * @return AxLog 인스턴스
 */
+(AxLog*) log:(NSString*)category;
/*!
 * 지정한 태그 및 레벨을 지정하여 로그 객체 생성
 * @param category
 *           카테고리
 * @param level
 *           레벨
 * @return AxLog 인스턴스
 */
+(AxLog*) log:(NSString*)category level:(int)level;

/*!
 * 상세 로그 출력
 * @param format 
 *           문자열
 * @param args
 *            인자들. 인자가 하나 이상일 경우에는 마지막는 nil이 있어야 함.
 */
-(void) trace:(NSString*) format, ...;
/*!
 * 디버그 로그 출력
 * @param format 
 *           문자열
 * @param args
 *            인자들. 인자가 하나 이상일 경우에는 마지막는 nil이 있어야 함.
 */
-(void) debug:(NSString*) format, ...;
/*!
 * 정보 로그 출력
 * @param format 
 *           문자열
 * @param args
 *            인자들. 인자가 하나 이상일 경우에는 마지막는 nil이 있어야 함.
 */
-(void) info:(NSString*) format, ...;
/*!
 * 경고 로그 출력
 * @param format 
 *           문자열
 * @param args
 *            인자들. 인자가 하나 이상일 경우에는 마지막는 nil이 있어야 함.
 */
-(void) warn:(NSString*) format, ...;
/*!
 * 에러 로그 출력
 * @param format 
 *           문자열
 * @param args
 *            인자들. 인자가 하나 이상일 경우에는 마지막는 nil이 있어야 함.
 */
-(void) error:(NSString*) format, ...;

/*!
 * 상세 로그 허용여부
 * @return 허용하면 YES, 그렇지 않으면 NO.
 */
-(BOOL) isTraceEnabled;
/*!
 * 디버그 로그 허용여부
 * @return 허용하면 YES, 그렇지 않으면 NO.
 */
-(BOOL) isDebugEnabled;
/*!
 * 정보 로그 허용여부
 * @return 허용하면 YES, 그렇지 않으면 NO.
 */
-(BOOL) isInfoEnabled;
/*!
 * 경고 로그 허용여부
 * @return 허용하면 YES, 그렇지 않으면 NO.
 */
-(BOOL) isWarnEnabled;
/*!
 * 에러 로그 허용여부
 * @return 허용하면 YES, 그렇지 않으면 NO.
 */
-(BOOL) isErrorEnabled;

#define AX_LOG_TRACE(format,...) \
    if([[AxLog log] isTraceEnabled]) { \
        [[AxLog log] trace:(format), ##__VA_ARGS__]; \
    }

#define AX_LOG_DEBUG(format,...) \
    if([[AxLog log] isDebugEnabled]) { \
        [[AxLog log] debug:(format), ##__VA_ARGS__]; \
    }

#define AX_LOG_INFO(format,...) \
    if([[AxLog log] isInfoEnabled]) { \
        [[AxLog log] info:(format), ##__VA_ARGS__]; \
    }

#define AX_LOG_WARN(format,...) \
    if([[AxLog log] isWarnEnabled]) { \
        [[AxLog log] warn:(format), ##__VA_ARGS__]; \
    }

#define AX_LOG_ERROR(format,...) \
    if([[AxLog log] isErrorEnabled]) { \
        [[AxLog log] error:(format), ##__VA_ARGS__]; \
    }

@end
