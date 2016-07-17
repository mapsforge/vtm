//
//  SVGDocumentView.h
//  SVGgh
// The MIT License (MIT)

//  Copyright (c) 2011-2014 Glenn R. Howes

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
//  Created by Glenn Howes on 1/15/11.


#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import UIKit;
#else
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#endif

#import "GHRenderable.h"

#ifndef IBInspectable
#define IBInspectable
#endif

#ifndef IB_DESIGNABLE
#define IB_DESIGNABLE
#endif

NS_ASSUME_NONNULL_BEGIN

/*
 * Note you can instantiate a SVGDocumentView in a storyboard or a XIB by dragging a UIView into your view and giving it a class of SVGDocumentView.
 * You can configure the contents of the view by adding "User Defined Runtime Attributes" in XCode's "Identity Inspector"
 ***********************************
 ** Key Path      ***         Type        ***    Value    **
 ----------------------------------------------------------------
 schemeNumber             Number                 3
 artworkPath              String             Artwork/Background
 -----------------------------------------------------------------
 Will draw the contents of a file in your app's bundle with the path /Artwork/Background.svg
 You may also want to set the "Mode" in the "Attributes Inspector" panel to "Scale to Fill"
 */

@class SVGRenderer;
/*! @brief a view capable of hosting an SVGRenderer or rendering a chunk of SVG
*/
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 80000
IB_DESIGNABLE // Cocapods users should add the use_frameworks! directive if this causes an error
#endif
@interface SVGDocumentView : UIView

/*! @property artworkPath
* @brief the text contents of an SVG document can be accessed via 'User Defined RuntimeAttributes'
*/
@property(nonatomic, strong) IBInspectable NSString* __nullable        artworkPath;

/*! @property defaultColor
 * @brief the color that 'currentColor' in SVG documents will be set to 
*/
@property(nonatomic, strong) IBInspectable UIColor* __nullable  defaultColor;

/*! @property renderer
* @brief a pre-configured SVGRenderer object which will be called to draw the content
*/
@property(nonatomic, strong)	SVGRenderer* __nullable 	renderer;

/*! @property beTransparent
 * @brief ignore the document's 'viewport-fill' property
 */
@property(nonatomic, assign) IBInspectable   BOOL beTransparent;

/*! @brief method that tries to locate an object located at the given point inside the coordinate system of the view
* @param testPoint a point in the coordinate system of the view
* @return an object hit by the point
*/
-(nullable id<GHRenderable>) findRenderableObject:(CGPoint)testPoint;
+(void)makeSureLoaded;
@end

NS_ASSUME_NONNULL_END
