//
//  GHSegmentedControl.h
//  SVGgh
// The MIT License (MIT)

//  Copyright (c) 2015 Glenn R. Howes

//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//
//  Created by Glenn Howes on 2015-03-26.
//  Copyright (c) 2015 Generally Helpful Software. All rights reserved.
//
// Interface deliberately similar to UISegmentedControl

#import "GHControl.h"

@class SVGRenderer;

NS_ASSUME_NONNULL_BEGIN

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 80000
IB_DESIGNABLE // Cocapods users should add the use_frameworks! directive if this causes an error
#endif
@interface GHSegmentedControl : GHControl

/*! @property momentary
 * @brief if set, then don't keep showing selected state after tracking ends. Default is NO
 */
@property(nonatomic,getter=isMomentary) BOOL momentary;

/*! @property numberOfSegments

 * @brief The number of tappable segments in this control
 */
@property(nonatomic,readonly) NSUInteger numberOfSegments;

/*! @property numberOfSegments
 
 * @brief Scale the segments that aren't explicitly set
 */
@property(nonatomic) BOOL apportionsSegmentWidthsByContent;

/*! @property selectedSegmentIndex
* @brief Ignored in momentary mode. returns last segment pressed. default is UISegmentedControlNoSegment until a segment 
 is pressed. The UIControlEventValueChanged action is invoked when the segment changes via a user event. set
  to UISegmentedControlNoSegment to turn off selection
 */
@property(nonatomic) NSInteger selectedSegmentIndex;

- (instancetype)initWithItems:(NSArray *)items;

- (void)insertSegmentWithTitle:(NSString *)title atIndex:(NSUInteger)segment animated:(BOOL)animated;
- (void)insertSegmentWithRenderer:(SVGRenderer *)renderer  atIndex:(NSUInteger)segment animated:(BOOL)animated;
- (void)insertSegmentWithRenderer:(SVGRenderer *)renderer accessibilityLabel:(nullable NSString*)accessibilityLabel  atIndex:(NSUInteger)segment animated:(BOOL)animated;
- (void)removeSegmentAtIndex:(NSUInteger)segment animated:(BOOL)animated;
- (void)removeAllSegments;

- (void)setTitle:(nullable NSString *)title forSegmentAtIndex:(NSUInteger)segment;
- (nullable NSString *)titleForSegmentAtIndex:(NSUInteger)segment;

-(void) setRenderer:(nullable SVGRenderer *)renderer forSegmentedIndex:(NSUInteger)segment;
-(nullable SVGRenderer*) rendererForSegmentedIndex:(NSUInteger)segment;

-(void) setAccessibilityLabel:(NSString *)accessibilityLabel forSegmentIndex:(NSUInteger)segment;
-(nullable NSString*)accessibilityLabelForSegmentedIndex:(NSUInteger)segment;

- (void)setWidth:(CGFloat)width forSegmentAtIndex:(NSUInteger)segment;
- (CGFloat)widthForSegmentAtIndex:(NSUInteger)segment;


- (void)setEnabled:(BOOL)enabled forSegmentAtIndex:(NSUInteger)segment;        // default is YES
- (BOOL)isEnabledForSegmentAtIndex:(NSUInteger)segment;



+(void)makeSureLoaded;
@end

NS_ASSUME_NONNULL_END
