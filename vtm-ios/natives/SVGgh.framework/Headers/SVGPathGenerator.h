//
//  SVGPathGenerator.h
//  SVGgh
// The MIT License (MIT)

//  Copyright (c) 2012-2014 Glenn R. Howes

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
//  Created by Glenn Howes on 12/31/12.


#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import CoreGraphics;
#else
#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#endif

typedef enum SVGPathValidationError 
{
    kPathParsingErrorNone = 0,
    kPathParsingErrorMissingNumber,
    kPathParsingErrorExpectedBoolean,
    kPathParsingErrorExpectedDegrees,
    kPathParsingErrorUnknownOperand,
    kPathParsingErrorMissingStart,
    kPathParsingErrorMissingVirtualControlPoint
}SVGPathValidationError;

NS_ASSUME_NONNULL_BEGIN

/*! @brief an object which encapsulates the results of trying to verify an SVG paths 'd' attribute
*/
@interface PathValidationResult : NSObject

/*! @property rangeOfError where in the path string was an error found
*/
@property(nonatomic, readonly) NSRange rangeOfError;

/*! @property error which of the enumerated SVGPathValidationError codes were found
*/
@property(nonatomic, readonly) SVGPathValidationError errorCode;

/*! @property operatorAtError which SVG path operation was the point at which an invalid string was reached
*/
@property(nonatomic, readonly) unsigned char  operatorAtError;

/*! @property errorInLastOperation was the error (if any) found while parsing the last operation
*/
@property(nonatomic, readonly) BOOL   errorInLastOperation;

/*! @property unexpectedCharacters a string of characters that should never be in an SVG path entity's 'd' attribute but were
*/
@property(nonatomic, readonly) NSString* __nullable  unexpectedCharacters;

@end

/*! @brief a bundle of mehtods that deal with the interaction between CGPaths and the text strings to build them
*/
@interface SVGPathGenerator : NSObject
/*! @brief given a CGPathRef, convert it to an SVG Path
* @param aPath a path to be serialized
* @return a string appropriate for a 'd' attribute of an SVG path entity
*/
+(nullable NSString*) svgPathFromCGPath:(CGPathRef)aPath;

/*! @brief given a 'd' attribute from an SVG path entity, create a Core Graphics Path
* @param anSVGPath something like 'M33 11 H22 L 100 100 a 20 40 0 1 1 12 14 Z'
* @param aTransform an affine transform to apply to the result at the time of creation
*/
+(nullable CGPathRef) newCGPathFromSVGPath:(NSString*)anSVGPath whileApplyingTransform:(CGAffineTransform)aTransform CF_RETURNS_RETAINED;

/*! @brief given a SVG path in text form, return a bounding box (includes control points)
* @param anSVGPath a string from a path entity's 'd' attribute
* @return a rectangle which encapulates all the points on the path and any control points
*/
+(CGRect)  maxBoundingBoxForSVGPath:(NSString*)anSVGPath;

/*! @brief validate the provided SVG path string
* @param anSVGPath a string from a path entity's 'd' attribute
* @return an object which should be checked for errors in parsing the path
*/
+(nullable PathValidationResult*) findFailure:(NSString*)anSVGPath;

/*! @brief given an SVG operator e.g. 'm', 'z', 'l', 'H', etc., give the number of expected parameters
* @param svgOperator one of the expected operators for an SVG path
* @return number of parameters needed for given operator, e.g. 'z' would return 0
*/
+(NSInteger) parametersNeededForOperator:(unsigned char)svgOperator;

/*! @brief set of characters that should never appear in a 'd' attribute of an SVG path entity
* @return a set of characters like 'b' or '!' or whatever that never appear
*/
+(NSCharacterSet*)invalidPathCharacters;

@end
NS_ASSUME_NONNULL_END

