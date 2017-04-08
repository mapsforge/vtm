//
//  SVGContext.h
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
//  Created by Glenn Howes on 1/28/14.
//

#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import UIKit;
#else
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#endif

NS_ASSUME_NONNULL_BEGIN

/*! @brief a protocol followed to communicate state when walking through a tree of SVG objects, passed into nodes/leaves in that tree
 */
@protocol SVGContext
/*! @brief makes a color for a given string found in such SVG attributes as fill, stroke, etc..
 * @param svgColorString a string such as 'blue', '#AAA', '#A7A2F9' or 'rgb(122, 255, 0)' which can be mapped to an RGB color
 * @return a UIColor from the RGB color space
 * @see UIColorFromSVGColorString
 */
-(nullable UIColor*) colorForSVGColorString:(NSString*)svgColorString;
/*! @brief make a URL relative to the document being parsed
 * @param subPath a location inside the app's resource bundle
 * @return an NSURL to some resource (hopefully)
 */
-(nullable NSURL*)	relativeURL:(NSString*)subPath;

/*! @brief make a URL
 * @param absolutePath a file path
 * @return an NSURL to some resource (hopefully)
 */
-(nullable NSURL*)   absoluteURL:(NSString*)absolutePath; // sort of...

/*! @brief find an object whose 'id' or maybe 'xml:id' property have the given name
 * @param objectName the name key to look for
 * @return some object (usually an id<GHRenderable> but not always
 */
-(nullable id)       objectNamed:(NSString*)objectName;

/*! @brief sometimes objects in SVG are referenced in the form 'URL(#aRef)'. This returns them.
 * @param aLocation some object in this document probably
 * @return some object (usually an id<GHRenderable> but not always
 */
-(nullable id)       objectAtURL:(NSString*)aLocation;
/*! @brief sometimes SVG colors are specified as 'currentColor'. This sets the starting currentColor before the tree is visited. Good for colorizing artwork.
 * @param startingCurrentColor a UIColor to start with
 */
-(void)     setCurrentColor:(nullable UIColor*)startingCurrentColor;
/*! @brief the value for 'currentColor' at this moment in the process of visiting a document
 */
-(nullable UIColor*) currentColor;

/*! @brief the value for 'opacity' at this moment in the process of visiting a document
 */
-(CGFloat) opacity;

/*! @brief opacity is dependent (via inheritence) as you descend an SVG Document. This opacity is the place to keep track of updated opacity.
 * @param the current opacity (defaults to 1.0)
 */
-(void) setOpacity:(CGFloat)opacity;


/*! @brief the active language expected by the user like 'en' or 'sp' or 'zh'
 */
-(nullable NSString*) isoLanguage;

/*! @brief if the SVG document specifies a 'non-scaling-stroke' this could be used to scale that. Rarely used.
 */
-(CGFloat)  explicitLineScaling;

/*! @brief  Does this SVGDocument/renderer have Cascading Style Sheet based attributes. Rarely true.
 */
-(BOOL) hasCSSAttributes;

/*! @brief  Look through the CSS attributes for a given styling attribute.
 * @param attributeName the name of the attribute like 'line-width'
 * @param listOfClasses the name of the CSS class like 'background' or some other arbitrary item
 * @param entityName the name of the entity like 'rect' or 'polyline'
 */
-(nullable NSString*) attributeNamed:(NSString*)attributeName classes:(nullable NSArray<NSString*>*)listOfClasses entityName:(nullable NSString*)entityName;

@end

NS_ASSUME_NONNULL_END
