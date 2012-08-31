//
//  AppspressoJSResponse.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

#import "AppspressoResponse.h"

/**
 * http://kraken.host:kraken.port/appspresso/appspresso.js 요청을 받으면,
 * assets/ax_scripts/ 디렉토리 아래에 있는
 * 1. keel.js를 뿌리고...
 * 2. 활성화된 모든 플러그인들의 자바스크립트 파일들을 적재된 순서대로 뿌리고...
 * 3. on-the-fly를 지원하기 위해 nessie와의 통신을 초기화하는 자바스크립트 코드를 뿌린다...
 *
 * TODO: 스트림화? 여러번 호출될 경우(멀티 페이지 웹앱)를 고려하여 캐싱? 자바스크립트 코드 암호화?
 */
@interface AppspressoJSResponse : AppspressoResponse
@end
