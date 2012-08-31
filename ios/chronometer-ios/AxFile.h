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

@protocol AxFile;

@protocol AxFileFilter;

typedef enum {
	AxFileOpenForReadOnly,
	AxFileOpenForWriteOnly,
	AxFileOpenForReadWrite,
} AxFileOpenMode;

/*!
 * 앱스프레소의 가상 파일.
 * @version 1.0
 */
@protocol AxFile 

/*!
 * 파일 이름을 반환
 * @return 파일 이름
 */
-(NSString*)getName;
/*!
 * 파일시스템의 이름으로 시작하는 가상 파일 경로를 반환
 * @return 파일의 가상 경로
 */
-(NSString*)getPath;
/*!
 * 부모 파일을 반환
 * @return 부모 파일의 인스턴스. 단, 파일이 root 이면 nil 을 반환.
 */
-(NSObject<AxFile>*)getParent;

/*!
 * 파일인지 아닌지 식별함.
 * @return 파일이면 YES, 아니면 NO
 */
-(BOOL)isFile;
/*!
 * 디렉토리인지 아닌지 식별함.
 * @return 디렉토리이면 YES, 아니면 NO
 */
-(BOOL)isDirectory;
/*!
 * 파일의 크기(byte)를 반환. 
 * @return 파일이면 파일 사이즈를 리턴함. 디렉토리이면 하위 파일 갯수를 리턴.
 */
-(NSUInteger)getLength;
/*!
 * 생성일자를 반환.
 * @return NSDate 생성일
 */
-(NSDate*)getCreated;
/*!
 * 파일이 마지막으로 수정된 일자를 반환.
 * @return NSDate 수정일
 */
-(NSDate*)getModified;
/*!
 * 파일의 존재 여부를 반환.
 * @return 존재하면 YES, 아니면 NO
 */
-(BOOL)exists;
/*!
 * 파일의 읽기 가능 여부를 반환.
 * @return 읽을 수 있으면 YES, 아니면 NO
 */
-(BOOL)canRead;
/*!
 * 파일의 쓰기 가능 여부를 반환.
 * @return 쓸 수 있으면 YES, 아니면 NO
 */
-(BOOL)canWrite;

/*!
 * 바이트 배열 형식으로 파일의 내용을 읽음.
 *
 * @return 내용 바이트 배열
 */
-(NSData*)getContentsAsData;

/*!
 * 바이트 배열 형식으로 파일의 내용을 변경.
 *
 * @param data 
 *           내용
 */
-(void)setContentsWithData:(NSData*)data;

/*!
 * (UTF-8로 인코딩된)문자열 형식으로 파일의 내용을 읽음.
 *
 * @return 내용 문자열
 */
-(NSString*)getContentsAsString;

/*!
 * (UTF-8로 인코딩된)문자열 형식으로 파일의 내용을 변경.
 *
 * @param str 
 *         내용
 */
-(void)setContentsWithString:(NSString*)str;

/*!
 * 지정된 인코딩을 가진 문자열 형식으로 파일의 내용을 읽음.
 *
 * @param encoding 문자열 인코딩
 * @return 내용 문자열
 */
-(NSString*)getContentsAsString:(NSString*)encoding;

/*!
 * 지정된 인코딩을 가진 문자열 형식으로 파일의 내용을 변경.
 *
 * @param str 
 *         내용
 * @param encoding 
 *         문자열 인코딩
 */
-(void)setContentsWithString:(NSString*)str encoding:(NSString*)encoding;

/*!
 * 해당 조건을 만족하는 하위 파일의 목록을 리턴함.
 *
 * @param filter
 *          AxFileFilter 로 구현된 필터링 할 조건. nil일 경우 모든 파일을 가져옴.
 * @return 파일객체로 이루어진 NSArray
 */
-(NSArray*)listFiles : (NSObject<AxFileFilter>*) filter;

/*!
 * 해당 파일 객체를 식별할 데이터를 반환.
 *
 * @return 객체 식별용 고유값
 */
-(id)getPeer;

/*!
 * 읽고 쓰기 위한 상태로 파일을 연다.
 *
 * @param mode
 *          0=read,1=write,2=read/write
 * @return 열릴 경우 YES, 아닐 경우 NO.
 */
-(BOOL)open:(int)mode;//0=read,1=write,2=read/write
/*!
 * 파일을 닫음.
 */
-(void)close;
/*!
 * 파일포인터가 파일의 끝에 위치하고 있는지 확인.
 *
 * @return eof 일 경우 YES, 아닐 경우 NO.
 */
-(BOOL)isEof;
/*!
 * 지정한 위치로 파일포인터를 이동.
 *
 * @param position
 *           파일포인터를 이동시킬 위치
 */
-(void)seek:(NSUInteger)position;
/*!
 * 파일포인터의 현재 위치를 반환.
 *
 * @return 파일포인터의 현재 위치
 */
-(NSUInteger)getPosition;
/*!
 * 현재의 파일포인터의 위치부터 지정한 크기만큼 읽음. 앞으로 읽을 수 있는 크기가 지정한 크기보다 작을 경우 가능한 크기 만큼만 읽는다.
 *
 * @param size
 *          읽어 올 크기
 * @return 읽어온 데이터
 */
-(NSData*)read:(NSUInteger)size;
/*!
 * 현재 파일포인터의 위치부터 데이터를 쓴다.
 *
 * @param data
 *          쓰고자 하는 데이터
 */
-(void)write:(NSData*)data;

@end
