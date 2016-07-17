//
//  SVGRenderer+PDF.h
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
//  Created by Glenn Howes on 2/4/14.
//
#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import CoreGraphics;
#else
#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#endif

@class SVGRenderer;

NS_ASSUME_NONNULL_BEGIN

typedef void(^renderPDFCallback_t)(NSData* __nullable  pdfData);



@interface SVGtoPDFConverter : NSObject
/*! @brief call to create a PDF, does so on another queue
* @param aRenderer a configured renderer
* @param callback the block to get called when done
* @attention will callback on another queue may return nil pdfData
*/
+(void) createPDFFromRenderer:(SVGRenderer*)aRenderer intoCallback:(renderPDFCallback_t)callback;
@end

/*! \brief utility method to create a PDF context
* \param mediaRect the resulting PDFs boundary (zero origin preferred)
* \param theData an allocated but empty block of data which will be filled with the PDF
* \return a Core Graphics context. Caller responsible for disposal.
*/
__nullable CGContextRef	CreatePDFContext(const CGRect mediaRect, CFMutableDataRef theData);

NS_ASSUME_NONNULL_END
