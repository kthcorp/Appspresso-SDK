//
//  ContactPhotoFile.m
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "ContactPhotoFile.h"
#import "ContactPhotoFileSystem.h"

@implementation ContactPhotoFile


- (id)initRootWithFileSystem:(ContactPhotoFileSystem*)fileSystem
{
    if ((self = [super init])) {
        _fileSystem = [fileSystem retain];
        _personId = 0;
        _isRoot = YES;
    }
    return self;
}    

- (id)initWithPersonId:(ABRecordID)personId fileSystem:(ContactPhotoFileSystem*)fileSystem
{
    if ((self = [super init])) {
        _personId = personId;
        _fileSystem = [fileSystem retain];
        _record = ABAddressBookGetPersonWithRecordID(ABAddressBookCreate(), _personId);
        _isRoot = NO;
    }
    return self;
}

-(void)dealloc {
    [_fileSystem release];
    CFRelease(_record);
    [super dealloc];
}


-(NSString*)getName {
    // abrecord person id 를 이름으로 사용
    return (_isRoot) ? @"/" : [NSString stringWithFormat:@"%d", _personId];
}

-(NSString*)getPath {
    // 루트 외에는 디렉토리가 없음 --> 이름이 곧 경로
    return [self getName];
}

-(id<AxFile>)getParent {
    // 루트 외에는 디렉토리가 없음 --> 루트가 아닌 녀석의 부모는 항상 root
    // XXX: 루트의 부모는 nil??(스펙 확인 요망)
    return (_isRoot) ? nil : [_fileSystem getRoot];
}

-(BOOL)isFile {
    // 루트 외에는 디렉토리가 없음 --> 루트가 아니면 항상 파일
    return !_isRoot;
}

-(BOOL)isDirectory {
    // 루트 외에는 디렉토리가 없음 --> 루트만 디렉토리
    return _isRoot;
}

-(NSUInteger)getLength {
    // XXX: 디렉토리의 length는 뭐지?
    if (_isRoot) {
        return 0;
    }
    if(_data == nil) {
        [self open:AxFileOpenForReadOnly];
    }
    return [_data length];
}

-(NSDate*)getCreated {
    // XXX: 날짜 지원 안함
    return [NSDate date];
}

-(NSDate*)getModified {
    // XXX: 날짜 지원 안함
    return [NSDate date];
}

-(BOOL)exists {
    // 루트 디렉토리거나 이미지 데이터가 있으면 존재하는 것으로 간주
    return (_isRoot) || ABPersonHasImageData(_record);
}

-(BOOL)canRead {
    // 다 읽을 수 있음
    return YES;
}

-(BOOL)canWrite {
    // 루트 외에는 다 쓸 수 있음
    return !_isRoot;
}

-(NSData*)getContentsAsData {
    return (NSData*)ABPersonCopyImageData(_record);
}

-(void)setContentsWithData:(NSData *)data {
    ABPersonSetImageData(_record, (CFDataRef)data, nil);
}

-(NSString*)getContentsAsString {
    // not supported!
    return nil;
}

-(void)setContentsWithString:(NSString *)str {
    // not supported!
}

-(NSString*)getContentsAsString:(NSString*)encoding {
    // not supported!
    return nil;
}

-(void)setContentsWithString:(NSString *)str encoding:(NSString *)encoding {
    // not supported!
}

-(NSArray*)listFiles : (NSObject<AxFileFilter>*) filter {
    // 루트 외에는 폴더가 없음.
    // TODO: 루트일 경우에는 가용한 주소록사진 목록을 만들어서 리턴??
    return [NSArray array];
}

-(id)getPeer {
    return (id)_personId;
}

-(BOOL)open:(int)mode {
    if(_isRoot) {
        // can't open directory
        return NO;
    }
    if(_data) {
        // already opened!
        return NO;
    }
    _data = (NSData*)ABPersonCopyImageData(_record);
    _position = 0;
    return YES;
}

-(void)close {
    if(_data != nil) {
        CFRelease(_data);
        _data = nil;
    }
}

-(BOOL)isEof {
    if(_data != nil) {
        // not yet open!
    }
    return _position > [_data length];
}

-(void)seek:(NSUInteger)position {
    if(_data != nil) {
        // not yet open!
    }
    _position = position;
}

-(NSUInteger)getPosition {
    if(_data != nil) {
        // not yet open!
    }
    return _position;
}

-(NSData*)read:(NSUInteger)size {
    if(_data != nil) {
        // not yet open!
    }
    NSRange range;
    range.location = _position;
    range.length = size;
    return [_data subdataWithRange:range];
}

-(void)write:(NSData*)data {
    if(_data != nil) {
        // not yet open!
    }
    // TODO: ...
    // _data = ...
    // ABPersonSetImageData(_abrecord, _data, ...
}

@end
