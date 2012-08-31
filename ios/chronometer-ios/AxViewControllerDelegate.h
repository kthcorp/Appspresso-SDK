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
 * 앱스프레소 앱의 화면을 관리하는 UIViewController로부터 위임받아서 처리.
 * 
 * 주의: willXXX류의 함수들은 super의 해당 메소드를 호출한 후에 호출되고, didXXX류의 함수들은 super의 해당 메소드를 호출하기 전에 호출됨.
 * 
 * @see //AppleDocument http://developer.apple.com/library/ios/#DOCUMENTATION/UIKit/Reference/UIViewController_Class/Reference/Reference.html
 */
@protocol AxViewControllerDelegate <NSObject>
@optional
/*!
 * Notifies the view controller that its view is about to be become visible.
 * 
 * @param animated 
 *           If YES, the view is being added to the window using an animation.
 * @since Available in iOS 2.0 and later.
 */
- (void)viewWillAppear:(BOOL)animated;
/*!
 * Notifies the view controller that its view was added to a window.
 * 
 * @param animated 
 *           If YES, the view was added to the window using an animation.
 * @since Available in iOS 2.0 and later.
 */
- (void)viewDidAppear:(BOOL)animated;
/*!
 * Notifies the view controller that its view is about to be dismissed, covered, or otherwise hidden from view.
 * 
 * @param animated 
 *           If YES, the disappearance of the view is being animated
 * @since Available in iOS 2.0 and later.
 */
- (void)viewWillDisappear:(BOOL)animated;
/*!
 * Notifies the view controller that its view was dismissed, covered, or otherwise hidden from view.
 * 
 * @param animated 
 *           If YES, the disappearance of the view was animated.
 * @since Available in iOS 2.0 and later.
 */
- (void)viewDidDisappear:(BOOL)animated;

/*!
 * Returns a Boolean value indicating whether the view controller supports the specified orientation.
 * 
 * @param interfaceOrientation
 *           The orientation of the application’s user interface after the rotation. The possible values are described in UIInterfaceOrientation.
 * @return YES if the view controller auto-rotates its view to the specified orientation; otherwise, NO .
 * @since Available in iOS 2.0 and later.
 */
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation;

/*!
 * Sent to the view controller just before the user interface begins rotating.
 * 
 * @param toInterfaceOrientation
 *           The new orientation for the user interface. The possible values are described in UIInterfaceOrientation.
 * @param duration
 *           The duration of the pending rotation, measured in seconds.
 * @since Available in iOS 2.0 and later.
 */
- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration;
/*!
 * Sent to the view controller before performing a one-step user interface rotation.
 * 
 * @param toInterfaceOrientation
 *           The new orientation for the user interface. The possible values are described in UIInterfaceOrientation.
 * @param duration
 *           The duration of the pending rotation, measured in seconds.
 * @since Available in iOS 3.0 and later.
 */
- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation duration:(NSTimeInterval)duration;
/*!
 * Sent to the view controller after the user interface rotates.
 * 
 * @param fromInterfaceOrientation
 *           The old orientation of the user interface. The possible values are described in UIInterfaceOrientation.
 * @since Available in iOS 2.0 and later.
 */
- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation;
/*!
 * Sent to the view controller before performing the first half of a user interface rotation.
 * 
 * @param toInterfaceOrientation
 *           The state of the application’s user interface orientation before the rotation. The possible values are described in UIInterfaceOrientation.
 * @param duration
 *           The duration of the first half of the pending rotation, measured in seconds.
 * @since Available in iOS 2.0 and later. 
 * @deprecated in iOS 5.0.
 */
- (void)willAnimateFirstHalfOfRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration;
/*!
 * Sent to the view controller after the completion of the first half of the user interface rotation.
 * 
 * @param toInterfaceOrientation
 *           The state of the application’s user interface orientation after the rotation. The possible values are described in UIInterfaceOrientation.
 * @since Available in iOS 2.1 and later. 
 * @deprecated iOS 5.0
 */
- (void)didAnimateFirstHalfOfRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation;
/*!
 * Sent to the view controller before the second half of the user interface rotates.
 * 
 * @param fromInterfaceOrientation
 *           The state of the application’s user interface orientation before the rotation. The possible values are described in UIInterfaceOrientation.
 * @param duration
 *           The duration of the second half of the pending rotation, measured in seconds.
 * @since Available in iOS 2.0 and later. Deprecated in iOS 5.0.
 */
- (void)willAnimateSecondHalfOfRotationFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation duration:(NSTimeInterval)duration;

/*!
 * Sent to the view controller when the application receives a memory warning.
 * 
 * @since Available in iOS 2.0 and later. 
 * @deprecated iOS 5.0
 */
- (void)didReceiveMemoryWarning;

@end
