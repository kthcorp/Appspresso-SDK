/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */


#import <Foundation/Foundation.h>

/*!
 * appspresso_config.plist 에 접근하여 런타임 내부에서 사용하는 설정을 읽음. <br>
 * @version 1.0
 */
@interface AxConfig : NSObject

/*!
 * plist 에서 키에 대한 밸류를 얻어옴
 * @param name
 *          키
 * @return 문자열로 된 키에 대한 밸류
 */
+(NSString*)getAttribute:(NSString*)name;
/*!
 * plist 에서 키에 대한 밸류를 얻어옴.<br>
 * 밸류가 없을 경우 defaultValue 리턴.
 * @param name
 *          키
 * @param defaultValue
 *          디폴트 값
 * @return 문자열로 된 키에 대한 밸류
 */
+(NSString*)getAttribute:(NSString*)name defaultValue:(NSString*)defaultValue;
/*!
 * plist 에서 키에 대한 밸류를 얻어옴.<br>
 * 밸류가 없을 경우 defaultValue 리턴.
 * @param name
 *          키
 * @param defaultValue
 *          디폴트 값
 * @return integer 형으로 된 키에 대한 밸류
 */
+(int)getAttributeAsInteger:(NSString*)name defaultValue:(int)defaultValue;
/*!
 * plist 에서 키에 대한 밸류를 얻어옴.<br>
 * 밸류가 없을 경우 defaultValue 리턴.
 * @param name
 *          키
 * @param defaultValue
 *          디폴트 값
 * @return BOOL형으로 된 키에 대한 밸류
 */
+(BOOL)getAttributeAsBoolean:(NSString*)name defaultValue:(BOOL)defaultValue;

@end
