/*
 * Copyright (c) 2010 Dave Dribin
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

#import "A440AudioQueue.h"

#import "AxLog.h"


#define kNumberBuffers (sizeof(_buffers)/sizeof(*_buffers))

#define FAIL_ON_ERR(_X_) if ((status = (_X_)) != noErr) { goto failed; }

@interface A440AudioQueue ()
- (void)setupDataFormat;
- (OSStatus)allocateBuffers;
- (UInt32)calculateBufferSizeForSeconds:(Float64)seconds;
- (void)primeBuffers;
@end

static void HandleOutputBuffer(void * inUserData,
                               AudioQueueRef inAQ,
                               AudioQueueBufferRef inBuffer);
static void FillFrame(A440AudioQueue * self, int16_t * sample);

@implementation A440AudioQueue

- (void)dealloc
{
    if (_queue != NULL) {
        [self stop:NULL];
    }
    [super dealloc];
}

- (BOOL)play:(NSError **)error;
{
    NSAssert(_queue == NULL, @"Queue is already setup");

    OSStatus status;
    
    [self setupDataFormat];
    FAIL_ON_ERR(AudioQueueNewOutput(&_dataFormat, HandleOutputBuffer,
                                    self, CFRunLoopGetCurrent(),
                                    kCFRunLoopCommonModes, 0, &_queue));
    FAIL_ON_ERR([self allocateBuffers]);
    A440SineWaveGeneratorInitWithFrequency(&_sineWaveGenerator, 440.0);
    [self primeBuffers];
    FAIL_ON_ERR(AudioQueueStart(_queue, NULL));
    return YES;
    
failed:
    // Error handling...
    if (_queue != NULL) {
        AudioQueueDispose(_queue, YES);
        _queue = NULL;
    }
    
    if (error != NULL) {
        *error = [NSError errorWithDomain:NSOSStatusErrorDomain
                                     code:status userInfo:nil];
    }
    return NO;
}

- (void)setupDataFormat;
{
    // 16-bit native endian signed integer, stereo LPCM
    UInt32 formatFlags = (0
                          | kAudioFormatFlagIsPacked 
                          | kAudioFormatFlagIsSignedInteger 
                          | kAudioFormatFlagsNativeEndian
                          );
    
    _dataFormat = (AudioStreamBasicDescription){
        .mFormatID = kAudioFormatLinearPCM,
        .mFormatFlags = formatFlags,
        .mSampleRate = SAMPLE_RATE,
        .mBitsPerChannel = 16,
        .mChannelsPerFrame = 2,
        .mBytesPerFrame = 4,
        .mFramesPerPacket = 1,
        .mBytesPerPacket = 4,
    };
}

- (OSStatus)allocateBuffers;
{
    UInt32 bufferSize = [self calculateBufferSizeForSeconds:0.5];
    
    OSStatus status;
    for (int i = 0; i < kNumberBuffers; ++i) {
        status = AudioQueueAllocateBuffer(_queue, bufferSize,
                                          &_buffers[i]);
        if (status != noErr) {
            return status;
        }
    }
    return noErr;
}

- (UInt32)calculateBufferSizeForSeconds:(Float64)seconds;
{
    UInt32 bufferSize = (_dataFormat.mSampleRate *
                         _dataFormat.mBytesPerPacket *
                         seconds);
    return bufferSize;
}

- (void)primeBuffers;
{
    _shouldBufferDataInCallback = YES;
    for (int i = 0; i < kNumberBuffers; ++i) {
        HandleOutputBuffer(self, _queue, _buffers[i]);
    }
}

#pragma mark -

static void HandleOutputBuffer(void * inUserData,
                               AudioQueueRef inAQ,
                               AudioQueueBufferRef inBuffer)
{
    A440AudioQueue * self = inUserData;
    
    if (!self->_shouldBufferDataInCallback) {
        return;
    }
    
    int16_t * sample = inBuffer->mAudioData;
    UInt32 numberOfFrames = (inBuffer->mAudioDataBytesCapacity /
                             self->_dataFormat.mBytesPerFrame);
    
    for (UInt32 i = 0; i < numberOfFrames; i++) {
        FillFrame(self, sample);
        sample += self->_dataFormat.mChannelsPerFrame;
    }
    
    inBuffer->mAudioDataByteSize = (numberOfFrames *
                                    self->_dataFormat.mBytesPerFrame);
    
    OSStatus result;
    result = AudioQueueEnqueueBuffer(self->_queue, inBuffer, 0, NULL);
    if (result != noErr) {
        AX_LOG_TRACE(@"AudioQueueEnqueueBuffer error: %d", result);
    }
}

static void FillFrame(A440AudioQueue * self, int16_t * sample)
{
    A440SineWaveGenerator * generator = &self->_sineWaveGenerator;
    int16_t sampleValue =
        A440SineWaveGeneratorNextSample(generator);
    // Divide by four to keep the volume away from the max
    sampleValue /= 4;
    
    // Fill two channels
    sample[0] = sampleValue;
    sample[1] = sampleValue;
}

#pragma mark -

- (BOOL)stop:(NSError **)error;
{
    NSAssert(_queue != NULL, @"Queue is not setup");
    
    OSStatus status;
    _shouldBufferDataInCallback = NO;
    FAIL_ON_ERR(AudioQueueStop(_queue, YES));
    FAIL_ON_ERR(AudioQueueDispose(_queue, YES));
    _queue = NULL;
    return YES;
    
failed:
    // Error handling...
    if (error != NULL) {
        *error = [NSError errorWithDomain:NSOSStatusErrorDomain
                                     code:status userInfo:nil];
    }
    return NO;
}

@end
