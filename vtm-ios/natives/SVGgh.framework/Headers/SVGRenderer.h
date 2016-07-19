//
//  SVGRenderer.h
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
//  Created by Glenn Howes on 1/12/11.

#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
#else
#import <Foundation/Foundation.h>
#endif

#import "SVGParser.h"
#import "GHRenderable.h"
#import "SVGContext.h"
#import "GHCSSStyle.h"

NS_ASSUME_NONNULL_BEGIN

/*! @brief a class capable of rendering itself into a core graphics context
*/
@interface SVGRenderer : SVGParser<SVGContext, GHRenderable>

/*! @property viewRect
 * @brief a set of flags that allow the dynamic manipulation of rendering styles (for instance, a focused tvOS image could use kPseudoClassFocused)
 */
@property (assign, nonatomic)   CSSPseudoClassFlags cssPseudoClass;

/*! @property viewRect
* @brief the intrinsic rect declared in the SVG document being rendered
*/
@property (nonatomic, readonly)         CGRect	viewRect;

/*! @brief a queue where it is convenient to renders when the main queue is not necessary
* @return a shared operation queue
*/
+(NSOperationQueue*) rendererQueue;

/*! @brief draw the SVG
* @param quartzContext context into which to draw, could be a CALayer, a PDF, an offscreen bitmap, whatever
*/
-(void)renderIntoContext:(CGContextRef)quartzContext;

/*! @brief try to locate an object that's been tapped
* @param testPoint a point in the coordinate system of this renderer
* @return an object which implements the GHRenderable protocol
*/
-(nullable id<GHRenderable>) findRenderableObject:(CGPoint)testPoint;

/*! @brief make a scaled image from the renderer
 * @param maximumSize the maximum dimension in points to render into.
 * @param scale same as a UIWindow's scale
 * @return a UIImage
 */
-(UIImage*)asImageWithSize:(CGSize)maximumSize andScale:(CGFloat)scale;

@end

NS_ASSUME_NONNULL_END


