/*
 * Author: Landon Fuller <landonf@plausiblelabs.com>
 * Copyright (c) 2008 Plausible Labs Cooperative, Inc.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

 /**
  * Modifications made by Appcelerator, Inc. licensed under the
  * same license as above.
  */

#import "iPhoneSimulator.h"
#import "nsprintf.h"

#define SDK_PATH @"Platforms/iPhoneSimulator.platform/Developer/SDKs"

/**
 * A simple iPhoneSimulatorRemoteClient framework.
 */
@implementation iPhoneSimulator

- (NSString *)getSDKVersion:(NSString *)path
{
	NSRange prefix = [path rangeOfString:@"iPhoneSimulator"];
	if (0 != prefix.location) {
		return nil;
	}
	NSRange suffix = [path rangeOfString:@".sdk"];
	if (NSNotFound == suffix.location) {
		return nil;
	}
	NSRange range = NSMakeRange(prefix.length, suffix.location-prefix.length);
	return [path substringWithRange:range];
}

- (NSArray *)getSDKRoots
{
	NSString *path = [_xcodeRootPath stringByAppendingPathComponent:SDK_PATH];
	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	
	return [fm contentsOfDirectoryAtPath:path error:nil];
}

/**
 * Print usage.
 */
- (void) printUsage {
    fprintf(stderr, "Usage: iphonesim <options> <command> ...\n");
    fprintf(stderr, "Commands:\n");
    fprintf(stderr, "  showsdks <xcodeRootPath>\n");
    fprintf(stderr, "  launch <xcodeRootPath> <application path> [sdkversion] [family] [uuid]\n");
}


/**
 * List available SDK roots.
 */
- (int) showSDKs {
	NSArray *roots = [self getSDKRoots];
	for (NSString *sdk in roots) {
		NSString *version = [self getSDKVersion:sdk];
		if (nil != version) {
			nsfprintf(stdout, @"%@", version);
		}
	}
	
    return EXIT_SUCCESS;
}

- (void) session: (DTiPhoneSimulatorSession *) session didEndWithError: (NSError *) error {
    nsprintf(@"Session did end with error %@", error);
    
    if (error != nil)
        exit(EXIT_FAILURE);

    exit(EXIT_SUCCESS);
}


- (void) session: (DTiPhoneSimulatorSession *) session didStart: (BOOL) started withError: (NSError *) error {
    if (started) {
        nsprintf(@"Session started");
        exit(EXIT_SUCCESS);
    } else {
        nsprintf(@"Session could not be started: %@", error);
        exit(EXIT_FAILURE);
    }
}


/**
 * Launch the given Simulator binary.
 */
- (int) launchApp: (NSString *) path withFamily:(NSString*)family uuid:(NSString*)uuid{
    DTiPhoneSimulatorApplicationSpecifier *appSpec;
    DTiPhoneSimulatorSessionConfig *config;
    DTiPhoneSimulatorSession *session;
    NSError *error;

    /* Create the app specifier */
    appSpec = [DTiPhoneSimulatorApplicationSpecifier specifierWithApplicationPath: path];
    if (appSpec == nil) {
        nsprintf(@"Could not load application specification for %s", path);
        return EXIT_FAILURE;
    }
//    nsprintf(@"App Spec: %@", appSpec);

    /* Load the default SDK root */
    
//    nsprintf(@"SDK Root: %@", sdkRoot);

    /* Set up the session configuration */
    config = [[[DTiPhoneSimulatorSessionConfig alloc] init] autorelease];
    [config setApplicationToSimulateOnStart: appSpec];
    [config setSimulatedSystemRoot: sdkRoot];
    [config setSimulatedApplicationShouldWaitForDebugger: NO];

    [config setSimulatedApplicationLaunchArgs: [NSArray array]];
    [config setSimulatedApplicationLaunchEnvironment: [NSDictionary dictionary]];

    [config setLocalizedClientName: @"AppspressoDeveloper"];

	// this was introduced in 3.2 of SDK
	if ([config respondsToSelector:@selector(setSimulatedDeviceFamily:)])
	{
		if (family == nil)
		{
			family = @"iphone";
		}

//		nsprintf(@"using device family %@",family);

		if ([family isEqualToString:@"ipad"])
		{
			[config setSimulatedDeviceFamily:[NSNumber numberWithInt:2]];
		}
		else
		{
			[config setSimulatedDeviceFamily:[NSNumber numberWithInt:1]];
		}
	}

    /* Start the session */
    session = [[[DTiPhoneSimulatorSession alloc] init] autorelease];
    [session setDelegate: self];
    [session setSimulatedApplicationPID: [NSNumber numberWithInt: 35]];
	if (uuid!=nil)
	{
		[session setUuid:uuid];
	}

    if (![session requestStartWithConfig: config timeout: 30 error: &error]) {
        nsprintf(@"Could not start simulator session: %@", error);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}


/**
 * Execute 'main'
 */
- (void) runWithArgc: (int) argc argv: (char **) argv {
    /* Read the command */
    if (argc < 3) {
        [self printUsage];
        exit(EXIT_FAILURE);
    }
	
	_xcodeRootPath = [NSString stringWithUTF8String:argv[2]];

    if (strcmp(argv[1], "showsdks") == 0) {
        exit([self showSDKs]);
    }
    else if (strcmp(argv[1], "launch") == 0) {
        /* Requires an additional argument */
        if (argc < 4) {
            fprintf(stderr, "Missing application path argument\n");
            [self printUsage];
            exit(EXIT_FAILURE);
        }
        if (argc > 4) {
            NSString* ver = [NSString stringWithCString:argv[4] encoding:NSUTF8StringEncoding];
            NSArray *roots = [self getSDKRoots];
            for (NSString *sdk in roots) {
                NSString *v = [self getSDKVersion:sdk];
                if (nil != v && [v isEqualToString:ver])
                {
					NSString *sdkroot = [_xcodeRootPath stringByAppendingPathComponent:SDK_PATH];
                    sdkRoot = [DTiPhoneSimulatorSystemRoot rootWithSDKPath:[sdkroot stringByAppendingPathComponent:sdk]];
                    break;
                }
            }
            if (sdkRoot == nil)
            {
                fprintf(stderr,"Unknown or unsupported SDK version: %s\n",argv[4]);
                [self showSDKs];
                exit(EXIT_FAILURE);
            }
        }
        else {
            sdkRoot = [DTiPhoneSimulatorSystemRoot defaultRoot];
        }

        /* Don't exit, adds to runloop */
		NSString *family = nil;
		NSString *uuid = nil;
		if (argc > 5)
		{
			family = [NSString stringWithUTF8String:argv[5]];
		}
		if (argc > 6)
		{
			uuid = [NSString stringWithUTF8String:argv[6]];
		}
        [self launchApp: [NSString stringWithUTF8String: argv[3]] withFamily:family uuid:uuid];
    } else {
        fprintf(stderr, "Unknown command\n");
        [self printUsage];
        exit(EXIT_FAILURE);
    }
}

@end
