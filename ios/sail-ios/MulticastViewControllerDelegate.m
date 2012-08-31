//
//  MulticastViewControllerDelegate.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "MulticastViewControllerDelegate.h"

@implementation MulticastViewControllerDelegate

- (id)init {
    self = [super init];
    if(self) {
        _delegates = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc {
    [_delegates release];
    [super dealloc];
}

-(void)addViewControllerDelegate:(id)delegate {
    
    for (id delegateInDelegates in _delegates) {
        if ([delegateInDelegates isEqual:delegate]) {
            return;
        }
    }
    
    if([delegate conformsToProtocol:@protocol(AxViewControllerDelegate)]) {
        [_delegates addObject:delegate];
    }
}

-(void)removeViewControllerDelegate:(id)delegate {
    if([delegate conformsToProtocol:@protocol(AxViewControllerDelegate)]) {
        [_delegates removeObject:delegate];
    }
}

#pragma AxViewControllerDelegate

- (void)viewWillAppear:(BOOL)animated {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(viewWillAppear:)]) {
            [delegate viewWillAppear:animated];
        }
    }   
}

- (void)viewDidAppear:(BOOL)animated {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(viewDidAppear:)]) {
            [delegate viewDidAppear:animated];
        }
    }   
}

- (void)viewWillDisappear:(BOOL)animated {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(viewWillDisappear:)]) {
            [delegate viewWillDisappear:animated];
        }
    }   
}

- (void)viewDidDisappear:(BOOL)animated {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(viewDidDisappear:)]) {
            [delegate viewDidDisappear:animated];
        }
    }   
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    if ([_delegates count] > 0) {
        id delegate = [_delegates objectAtIndex:[_delegates count]-1];
        if([delegate respondsToSelector:@selector(shouldAutorotateToInterfaceOrientation:)]) {
            if([delegate shouldAutorotateToInterfaceOrientation:interfaceOrientation]) { return YES; }
        }
        return NO;
    }
    
    if (([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) && (interfaceOrientation == UIDeviceOrientationPortraitUpsideDown) ) {
        return NO;
    }
    return YES;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(willRotateToInterfaceOrientation:duration:)]) {
            [delegate willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
        }
    }   
}

- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation duration:(NSTimeInterval)duration {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(willAnimateRotationToInterfaceOrientation:duration:)]) {
            [delegate willAnimateRotationToInterfaceOrientation:interfaceOrientation duration:duration];
        }
    }   
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(didRotateFromInterfaceOrientation:)]) {
            [delegate didRotateFromInterfaceOrientation:fromInterfaceOrientation];
        }
    }   
}

- (void)willAnimateFirstHalfOfRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(willAnimateFirstHalfOfRotationToInterfaceOrientation:duration:)]) {
            [delegate willAnimateFirstHalfOfRotationToInterfaceOrientation:toInterfaceOrientation duration:duration];
        }
    }   
}

- (void)didAnimateFirstHalfOfRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(didAnimateFirstHalfOfRotationToInterfaceOrientation:)]) {
            [delegate didAnimateFirstHalfOfRotationToInterfaceOrientation:toInterfaceOrientation];
        }
    }   
}

- (void)willAnimateSecondHalfOfRotationFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation duration:(NSTimeInterval)duration {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(willAnimateSecondHalfOfRotationFromInterfaceOrientation:duration:)]) {
            [delegate willAnimateSecondHalfOfRotationFromInterfaceOrientation:fromInterfaceOrientation duration:duration];
        }
    }   
}

- (void)didReceiveMemoryWarning {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(didReceiveMemoryWarning)]) {
            [delegate didReceiveMemoryWarning];
        }
    }   
}

@end
