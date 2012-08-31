//
//  ax_ext_media_MyPlugin.m
//  ax.ext.media
//
//  Copyright (c) 2012 KTH Corp.
//

#import <AudioToolbox/AudioToolbox.h>
#import <QuartzCore/CALayer.h>

#import "AxPluginContext.h"
#import "AxRuntimeContext.h"
#import "AxFileSystemManager.h"
#import "AxLog.h"
#import "AxError.h"

#import "ax_ext_media_MyPlugin.h"

@implementation ax_ext_media_MyPlugin

//
//
//

- (void)activate:(NSObject<AxRuntimeContext>*)context {
    [super activate:context];
	_soundIds = [[NSMutableDictionary alloc] init];
    _soundPlayerArray = [[NSMutableArray alloc]init];
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)context {
    [_soundIds release];
    [_soundPlayerArray release];
    [super deactivate:context];
}

//
//
//

- (void)pickImage:(NSObject<AxPluginContext>*)context {
    if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
		[context sendError:-1 message:@"pickImage error!"];
		return;
	}

    if (_runningContext != nil) {
        [context sendError:AX_INVALID_ACCESS_ERR message:@"pick/capture image is already running"];
        return;
    }

    _runningContext = [context retain];

	dispatch_async(dispatch_get_main_queue(), ^{
		NSString *out = [context getParamAsString:0 name:@"out"];
		BOOL crop = [context getParamAsBoolean:0 name:@"crop" defaultValue:NO];
        int origin_x = [context getParamAsInteger:0 name:@"x"];
        int origin_y = [context getParamAsInteger:0 name:@"y"];
		if(!out) {
			out = [NSString stringWithFormat:@"wgt-private-tmp/PICK-%x.jpg", [NSDate timeIntervalSinceReferenceDate]];
		}
		_outPath = [[self.runtimeContext getFileSystemManager] toNativePath:out];
        [_outPath retain];
        AX_LOG_TRACE(@"%s: outPath=%@,crop=%d", __PRETTY_FUNCTION__, _outPath, crop);

        //
		UIImagePickerController *picker = [[[UIImagePickerController alloc] init] autorelease];
        picker.sourceType =  UIImagePickerControllerSourceTypePhotoLibrary;
		picker.navigationBarHidden = YES;
		picker.delegate = self;
		picker.allowsEditing = crop;
        _popover = nil;
        if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
            _popover = [[UIPopoverController alloc] initWithContentViewController:picker];
            [_popover presentPopoverFromRect:CGRectMake(origin_x, origin_y, 0, 0)
                                    inView:[self.runtimeContext getWebView]
                   permittedArrowDirections:UIPopoverArrowDirectionAny
                                   animated:YES];
        }
        else {
            [[self.runtimeContext getViewController] presentModalViewController:picker animated:YES];
        }
	});
}

- (void)captureImage:(NSObject<AxPluginContext>*)context {
    if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
		[context sendError:-1 message:@"captureImage error!"];
		return;
	}

    if (_runningContext != nil) {
        [context sendError:AX_INVALID_ACCESS_ERR message:@"pick/capture image is already running"];
        return;
    }

    _runningContext = [context retain];

	dispatch_async(dispatch_get_main_queue(), ^{
		NSString *out = [context getParamAsString:0 name:@"out"];
		BOOL crop = [context getParamAsBoolean:0 name:@"crop" defaultValue:NO];
		if(!out) {
			out = [NSString stringWithFormat:@"wgt-private-tmp/CAPTURE-%x.jpg", [NSDate timeIntervalSinceReferenceDate]];
		}
		_outPath = [[self.runtimeContext getFileSystemManager] toNativePath:out];
        [_outPath retain];
        AX_LOG_TRACE(@"%s: outPath=%@,crop=%d", __PRETTY_FUNCTION__, _outPath, crop);

		UIImagePickerController *picker = [[[UIImagePickerController alloc] init] autorelease];
		picker.sourceType =  UIImagePickerControllerSourceTypeCamera;
		picker.navigationBarHidden = YES;
		picker.delegate = self;
		picker.allowsEditing = crop;
		[[self.runtimeContext getViewController] presentModalViewController:picker animated:YES];
	});
}

//
//
//

- (void)transformImage:(NSObject<AxPluginContext>*)context
{
    NSString *src = [context getParamAsString:0 name:@"src"];
    NSString *out = [context getParamAsString:0 name:@"out"];
	int newSize = [context getParamAsInteger:0 name:@"newSize"];

	NSString *srcPath = [[self.runtimeContext getFileSystemManager] toNativePath:src];
	NSString *outPath = [[self.runtimeContext getFileSystemManager] toNativePath:out];
	AX_LOG_TRACE(@"%s: srcPath=%@,outPath=%@,newSize=%d", __PRETTY_FUNCTION__, srcPath, outPath, newSize);

	UIImage *image = [UIImage imageWithContentsOfFile:srcPath];
    if (image.size.width > newSize || image.size.height > newSize) {
		int height, width;
        if (image.size.width > image.size.height) {
            height = image.size.height * newSize / image.size.width;
            width = newSize;
        } else {
            height = newSize;
            width = image.size.width * newSize / image.size.height;
        }
		CGImageRef imageRef = image.CGImage;
		CGContextRef bitmap = CGBitmapContextCreate(NULL, width, height,
													CGImageGetBitsPerComponent(imageRef),
													CGImageGetBytesPerRow(imageRef),
													CGImageGetColorSpace(imageRef),
													kCGImageAlphaNoneSkipLast);//CGImageGetBitmapInfo(imageRef));

		CGContextDrawImage(bitmap, CGRectMake(0, 0, width, height), imageRef);

		CGImageRef resizeImageRef = CGBitmapContextCreateImage(bitmap);
		UIImage* resizeImage = [UIImage imageWithCGImage:resizeImageRef];
		CGContextRelease(bitmap);
		CGImageRelease(resizeImageRef);

		[UIImageJPEGRepresentation(resizeImage, 0.9f) writeToFile:outPath atomically:YES];
    } else {
		[UIImageJPEGRepresentation(image, 0.9f) writeToFile:outPath atomically:YES];
	}

	[context sendResult:[[self.runtimeContext getFileSystemManager] toVirtualPath:outPath]];
}

//
//
//

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo
{
	AX_LOG_TRACE(@"%s: error=%@", __PRETTY_FUNCTION__, error);
}

-(void)captureScreen:(NSObject<AxPluginContext>*)context
{
	dispatch_async(dispatch_get_main_queue(), ^{
        NSString *out = [context getParamAsString:0 name:@"out"];
		if(!out) {
			out = [NSString stringWithFormat:@"wgt-private-tmp/SSHOT-%x.jpg", [NSDate timeIntervalSinceReferenceDate]];
		}
        NSString *outPath = [[self.runtimeContext getFileSystemManager] toNativePath:out];
		AX_LOG_TRACE(@"%s: outPath=%@", __PRETTY_FUNCTION__, outPath);

		UIGraphicsBeginImageContextWithOptions([self.runtimeContext getWebView].bounds.size, NO, 0);
		[[self.runtimeContext getWebView].layer renderInContext:UIGraphicsGetCurrentContext()];
		UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
		UIGraphicsEndImageContext();

		[UIImageJPEGRepresentation(image, 1.0f) writeToFile:outPath atomically:YES];

        [context sendResult:[[self.runtimeContext getFileSystemManager] toVirtualPath:outPath]];
	});
}

-(void)playAudio:(NSObject<AxPluginContext>*)context {
    NSString *path = [context getParamAsString:0];
	AX_LOG_TRACE(@"%s: path=%@", __PRETTY_FUNCTION__, path);
    if ([[self.runtimeContext getFileSystemManager] toNativePath:path]==nil) {
        [context sendError:-1 message:@"invalid file path"];
        return;
    }

	dispatch_async(dispatch_get_main_queue(), ^{
        NSNumber *soundId_ = [_soundIds valueForKey:path];
        if(!soundId_) {
            SystemSoundID soundId;
            NSURL *url = [NSURL fileURLWithPath:[[self.runtimeContext getFileSystemManager] toNativePath:path]];
            AudioServicesCreateSystemSoundID((CFURLRef)url, &soundId);
            soundId_ = [NSNumber numberWithUnsignedInt:soundId];
            [_soundIds setValue:soundId_ forKey:path];
        }
        AudioServicesPlaySystemSound([soundId_ unsignedIntValue]);
    });

	[context sendResult];
}

-(void)playSound:(NSObject<AxPluginContext>*)context {
    NSString *path = [context getParamAsString:0];
	AX_LOG_TRACE(@"%s: path=%@", __PRETTY_FUNCTION__, path);
    if ([[self.runtimeContext getFileSystemManager] toNativePath:path]==nil) {
        [context sendError:-1 message:@"invalid file path"];
        return;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        NSURL *url = [NSURL fileURLWithPath:[[self.runtimeContext getFileSystemManager] toNativePath:path]];
        AVAudioPlayer *newPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
        [newPlayer play];
        [_soundPlayerArray addObject:newPlayer];
        [newPlayer release];
    });
	[context sendResult:[NSNumber numberWithInt:[_soundPlayerArray count]]];
}

- (void)stopSound:(NSObject<AxPluginContext>*)context {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([_soundPlayerArray count] > 0) {
            AVAudioPlayer *newPlayer = [_soundPlayerArray objectAtIndex:0];
            [newPlayer stop];
            [_soundPlayerArray removeObjectAtIndex:0];
        }
    });
    [context sendResult];
}

//
//
//

- (void)addToGallery:(NSObject<AxPluginContext>*)context
{
	NSString *path_ = [context getParamAsString:0];
	NSString *path = [[self.runtimeContext getFileSystemManager] toNativePath:path_];
	AX_LOG_TRACE(@"%s: path=%@", __PRETTY_FUNCTION__, path);
    if ([path isEqual:nil]) {
        [context sendError:-1 message:@"invalid file path"];
        return;
    }

	UIImage *image = [UIImage imageWithContentsOfFile:path];
    if ([image isEqual:nil]) {
        [context sendError:-1 message:@"no such file"];
        return;
    }
	UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), self);

	[context sendResult];
}

//
//
//

- (void)clearSpentContext
{
    [_runningContext release];
    _runningContext = nil;
}

#pragma mark UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
	AX_LOG_TRACE(@"%s: info=%@", __PRETTY_FUNCTION__, info);
	//NSString *const UIImagePickerControllerMediaType;
	//NSString *const UIImagePickerControllerOriginalImage;
	//NSString *const UIImagePickerControllerEditedImage;
	//NSString *const UIImagePickerControllerCropRect;
	//NSString *const UIImagePickerControllerMediaURL;
	//NSString *const UIImagePickerControllerReferenceURL;
	//NSString *const UIImagePickerControllerMediaMetadata;

    BOOL capturing = picker.sourceType == UIImagePickerControllerSourceTypeCamera;
    if (capturing) {
		// save original image to photo album
		UIImageWriteToSavedPhotosAlbum([info objectForKey:UIImagePickerControllerOriginalImage], self, nil, nil);
    }

	if(picker.allowsEditing) {
		// save cropped image to out
		[UIImageJPEGRepresentation([info objectForKey:UIImagePickerControllerEditedImage], 1.0f) writeToFile:_outPath atomically:YES];
	} else {
		// save original image to out
		[UIImageJPEGRepresentation([info objectForKey:UIImagePickerControllerOriginalImage], 1.0f) writeToFile:_outPath atomically:YES];
	}

    NSString *voutPath = [[self.runtimeContext getFileSystemManager] toVirtualPath:_outPath];

    if (_runningContext == nil) {
        AX_LOG_WARN(@"%@ missing context", __PRETTY_FUNCTION__);
    } else {
        [_runningContext sendResult:voutPath];
        [self clearSpentContext];
    }

    if (_popover != nil) {
        [_popover dismissPopoverAnimated:YES];
    }
    else {
        [picker dismissModalViewControllerAnimated:YES];
    }
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
	AX_LOG_TRACE(@"%s:", __PRETTY_FUNCTION__);

    if (_runningContext == nil) {
        AX_LOG_WARN(@"%@ missing context", __PRETTY_FUNCTION__);
    } else {
        [_runningContext sendError:AX_ABORT_ERR message:@"user canceled image capture/pick"];
        [self clearSpentContext];
    }

    [picker dismissModalViewControllerAnimated:YES];
}

@end
