package svg;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.objc.annotation.Property;
import org.robovm.rt.bro.annotation.Library;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.ptr.Ptr;

@Library(Library.INTERNAL) 
@NativeClass("SVGParser")
public class SVGParser extends NSObject {
    public static class SVGParserPtr extends Ptr<SVGParser, SVGParserPtr> {}
    static { ObjCRuntime.bind(SVGParser.class); }/*</bind>*/

	public SVGParser() {};
    protected SVGParser(long handle) { super(handle); }
    protected SVGParser(SkipInit skipInit) { super(skipInit); }
    
    public SVGParser(String utf8String) { super((SkipInit) null); initObject(init(utf8String)); }
    
    @Method(selector = "initWithString:")
    protected native @Pointer long init(String utf8String);

    @Property(selector = "parserError")
    public native NSError getParserError();

}
