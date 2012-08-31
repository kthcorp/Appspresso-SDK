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
