//
//  AppspressoJSResponse.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AppspressoJSResponse.h"
#import "HTTPConnection.h"
#import "DefaultPluginManager.h"
#import "DefaultWidgetAgent.h"
#import "AxConfig.h"
#import "MimeTypeUtils.h"
#import "HydraWebServer.h"

@implementation AppspressoJSResponse

- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value
{
    NSString *scriptsPath = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"assets/ax_scripts"];
    
    NSMutableData *result = [NSMutableData data];
    
    // 1. keel.js를 뿌리고...
    NSString *keelJsPath = [scriptsPath stringByAppendingPathComponent:@"keel.js"];
    [result appendData:[NSData dataWithContentsOfFile:keelJsPath]];
    
    // 2. 활성화된 모든 플러그인들의 자바스크립트 파일들을 적재된 순서대로 뿌리고...
    for(NSString* pluginId in [self.widgetAgent.pluginManager getPluginLoadedOrder]) {
        NSString *pluginJsPath = [scriptsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.js", pluginId]];
        [result appendData:[NSData dataWithContentsOfFile:pluginJsPath]];
    }

    // 3. on-the-fly를 지원하기 위해 nessie와의 통신을 초기화하는 자바스크립트 코드를 뿌린다...
    // XXX: 좀 더 우아한 방법을 찾아보자...
    NSString *nessieProject = [AxConfig getAttribute:@"nessie.project"];
    if(nessieProject != nil && [self isLocalConnection]) {
        NSString *nessieHost = [AxConfig getAttribute:@"nessie.host"];
        NSString *nessiePort = [AxConfig getAttribute:@"nessie.port"];
        NSString *consoleUrl = [NSString stringWithFormat:@"http://%@:%@/appspresso/CON$/%@",
                                nessieHost, nessiePort, nessieProject];
        NSString *logUrl = [NSString stringWithFormat:@"http://%@:%@/appspresso/LOG$/%@",
                            nessieHost, nessiePort, nessieProject];
        NSString *debugSessionUrl = [NSString stringWithFormat:@"http://%@:%@/appspresso/debug/session/issue/%@",
                            nessieHost, nessiePort, nessieProject];
        NSString *script = [NSString stringWithFormat:@";\
                            window._APPSPRESSO_CONSOLE_URL='%@';\
                            window._APPSPRESSO_LOG_URL='%@';\
                            window._APPSPRESSO_DEBUG_SESSION_ISSUE_URL='%@';\
                            ax.console.initDebugSession();\
                            ax.console.startRedirect();",
                            consoleUrl, logUrl, debugSessionUrl];
        [result appendData:[script dataUsingEncoding:NSUTF8StringEncoding]];
    }

    // 4. ADE 등 외부에서 접속했을 경우 jsonrpc polling 시작하게 한다.
    if (![self isLocalConnection]) {
        [result appendData:[@"ax.bridge.rpcpoll.start();" dataUsingEncoding:NSUTF8StringEncoding]];
    }

    [self setContentType:MIME_TYPE_JS];
    [self setCacheHeader];
    [self replyResponse:result];
}
    
@end
