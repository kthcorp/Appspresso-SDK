//
//  HttpClient.h
//  ProbeCore
//
//  Copyright 2010 KTH Corp.
//

#import <Foundation/Foundation.h>

@class HttpClient;

typedef void (^HttpClientOnSuccessBlock)(HttpClient *httpClient, int status, NSString *data, NSDictionary *headers);
typedef void (^HttpClientOnErrorBlock)(HttpClient *httpClient, int code, NSString *message);
typedef void (^HttpClientOnSentBlock)(HttpClient *httpClient, int sentBytes, int totalBytes);
typedef void (^HttpClientOnReceivedBlock)(HttpClient *httpClient, int receivedBytes, int totalBytes);

/**
 * This interface provides a simple HTTP client capabilities.
 * 
 */
@interface HttpClient : NSObject {
@private
    NSStringEncoding _encoding;
    NSString *_download;
    
	HttpClientOnSuccessBlock _onSuccessBlock;
	HttpClientOnErrorBlock _onErrorBlock;
	HttpClientOnSentBlock _onSentBlock;
	HttpClientOnReceivedBlock _onReceivedBlock;
    
    NSURLConnection *_connection;
	NSURLRequest *_request;
	NSHTTPURLResponse *_response;
    NSMutableData *_responseData;
    NSFileHandle *_responseFile;
}

@property (readwrite,assign) NSStringEncoding encoding;
@property (readwrite,retain) NSString *download;
@property (readwrite,copy) HttpClientOnSuccessBlock onSuccessBlock;
@property (readwrite,copy) HttpClientOnErrorBlock onErrorBlock;
@property (readwrite,copy) HttpClientOnSentBlock onSentBlock;
@property (readwrite,copy) HttpClientOnReceivedBlock onReceivedBlock;
@property (readonly,retain) NSURLConnection *connection;
@property (readonly,retain) NSURLRequest *request;
@property (readonly,retain) NSURLResponse *response;

- (void)execute:(NSURLRequest*)request;

+ (NSStringEncoding)encodingFromString:(NSString*)encodingName;

+ (NSString*)encodeQueryString:(NSDictionary*)params
                      encoding:(NSStringEncoding)encoding;

+ (void)encodeMultipartFormData:(NSDictionary*)files
                    intoRequest:(NSMutableURLRequest*)request
                       encoding:(NSStringEncoding)encoding;

+ (NSURLRequest*)newHttpRequest:(NSString*)method
                            url:(NSURL*)url
                        headers:(NSDictionary*)headers
                           data:(id)data
                       encoding:(NSStringEncoding)encoding
                      multipart:(BOOL)multipart;

@end
