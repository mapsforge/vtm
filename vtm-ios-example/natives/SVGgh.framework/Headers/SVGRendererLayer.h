//
//  SVGRendererLayer.h
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
@import QuartzCore;
#else
#import <Foundation/Foundation.h>
#import <QuartzCore/QuartzCore.h>
#endif


#import "SVGRenderer.h"

NS_ASSUME_NONNULL_BEGIN

@protocol FillColorProtocol <NSObject>
-(nullable UIColor*) copyFillColor;
@end

/*! @brief a layer which takes an SVGRenderer and uses it to draw itself
* @see SVGDocumentView
* @see SVGRenderer
*/
@interface SVGRendererLayer : CALayer
/*! @property renderer
* @brief the object that does the actual drawing.
*/
@property(nonatomic, strong) SVGRenderer* __nullable 	renderer;

/*! @property defaultColor
 * @brief the value for 'currentColor' when the SVG is rendered from the root element
 */
@property(nonatomic, strong) UIColor* __nullable  defaultColor;

/*! @property beTransparent
 * @brief ignore the document's 'viewport-fill' property
 */
@property(nonatomic, assign) BOOL   beTransparent;

/*! @brief method that tries to locate an object located at the given point inside the coordinate system of the layer
 * @param testPoint point in the coordinate system of the layer
 * @return an object hit by the point
 */
-(nullable id<GHRenderable>) findRenderableObject:(CGPoint)testPoint;
@end

NS_ASSUME_NONNULL_END
