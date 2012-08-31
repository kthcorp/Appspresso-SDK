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

//
// deviceapis
//

#define AX_UNKNOWN_ERR 0

#define AX_INDEX_SIZE_ERR 1
#define AX_DOMSTRING_SIZE_ERR 2
#define AX_HIERARCHY_REQUEST_ERR 3
#define AX_WRONG_DOCUMENT_ERR 4
#define AX_INVALID_CHARACTER_ERR 5
#define AX_NO_DATA_ALLOWED_ERR 6
#define AX_NO_MODIFICATION_ALLOWED_ERR 7
#define AX_NOT_FOUND_ERR 8
#define AX_NOT_SUPPORTED_ERR 9

#define AX_INUSE_ATTRIBUTE_ERR 10
#define AX_INVALID_STATE_ERR 11
#define AX_SYNTAX_ERR 12
#define AX_INVALID_MODIFICATION_ERR 13
#define AX_NAMESPACE_ERR 14
#define AX_INVALID_ACCESS_ERR 15
#define AX_VALIDATION_ERR 16
#define AX_TYPE_MISMATCH_ERR 17
#define AX_SECURITY_ERR 18
#define AX_NETWORK_ERR 19
#define AX_ABORT_ERR 20
#define AX_TIMEOUT_ERR 21
#define AX_INVALID_VALUES_ERR 22

//
// filesystem
//

#define AX_IO_ERR 100

//
// devicestatus
//

#define AX_NOT_AVAILABLE_ERR 101

// ... more predefined error codes here...
// #define AX_FOO_ERR = ???;

/**
 * all custom error codes are bigger than this value.
 */
#define AX_CUSTOM_ERR_FLAG 0x10000

//
// common error messages
//

#define AX_UNKNOWN_ERR_MSG @"Unknown error"
#define AX_NOT_FOUND_ERR_MSG @"NotFound error"
#define AX_NOT_SUPPORTED_ERR_MSG @"This feature is not supported"
#define AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG @"This feature is not supported on simulator"
#define AX_TYPE_MISMATCH_ERR_MSG @"Type mismatch error"
#define AX_INVALID_VALUES_ERR_MSG @"Invalid Values error"
#define AX_NOT_AVAILABLE_ERR_INVALID_HANDLE_MSG @"Handle is invalid"
#define AX_SECURITY_ERR_MSG @"The operation is not allowed"

// NOTE: not AxException but NSError!
// TODO: change android api too! and remove this comment
/*!
 * This class represents an plugin method execution error for appspresso runtime. All predefined errors are based-on DeviceAPIError in Waikiki API. <br>
 * When you need custom error codes, Do *NOT* use magic number like this: throw new AxError(123); <br>
 * Do use toErrorCode(String) to generate an unqiue error code: private static final int CUSTOM_ERR = AxError.toErrorCode("Custom Error"); ... throw new AxError(CUSTOM_ERR); <br>
 * NOTE: Do *NOT* throw this exception out-of a plugin context. <br>
 */
@interface AxError : NSError {
    
}
/*!
 */
+(int)toErrorCode:(NSString*)errorId;
/*!
 */
+(AxError*)errorWithCode:(NSInteger)code message:(NSString*)message cause:(NSException*)cause;
/*!
 */
-(id)initWithCode:(NSInteger)code message:(NSString*)message cause:(NSException*)cause;
/*!
 */
-(NSString*)message;
/*!
 */
-(NSException*)cause;

@end

