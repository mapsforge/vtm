//
//  GHRenderable.h
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
@import CoreGraphics;
#else
#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#endif

@protocol SVGContext;


/*
 ClippingType
 When at all possible, try to use a vectored based mechanism for clipping. It seems so much cleaner and
 less memory intensive than having to mask to an image.
 */
enum
{
    kNoClippingType = 0,
    kPathClippingType,
    kEvenOddPathClippingType,
    kFontGlyphClippingType,
    kImageClipplingType,
    kMixedClippingType
};

typedef uint32_t ClippingType;

NS_ASSUME_NONNULL_BEGIN

/*! @brief a protocol adoptable by an object design to be rendered to the screen or to add to the clipping
* @see GHRenderableObject
*/
@protocol GHRenderable<NSObject>
/*! @property transform
* @brief How this object is located in its parent's drawing context
*/
@property (nonatomic, readonly)         CGAffineTransform	transform;
/*! @property hidden
* @brief if YES, the object will not be rendered
*/
@property (nonatomic, readonly)         BOOL                hidden;

/*! @property attributes
* @brief presumably this object will be an GHAttributedObject
*/
@property(nonatomic, readonly)         NSDictionary* __nullable        attributes;

/*! @brief draw the object into a Core Graphics context
* @param quartzContext the Core Graphics context into which to render
* @param svgContext state information about the document environment in which this object is being rendered (currentColor, etc.)
*/
-(void) renderIntoContext:(CGContextRef)quartzContext withSVGContext:(id<SVGContext>)svgContext;

/*! @brief routine to retrieve this object or a sub-object during hit testing
* @param testPoint a Core Graphic point in the coordinate system of this object's parent
* @param svgContext state information about the document environment in which this object is being visited
*/
-(nullable id<GHRenderable>) findRenderableObject:(CGPoint)testPoint withSVGContext:(id<SVGContext>)svgContext;

/*! @brief clip the appropriate region for this region
* @param quartzContext the Core Graphics context needing clipping
* @param svgContext state information about the document environment (parent attributes, etc.) 
* @param objectBox rectangle of the object being clipped in the coordinate system of this object
*/
-(void) addToClipForContext:(CGContextRef)quartzContext  withSVGContext:(id<SVGContext>)svgContext objectBoundingBox:(CGRect) objectBox;

/*! @brief if possible add to the clipping path (as opposed to using a bitmap mask for clipping)
* @param quartzContext the Core Graphics context needing clipping
* @param svgContext state information about the document environment (parent attributes, etc.) 
* @param objectBox rectangle of the object being clipped in the coordinate system of this object
*/
-(void) addToClipPathForContext:(CGContextRef)quartzContext  withSVGContext:(id<SVGContext>)svgContext objectBoundingBox:(CGRect) objectBox;

/*! @briefmethod to communicate the preferred clipping type that this object can provide (path clipping preferred)
@param svgContext state information about the document environment
*/
-(ClippingType) getClippingTypeWithSVGContext:(id<SVGContext>)svgContext;

/*! @brief return a tight bounding box for the object's content
* @param svgContext state information about the document environment
*/
-(CGRect) getBoundingBoxWithSVGContext:(id<SVGContext>)svgContext;

@end

NS_ASSUME_NONNULL_END
