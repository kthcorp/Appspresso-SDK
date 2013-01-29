/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */

#import <Foundation/Foundation.h>

/*!
 * This class represents a generic exception for whole appspresso runtime. You *SHOULD* extend this exception to represent more specific errors.
 */
@interface AxException : NSException {
    
}
/*!
 */
+(AxException*)exceptionWithMessage:(NSString*)message cause:(NSException*)cause;
/*!
 */
-(id)initWithMessage:(NSString*)message cause:(NSException*)cause;
/*!
 */
-(NSString*)message;
/*!
 */
-(NSException*)cause;

@end

/*!
 * This class represents a generic exception for whole appspresso runtime. You *SHOULD* extend this exception to represent more specific errors.
 * This is Exception Category using exception more conveniently
 */
@interface AxException (AxExceptionRaisingConveniences)

/*!
 */
+(void)raise;
/*!
 */
+(void)raiseWithMessage:(NSString*)message;
/*!
 */
+(void)raiseWithCause:(NSException*)cause;
/*!
 */
+(void)raiseWithMessage:(NSString*)message cause:(NSException*)cause;

@end
