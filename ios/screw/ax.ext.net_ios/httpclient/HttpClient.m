//
//  HttpClient.m
//  ProbeCore
//
//  Copyright 2010 KTH Corp.
//

#import "HttpClient.h"
#import "AxLog.h"
#import "AxError.h"

#define CONTENT_TYPE_FORM @"application/x-www-form-urlencoded"
#define CONTENT_TYPE_MULTIPART @"multipart/form-data"
#define CONTENT_TYPE_BINARY @"application/octet-stream"
#define	CONTENT_TRANSFER_ENCODING_BINARY @"binary"

// TODO: iOS 5에서 NSURLConnection 관련 delegate들이 왕창 바뀌었음... 확인 요망... -_-;;;;
// http://stackoverflow.com/questions/7803472/nsurlconnection-methods-no-more-available-in-ios5
// TODO: userAgent 헤더 바꿔치기...

@implementation HttpClient

@synthesize encoding = _encoding;
@synthesize download = _download;
@synthesize onSuccessBlock = _onSuccessBlock;
@synthesize onErrorBlock = _onErrorBlock;
@synthesize onSentBlock = _onSentBlock;
@synthesize onReceivedBlock = _onReceivedBlock;
@synthesize connection = _connection;
@synthesize request = _request;
@synthesize response = _response;

- (id)init {
	if((self = [super init])) {
        _encoding = NSUTF8StringEncoding;
        _download = nil;
        _onSuccessBlock = nil;
        _onErrorBlock = nil;
        _onSentBlock = nil;
        _onReceivedBlock = nil;
        _connection = nil;
        _request = nil;
        _response = nil;
        _responseData = nil;
        _responseFile = nil;
	}
	return self;
}

- (void)dealloc {
    //[_encoding release];
    [_download release];
	[_onSuccessBlock release];
	[_onErrorBlock release];
	[_onSentBlock release];
	[_onReceivedBlock release];
    [_connection release];
	[_request release];
	[_response release];    
    [_responseData release];
    [_responseFile release];
	[super dealloc];
}

- (void)execute:(NSURLRequest*)request {
	AX_LOG_TRACE(@"execute http request: request=%@", request);
    
    _request = [request retain];
	_connection = [[NSURLConnection alloc] initWithRequest:_request delegate:self];
	if(!_connection) { 
        @throw [AxError errorWithCode:AX_IO_ERR message:@"connection failed!" cause:nil];
	}
}

#pragma mark NSURLConnection delegate

- (void)connection:(NSURLConnection*)connection didReceiveResponse:(NSURLResponse*)response {
	AX_LOG_TRACE(@"%s", __PRETTY_FUNCTION__);
    
	_response = [(NSHTTPURLResponse*)response retain];
    
    int totalBytes = [response expectedContentLength];
    
    if(_download) {
        AX_LOG_TRACE(@"HttpClient response %d bytes to file %@", totalBytes, _download);
        
        [[NSFileManager defaultManager] createFileAtPath:_download contents:nil attributes:nil];
        _responseFile = [[NSFileHandle fileHandleForWritingAtPath:_download] retain];
    } else {
        AX_LOG_TRACE(@"HttpClient response %d bytes to memory", totalBytes);
        
        if(totalBytes != NSURLResponseUnknownLength) {
            _responseData = [[NSMutableData alloc] initWithCapacity:totalBytes];
        } else {
            _responseData = [[NSMutableData alloc] init];
        }
    }
}

- (void)connection:(NSURLConnection*)connection didReceiveData:(NSData *)data {
	AX_LOG_TRACE(@"%s", __PRETTY_FUNCTION__);
    
    int receivedBytes = 0;
    int totalBytes = [_response expectedContentLength];
    
    if(_responseFile) {
        [_responseFile writeData:data];
        receivedBytes = [_responseFile offsetInFile];
    } else {
        [_responseData appendData:data];
        receivedBytes = [_responseData length];
    }
    
	AX_LOG_TRACE(@"HttpClient received: %d %d %lld", [data length], receivedBytes, totalBytes);
    
	if(_onReceivedBlock) {
		_onReceivedBlock(self, receivedBytes, totalBytes);
	}
}

- (void)connection:(NSURLConnection*)connection didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite {
	AX_LOG_TRACE(@"%s", __PRETTY_FUNCTION__);
    
	AX_LOG_TRACE(@"HttpClient sent: %d %d %d", bytesWritten, totalBytesWritten, totalBytesExpectedToWrite);
    
	if(_onSentBlock) {
		_onSentBlock(self, totalBytesWritten, totalBytesExpectedToWrite);
	}
}

- (void)connection:(NSURLConnection*)connection didFailWithError:(NSError *)error {
	AX_LOG_TRACE(@"%s", __PRETTY_FUNCTION__);
    
	AX_LOG_TRACE(@"HttpClient error: error=%@", error);
    
	if(_onErrorBlock) {
		_onErrorBlock(self, AX_IO_ERR, [error description]);
	}
}

- (void)connectionDidFinishLoading:(NSURLConnection*)connection {
	AX_LOG_TRACE(@"%s", __PRETTY_FUNCTION__);
    
	if(_onSuccessBlock) {
        NSStringEncoding responseEncoding = _encoding;
        NSString *responseEncodingName = [_response textEncodingName];
        if(responseEncodingName != nil) {
            responseEncoding = [HttpClient encodingFromString:responseEncodingName];
        }
        NSString *data = [[[NSString alloc] initWithData:_responseData encoding:responseEncoding] autorelease];
		_onSuccessBlock(self, [_response statusCode], data, [_response allHeaderFields]);
	}
}

#pragma mark -

// http://stackoverflow.com/questions/2891327/convert-charset-name-to-nsstringencoding
+ (NSStringEncoding)encodingFromString:(NSString*)encoding {
    return CFStringConvertEncodingToNSStringEncoding(CFStringConvertIANACharSetNameToEncoding((CFStringRef)encoding));
}

+ (NSString*)encodeQueryString:(NSDictionary*)params encoding:(NSStringEncoding)encoding {
	NSMutableString *result = [NSMutableString string];
	BOOL isFirstParam = YES;
	for (NSString *paramName in params) {
		if (isFirstParam) {
			isFirstParam = NO;
		} else {
			[result appendString:@"&"];					
		}
		[result appendString:[paramName stringByAddingPercentEscapesUsingEncoding:encoding]];
		[result appendString:@"="];
		[result appendString:[[[params objectForKey:paramName] description] stringByAddingPercentEscapesUsingEncoding:encoding]];
	}
	//AX_LOG_TRACE(@"encodeQueryString:----------\n%@\n----------", result);
	return result;
}

// TODO: rewrite using stream!
+ (void)encodeMultipartFormData:(NSDictionary*)params intoRequest:(NSMutableURLRequest*)request encoding:(NSStringEncoding)encoding {
	NSString *boundary = [NSString stringWithFormat:@"%dx", [NSDate timeIntervalSinceReferenceDate]];

    NSMutableData *data = [NSMutableData data];

	for(NSString *name in params) {
        [data appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:encoding]];
        id value = [params objectForKey:name];
        if([value isKindOfClass:[NSURL class]] && [value isFileURL]) { // file
            AX_LOG_TRACE(@"\tfile: %@=%@ ", name, value);
            NSString *filePath = [(NSURL*)value path];
            [data appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n", name, [filePath lastPathComponent]] dataUsingEncoding:NSUTF8StringEncoding]];
            [data appendData:[[NSString stringWithFormat:@"Content-Type: %@\r\n\r\n", CONTENT_TYPE_BINARY] dataUsingEncoding:encoding]];
            [data appendData:[NSData dataWithContentsOfFile:filePath]];
        } else { // form field
            AX_LOG_TRACE(@"\tform: %@=%@", name, value);
            [data appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n", name] dataUsingEncoding:encoding]];
            [data appendData:[[value description] dataUsingEncoding:NSUTF8StringEncoding]];
        }
        [data appendData:[@"\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
	}
		
	[data appendData:[[NSString stringWithFormat:@"--%@--\r\n", boundary] dataUsingEncoding:encoding]];

	[request setValue:[NSString stringWithFormat:@"multipart/form-data;boundary=%@", boundary] forHTTPHeaderField:@"Content-Type"];
	[request setValue:[NSString stringWithFormat:@"%d", [data length]] forHTTPHeaderField:@"Content-Length"];
    [request setHTTPBody:data];
    //AX_LOG_TRACE(@"data(%d){{{{{%@}}}}}", [data length], data);
}

+ (NSURLRequest*)newHttpRequest:(NSString*)method
                            url:(NSURL*)url
                        headers:(NSDictionary*)headers
                           data:(id)data
                       encoding:(NSStringEncoding)encoding
                      multipart:(BOOL)multipart
{
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
	[request setHTTPMethod:method];
    
	if(headers) {
		// TRACE(@"request headers:%@", headers);
		// this doesn't work for non-string header values
		// [_request setAllHTTPHeaderFields:headers];
		AX_LOG_TRACE(@"request headers:");
		for (NSString *headerName in headers) {
			AX_LOG_TRACE(@"\t%@=%@", headerName, [headers objectForKey:headerName]);
			[request setValue:[[headers objectForKey:headerName] description] forHTTPHeaderField:headerName];
		}
	}
    
	if([@"GET" isEqualToString:method] || [@"HEAD" isEqualToString:method]) {
		// NSAsset(!data || data == NSNull.null, @"no request body is allowed for get/head methods");
	} else if(data && data != NSNull.null) {		
		AX_LOG_TRACE(@"request data: class=%@", [data class]);
		if ([data isKindOfClass:[NSString class]]) {
			if([(NSString*)data length] > 0) {
				[request setHTTPBody:[(NSString*)data dataUsingEncoding:encoding]];
			}
		} else if([data isKindOfClass:[NSData class]]) {
			if([(NSData*)data length] > 0) {
				[request setHTTPBody:data];
			}
		} else if([data isKindOfClass:[NSInputStream class]]) {
			[request setHTTPBodyStream:(NSInputStream *)data];
		} else if([data isKindOfClass:[NSDictionary class]]) {
            if(multipart) {
                //[request setValue:CONTENT_TYPE_MULTIPART forHTTPHeaderField:@"Content-Type"];
                //[request setHTTPBody:];
                [HttpClient encodeMultipartFormData:data intoRequest:request encoding:encoding];
            } else {
                [request setValue:CONTENT_TYPE_FORM forHTTPHeaderField:@"Content-Type"];
                [request setHTTPBody:[[HttpClient encodeQueryString:(NSDictionary*)data encoding:encoding] dataUsingEncoding:encoding]];
            }
            //} else if([data isKindOfClass:[HttpEntity class]]);
            // ... similar to HttpCore?
		} else {
            [request release];
            @throw [AxError errorWithCode:AX_IO_ERR message:[NSString stringWithFormat:@"unexpected data parameter type: %@", [data class]] cause:nil];
		}
	}
    
    return request;
}

@end
