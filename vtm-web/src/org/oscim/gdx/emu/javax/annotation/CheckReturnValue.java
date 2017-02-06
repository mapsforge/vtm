package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target( { ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE })
public @interface CheckReturnValue {
}
