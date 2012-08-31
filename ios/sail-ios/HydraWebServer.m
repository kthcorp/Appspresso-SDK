//
//  HydraWebServer.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <sqlite3.h>
#import "HTTPMessage.h"

#import "HydraWebServer.h"
#import "AppspressoJSResponse.h"
#import "AppspressoPluginResponse.h"
#import "AppspressoRpcPollResponse.h"
#import "WidgetContentHandler.h"
#import "OnTheFlyWidgetContentHandler.h"
#import "FileSystemContentHandler.h"
#import "AxConfig.h"
#import "AxLog.h"

//=======================================================

@implementation HydraWebServer

@synthesize startPage = _startPage;

#pragma mark -

static HydraWebServer* g_ax_hydraWebServer;

#define ANY_LOCAL_HOST @"0.0.0.0"
#define DEF_HYDRA_HOST @"localhost"
static const int DEF_HYDRA_PORT = 50010;

#pragma mark -

- (void)initHydra {
    AX_LOG_TRACE(@"*** Appspresso ***");
    _host = [AxConfig getAttributeAsBoolean:@"app.devel" defaultValue:NO] ? ANY_LOCAL_HOST : [AxConfig getAttribute:@"hydra.host" defaultValue:DEF_HYDRA_HOST];
    _port = [AxConfig getAttributeAsInteger:@"hydra.port" defaultValue:DEF_HYDRA_PORT];
             
    _hydra = [[HTTPServer new] autorelease];
    //_hydra =[[HTTPServer alloc] init];
    //[_hydra setType:@"_http._tcp."];
    [_hydra setConnectionClass:[HydraConnection class]];
    [_hydra setDocumentRoot:[[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"assets/ax_www/"]];
    [_hydra setPort:_port];
    [_hydra retain];
}

- (void)startHydra {
    NSError *error;
    if(![_hydra start:&error]) {
        AX_LOG_TRACE(@"Error starting HTTP Server: %@", error);
    }
    //_host = [_hydra listeningHost];
    _port = [_hydra listeningPort];
}

-(void)resumeHydra {
    NSError *error;
	[_hydra setPort:_port];
	if(_hydra && ![_hydra isRunning] && ![_hydra start:&error]) {
		AX_LOG_TRACE(@"Error starting HTTP Server: %@", error);
	}	
}

-(void)pauseHydra {
    if (_hydra && [_hydra isRunning]) {
		[_hydra stop];
	}
}

//TODO : iOS 5.1 change the path of localstorage and database.
static NSString *LOCALSTORAGE_PATH = @"WebKit/LocalStorage/";
static NSString *LOCALSTORAGE_PATH_5_1 = @"Caches/";
static NSString *DB_FILE_PATH = @"WebKit/Databases/";
static NSString *DB_FILE_PATH_5_1 = @"Caches/";

- (BOOL)isIOS5_1OrHigher
{
    // based on: http://stackoverflow.com/a/9320041
    NSArray *versionCompatibility = [[UIDevice currentDevice].systemVersion componentsSeparatedByString:@"."];
    
    if ( [[versionCompatibility objectAtIndex:0] intValue] > 5 ){
        return YES; // iOS 6+
    }
    
    if ( [[versionCompatibility objectAtIndex:0] intValue] < 5 ){
        return NO;  // iOS 4.x or lower
    }
    
    if ( [[versionCompatibility objectAtIndex:1] intValue] >= 1 ){
        return YES; // ios 5.<<1>> or higher
    }
    
    return NO;  // ios 5.<<0.x>> or lower
    
}

- (void)adjustLocalStorageAndDatabase
{
	//todo : merge table for sqllite3 and localstorage
	NSString *nowOrigin = [NSString stringWithFormat:@"http_%@_%d",_host,_port];
	
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
    NSString *libraryDirectory = [paths objectAtIndex:0];
	NSString *fileFormat = @".localstorage";
	
	NSString *storageFileName = nil;
	NSString *storagePath = [libraryDirectory stringByAppendingPathComponent:([self isIOS5_1OrHigher])?LOCALSTORAGE_PATH_5_1:LOCALSTORAGE_PATH];
	NSFileManager *storageManager = [[NSFileManager alloc] init];
	NSArray *storageFileArray = [storageManager contentsOfDirectoryAtPath:storagePath error:nil];
	for (storageFileName in storageFileArray) {
		if ([storageFileName hasSuffix:fileFormat]) {
			break;
		}
	}
	//AX_LOG_TRACE(@"storageFileName = %@",storageFileName);
	if (storageFileName != nil) {
		NSString *oldOriginFile = [NSString stringWithFormat:@"%@/%@",storagePath,storageFileName];
		NSString *nowOriginFile = [NSString stringWithFormat:@"%@/%@%@",storagePath,nowOrigin,fileFormat];
		AX_LOG_TRACE(@"oldOriginFile = %@",oldOriginFile);
		AX_LOG_TRACE(@"nowOriginFile = %@",nowOriginFile);
		
		if (![oldOriginFile isEqualToString:nowOriginFile]) {
			[storageManager copyItemAtPath:oldOriginFile toPath:nowOriginFile error:nil];
			[storageManager removeItemAtPath:oldOriginFile error:nil];
		}
	}
	
	[storageManager release];
	
	NSString *dbPath = [libraryDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@Databases.db",([self isIOS5_1OrHigher])?DB_FILE_PATH_5_1:DB_FILE_PATH]];
	
	sqlite3 *database;
    if (sqlite3_open([dbPath UTF8String], &database) != SQLITE_OK) {
        sqlite3_close(database);
	}
	else {
		//Todo : change db data and move directory
		
		NSString *oldOrigin = nil;
		
		sqlite3_stmt *selectStatement;
		char *selectSql = "SELECT origin FROM Origins";
		if (sqlite3_prepare_v2(database, selectSql, -1, &selectStatement, NULL) == SQLITE_OK) {
			while (sqlite3_step(selectStatement)==SQLITE_ROW) {
				oldOrigin = [NSString stringWithUTF8String:(char *)sqlite3_column_text(selectStatement, 0) ];
				AX_LOG_TRACE(@"origin : %@", oldOrigin);
			}
		}
		
		if (oldOrigin != nil) {
			NSString *oldOriginPath = [libraryDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@",([self isIOS5_1OrHigher])?DB_FILE_PATH_5_1:DB_FILE_PATH,oldOrigin]];
			NSString *nowOriginPath = [libraryDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@",([self isIOS5_1OrHigher])?DB_FILE_PATH_5_1:DB_FILE_PATH,nowOrigin]];
			
			if (![oldOriginPath isEqualToString:nowOriginPath]) {
				NSFileManager *fm = [[NSFileManager alloc] init];
				[fm moveItemAtPath:oldOriginPath toPath:nowOriginPath error:nil];
				[fm removeItemAtPath:oldOriginPath error:nil];
				[fm release];
				
				AX_LOG_TRACE(@"OldPath(%@) to NewPath(%@)", oldOriginPath, nowOriginPath);
				
				sqlite3_stmt *insertStatement;
				NSString *insertSql = [NSString stringWithFormat:@"UPDATE Origins SET origin = ? WHERE origin = '%@';",oldOrigin];
				if (sqlite3_prepare_v2(database, [insertSql UTF8String], -1, &insertStatement, NULL) == SQLITE_OK) {
					sqlite3_bind_text(insertStatement, 1, [nowOrigin UTF8String],  -1, SQLITE_TRANSIENT);
					if (sqlite3_step(insertStatement) != SQLITE_DONE) {
						AX_LOG_TRACE(@"Replace Error");
					}
				}
				insertSql = [NSString stringWithFormat:@"UPDATE Databases SET origin = ? WHERE origin = '%@';",oldOrigin];
				if (sqlite3_prepare_v2(database, [insertSql UTF8String], -1, &insertStatement, NULL) == SQLITE_OK) {
					sqlite3_bind_text(insertStatement, 1, [nowOrigin UTF8String],  -1, SQLITE_TRANSIENT);
					if (sqlite3_step(insertStatement) != SQLITE_DONE) {
						AX_LOG_TRACE(@"Replace Error");
					}
				}
				AX_LOG_TRACE(@"Update old data '%@' to now data '%@'", oldOrigin, nowOrigin);
				
				sqlite3_finalize(insertStatement);
			}
		}
		else {
			AX_LOG_TRACE(@"there's no data in table named Origins");
		}
		
		sqlite3_finalize(selectStatement);
		sqlite3_close(database);
	}
	
}

// XXX: 이걸 왜 밖에서 부르는 거야?
-(void)start{
    [self startHydra];
    [self adjustLocalStorageAndDatabase];
}

- (void)addHandlerClass:(Class)handler forPrefix:(NSString *)prefix
{
	[_handlers setObject:handler forKey:prefix];
    [_handlersInOrder addObject:prefix];
}

-(AppspressoResponse*)httpResponseForMethod:(NSString *)method URI:(NSString *)path connection:(HydraConnection*)connection
{
    for(NSString *prefix in _handlersInOrder) {
        // prefix가 일치하는 핸들러 발견...
        if([path hasPrefix:prefix]) {
            id handlerClass = [_handlers objectForKey:prefix];
            // TODO: 인스턴스 캐싱?
            // XXX: 이거 매번 인스턴스를 생성해야 하는겨?
            AppspressoResponse *response = [[handlerClass alloc] initWithWidgetAgent:_widgetAgent connection:connection];
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                [response handleRequestForApi:prefix value:[path substringFromIndex:[prefix length]]];
            });
            return [response autorelease];
        }
    }
	// 404 not found
    return nil;
}

#pragma mark -

- (id)initWithWidgetAgent:(DefaultWidgetAgent*)widgetAgent {
    if((self = [super init])) {
        g_ax_hydraWebServer = self;
        [self initHydra];
        
        _widgetAgent = [widgetAgent retain];
        _handlers = [[NSMutableDictionary alloc] init];
        _handlersInOrder = [[NSMutableArray alloc] init];
        _startPage = nil;
        
        // NOTE: 순서가 중요함!
        
        // /appspresso/appspresso.js - keel and plugin javascripts
        [self addHandlerClass:[AppspressoJSResponse class] forPrefix:@"/appspresso/appspresso.js"];
        // /appspresso/plugin - plugin jsonrpc
        [self addHandlerClass:[AppspressoPluginResponse class] forPrefix:kApiKeyPlugin];
        // /appspresso/rpcpoll - jsonrpc response | notification poll
        [self addHandlerClass:[AppspressoRpcPollResponse class] forPrefix:kApiKeyRpcPoll];
        // TODO: /appspresso/CON$/ - console
        //[self addHandler:[ConsoleHandler class] forPrefix:@"/appspresso/CON$"];
        // TODO: /appspresso/LOG$/ - log
        //[self addHandlerClass:[LogHandler class] forPrefix:@"/appspresso/LOG$"];
        // TODO: /appspresso/file/* - filesystem toURI() content...
        [self addHandlerClass:[FileSystemContentHandler class] forPrefix:@"/appspresso/file/"];
        // /* - widget content ...
        if ([AxConfig getAttribute:@"nessie.project"] != nil) {
            // /* - on-the-fly widget content ...
            [self addHandlerClass:[OnTheFlyWidgetContentHandler class] forPrefix:@"/"];
        } else {
            // /* - static widget content ...
            [self addHandlerClass:[WidgetContentHandler class] forPrefix:@"/"];
        }
        
    }
    return self;
}

- (void)dealloc {
    [_hydra release];
    [_handlers release];
    [_handlersInOrder release];
    [_widgetAgent release];
    [super dealloc];
}

#pragma mark WebServer

-(NSString*)getHost {
    return _host;
}

-(int)getPort {
    return _port;
}

#pragma mark Authenticate

-(HydraWebServer*)generateAuthPageWithStartPage:(NSString*)startPage {
    // iOS 에서는 핸들러가 그때그때 생성. 안타깝지만 hydra에 저장한다.
    _startPage = startPage;
    return self;
}

-(NSString*)getAuthPageUrl {
    return [NSString stringWithFormat:@"http://%@:%d/appspresso/auth.html", _host, _port];
}

#pragma mark UIApplicationDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    //[self start];
    return YES;
}

-(void)applicationDidEnterBackground:(UIApplication*)application {
    // 백그라운드로 내려갈때 웹서버 정지???
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [self pauseHydra];
}

-(void)applicationWillEnterForeground:(UIApplication*)application {
    // 백그라운드로 내려갈때 웹서버를 정지했다면 지금 다시 시작~
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [self resumeHydra];
}

@end

//=======================================================

@implementation HydraConnection

#pragma mark -

-(HTTPMessage*)request {
    return request;
}

-(NSString*)requestMethod {
    return [request method];
}

-(NSData*)requestBody {
    return [request body];
}

-(NSString*)requestBodyAsString {
    return [[[NSString alloc] initWithData:[request body] encoding:NSUTF8StringEncoding] autorelease];
}

-(NSString*)getRequestHeaderValueForKey:(NSString*)key {
    return [request headerField:key];
}

#pragma mark -

- (BOOL)supportsMethod:(NSString *)method atPath:(NSString *)relativePath {
    return YES;
}

- (void)processBodyData:(NSData *)postDataChunk {
    // TODO: 요청 데이터가 너무 클땐 어떻게? 임시 파일 만들어서... 어쩌구 저쩌구...
    if(![request appendData:postDataChunk]) {
        AX_LOG_ERROR(@"processBodyData: too large request!");
    }
}

- (NSObject<HTTPResponse> *)httpResponseForMethod:(NSString *)method URI:(NSString*)path {
	AX_LOG_TRACE(@"httpResponseForMethod:%@ URI:%@", method, path);
    return [g_ax_hydraWebServer httpResponseForMethod:method URI:path connection:self];
}

@end
