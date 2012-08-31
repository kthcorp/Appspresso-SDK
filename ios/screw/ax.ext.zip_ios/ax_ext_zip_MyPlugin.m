//
//  ax_ext_zip_MyPlugin.m
//  ax.ext.zip
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxPluginContext.h"
#import "AxRuntimeContext.h"
#import "AxFileSystemManager.h"
#import "AxLog.h"

#import "unzip.h"

#import "ax_ext_zip_MyPlugin.h"

@implementation ax_ext_zip_MyPlugin

static unzFile _unzFile;

-(BOOL) UnzipOpenFile:(NSString*) zipFile
{
	_unzFile = unzOpen( (const char*)[zipFile UTF8String] );
	if( _unzFile )
	{
		unz_global_info  globalInfo = {0};
		if( unzGetGlobalInfo(_unzFile, &globalInfo )==UNZ_OK )
		{
			AX_LOG_TRACE(@"%d entries in the zip file",globalInfo.number_entry);
		}
	}
	return _unzFile!=NULL;
}

-(BOOL) UnzipFileTo:(NSString*) path overWrite:(BOOL) overwrite
{
	BOOL success = YES;
	int ret = unzGoToFirstFile( _unzFile );
	unsigned char		buffer[4096] = {0};
	NSFileManager* fman = [NSFileManager defaultManager];
	if( ret!=UNZ_OK )
	{
		AX_LOG_TRACE(@"Failed");
	}
	
	do{
		ret = unzOpenCurrentFile( _unzFile );
		if( ret!=UNZ_OK )
		{
			AX_LOG_TRACE(@"Error occurs");
			success = NO;
			break;
		}
		// reading data and write to file
		int read ;
		unz_file_info	fileInfo ={0};
		ret = unzGetCurrentFileInfo(_unzFile, &fileInfo, NULL, 0, NULL, 0, NULL, 0);
		if( ret!=UNZ_OK )
		{
			AX_LOG_TRACE(@"Error occurs while getting file info");
			success = NO;
			unzCloseCurrentFile( _unzFile );
			break;
		}
		char* filename = (char*) malloc( fileInfo.size_filename +1 );
		unzGetCurrentFileInfo(_unzFile, &fileInfo, filename, fileInfo.size_filename + 1, NULL, 0, NULL, 0);
		filename[fileInfo.size_filename] = '\0';
		
		// check if it contains directory
		NSString * strPath = [NSString stringWithCString:filename encoding:NSUTF8StringEncoding];
		//NSString * strPath = [NSString  stringWithCString:filename];
		BOOL isDirectory = NO;
		if( filename[fileInfo.size_filename-1]=='/' || filename[fileInfo.size_filename-1]=='\\')
			isDirectory = YES;
		free( filename );
		if( [strPath rangeOfCharacterFromSet:[NSCharacterSet characterSetWithCharactersInString:@"/\\"]].location!=NSNotFound )
		{// contains a path
			strPath = [strPath stringByReplacingOccurrencesOfString:@"\\" withString:@"/"];
		}
		NSString* fullPath = [path stringByAppendingPathComponent:strPath];
		
		if( isDirectory )
			[fman createDirectoryAtPath:fullPath withIntermediateDirectories:YES attributes:nil error:nil];
		else
			[fman createDirectoryAtPath:[fullPath stringByDeletingLastPathComponent] withIntermediateDirectories:YES attributes:nil error:nil];
		FILE* fp = fopen( (const char*)[fullPath UTF8String], "wb");
		while( fp )
		{
			read=unzReadCurrentFile(_unzFile, buffer, 4096);
			if( read > 0 )
			{
				fwrite(buffer, read, 1, fp );
			}
			else if( read<0 )
			{
				AX_LOG_TRACE(@"Failed to reading zip file");
				break;
			}
			else 
				break;				
		}
		if( fp )
		{
			fclose( fp );
			// set the orignal datetime property
			NSDate* orgDate = nil;
			
			//{{ thanks to brad.eaton for the solution
			NSDateComponents *dc = [[NSDateComponents alloc] init];
			
			dc.second = fileInfo.tmu_date.tm_sec;
			dc.minute = fileInfo.tmu_date.tm_min;
			dc.hour = fileInfo.tmu_date.tm_hour;
			dc.day = fileInfo.tmu_date.tm_mday;
			dc.month = fileInfo.tmu_date.tm_mon+1;
			dc.year = fileInfo.tmu_date.tm_year;
			
			NSCalendar *gregorian = [[NSCalendar alloc] 
									 initWithCalendarIdentifier:NSGregorianCalendar];
			
			orgDate = [gregorian dateFromComponents:dc] ;
			[dc release];
			[gregorian release];
			//}}
			
			
			NSDictionary* attr = [NSDictionary dictionaryWithObject:orgDate forKey:NSFileModificationDate]; //[[NSFileManager defaultManager] fileAttributesAtPath:fullPath traverseLink:YES];
			if( attr )
			{
				//		[attr  setValue:orgDate forKey:NSFileCreationDate];
				if( ![[NSFileManager defaultManager] setAttributes:attr ofItemAtPath:fullPath error:nil] )
				{
					// cann't set attributes 
					AX_LOG_TRACE(@"Failed to set attributes");
				}
				
			}
		
			
			
		}
		unzCloseCurrentFile( _unzFile );
		ret = unzGoToNextFile( _unzFile );
	}while( ret==UNZ_OK && UNZ_OK!=UNZ_END_OF_LIST_OF_FILE );
	return success;
}

-(BOOL) UnzipCloseFile
{
	if( _unzFile )
		return unzClose( _unzFile )==UNZ_OK;
	return YES;
}

//
//
//

-(void)unzip:(NSObject<AxPluginContext>*)context {
	NSString *path_ = [context getParamAsString:0];
	NSString *targetDir_ = [context getParamAsString:1];

	NSString *path = [[self.runtimeContext getFileSystemManager] toNativePath:path_];
	NSString *targetDir = [[self.runtimeContext getFileSystemManager] toNativePath:targetDir_];
	AX_LOG_TRACE(@"%s: path=%@,targetDir=%@", __PRETTY_FUNCTION__, path, targetDir);
	
	NSError *error = nil;
	if(![[NSFileManager defaultManager] createDirectoryAtPath:targetDir withIntermediateDirectories:YES attributes:nil error:&error]) {
		[context sendError:-1 message:[error description]];
		return;
	}
	
	if(![self UnzipOpenFile:path]) {
		[context sendError:-1 message:@"failed to open zip file"];
		return;
	}
	if(![self UnzipFileTo:targetDir overWrite:YES]) {
		[context sendError:-1 message:@"failed to unzip file"];
		return;
	}
	[self UnzipCloseFile];
	
	[context sendResult];
}

@end
