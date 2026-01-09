package ge.ak.elasticsearchappender.logging;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class MaskLogSensitiveDataAnnotationIntrospector extends JacksonAnnotationIntrospector {


    @Override
    protected boolean _isIgnorable(Annotated a) {
        LogSensitiveData annotation = a.getAnnotation(LogSensitiveData.class);
        if (annotation != null) {
            return true;
        }
        return super._isIgnorable(a);
    }

}
