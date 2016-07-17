//
//  GHControl.h
//  SVGgh
// The MIT License (MIT)

//  Copyright (c) 2011-2015 Glenn R. Howes

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


#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import UIKit;
#else
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#endif



#ifndef IBInspectable
#define IBInspectable
#endif

#ifndef IB_DESIGNABLE
#define IB_DESIGNABLE
#endif

#ifndef DEFINED_COLOR_SCHEME
#define DEFINED_COLOR_SCHEME

typedef NSUInteger ColorScheme;
#endif

NS_ASSUME_NONNULL_BEGIN

@interface GHControl : UIControl
@property(nonatomic, assign) ColorScheme         scheme;
/*! @property schemeNumber
 * @brief this is equivalent to the scheme property, just the one expected to be set via Storyboard or Nib
 */
@property(nonatomic, assign) IBInspectable NSInteger           schemeNumber;


/*! @property artInsetFraction
 * @brief this is a value to inset the artwork from the edges, as a fraction of the widget's height. So if a button is 50 points high and its artInsetFraction is .1 it will be inset 5 points. Should be much less than 0.5
 */
@property(nonatomic, assign) IBInspectable CGFloat              artInsetFraction;

// these are all related to how the button draws itself as part of a scheme
@property(nonatomic, assign) CGGradientRef   __nullable    faceGradient;
@property(nonatomic, assign) CGGradientRef  __nullable    faceGradientPressed;
@property(nonatomic, assign) CGGradientRef   __nullable    faceGradientSelected;
@property(nonatomic, strong) IBInspectable UIColor*          textColor;
@property(nonatomic, strong) IBInspectable UIColor*        textColorPressed;
@property(nonatomic, strong) IBInspectable UIColor*        textColorSelected;
@property(nonatomic, strong) IBInspectable UIColor*        textColorDisabled;
@property(nonatomic, assign) BOOL                   drawsChrome;
@property(nonatomic, assign) BOOL                   drawsBackground;
@property(nonatomic, strong) UIColor*       __nullable      ringColor;
@property(nonatomic, strong) UIColor*       __nullable       textShadowColor;
@property(nonatomic, assign) BOOL                useRadialGradient;
@property (nonatomic, assign) CGFloat             textFontSize;
@property(nonatomic, assign) BOOL                 useBoldText;
@property(nonatomic, assign) BOOL                 showShadow;



-(void) setupForScheme:(NSUInteger)aScheme;

@end

extern const CGFloat kRingThickness;
extern const CGFloat kRoundButtonRadius;
extern const CGFloat kShadowInset;

NS_ASSUME_NONNULL_END
