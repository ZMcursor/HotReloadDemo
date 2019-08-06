package hotreload.apps.simpleapp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class demo {
    public void work() {
        Map<String, String> map = new HashMap<String, String>() {
            private static final long serialVersionUID = -1869619533366323578L;
        };

        System.out.println(map.getClass());
        System.out.println(map.getClass().getSuperclass());
        System.out.println(map.getClass().getGenericSuperclass());

        Type type = map.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            for (Type t : p.getActualTypeArguments()) {
                System.out.println(t);
            }
        }
    }
}