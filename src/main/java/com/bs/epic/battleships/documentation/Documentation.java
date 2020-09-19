package com.bs.epic.battleships.documentation;

import com.bs.epic.battleships.documentation.annotations.*;
import com.bs.epic.battleships.events.ErrorEvent;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;

public class Documentation {
    private final Collection<SocketEntry> socketApi;
    private final Collection<RestEntries> restApi;

    private SocketIOServer server;

    private static final Documentation documentation = new Documentation();

    private Documentation() {
        this.socketApi = new ArrayList<>();
        this.restApi = new ArrayList<>();
    }

    public static Documentation get() { return documentation; }

    public <T, U> void addEventListener(String eventName, Class<T> eventClass, Class<U> result, DataListener<T> listener) {
        server.addEventListener(eventName, eventClass, listener);
        System.out.println("[Docs] Adding Listener on: " + eventName);

        var entry = new SocketEntry(eventName);
        entry.input = getFields(eventClass);
        if (result != null) {
            entry.output = getFields(result);
        }
        else {
            entry.output = new Fields(eventName);
        }
        entry.onError = getFields(ErrorEvent.class);

        socketApi.add(entry);
    }

    public <T> void addController(Class<T> controller) {
        var name = getShortName(controller.getName());
        var entries = new RestEntries(name);
        System.out.println("[Docs] Adding Controller with name: " + name);

        for (var an : controller.getAnnotations()) {
            if (an instanceof RequestMapping) {
                var reqAn = (RequestMapping) an;
                var val = reqAn.value();
                if (val.length != 0) {
                    entries.basePath = val[0];
                    break;
                }
            }
        }

        for (var method : controller.getMethods()) {
            RestEntry entry = null;

            for (var annotation : method.getAnnotations()) {
                var reqMapping = getRequestMapping(annotation);
                if (reqMapping != null) {
                    entry = new RestEntry();

                    entry.httpVerb = getHttpVerb(reqMapping);
                    entry.path = getPath(annotation);
                    entry.output = new RestOutput(200);
                    entry.onError.add(new RestOutput(500));
                }

                if (entry != null) {
                    if (annotation instanceof Returns) {
                        var ret = (Returns) annotation;
                        entry.output.fields = getTuples(ret.value());
                    }

                    if (annotation instanceof OnError) {
                        var err = (OnError) annotation;
                        var code = err.code() == 0 ? 500 : err.code();
                        entry.onError.add(new RestOutput(code, err.desc(), getTuples(err.value())));
                    }

                    if (annotation instanceof OnErrors) {
                        var errors = (OnErrors) annotation;
                        for (var err : errors.value()) {
                            var code = err.code() == 0 ? 500 : err.code();
                            entry.onError.add(new RestOutput(code, err.desc(), getTuples(err.value())));
                        }
                    }
                }
            }

            if (entry != null) {
                for (var param : method.getParameters()) {
                    for (var annotation : param.getAnnotations()) {
                        if (annotation instanceof PathVariable) {
                            var type = param.getType().getName();
                            var n = param.getName();
                            var desc = param.getAnnotation(Doc.class).value();

                            entry.pathVariables.add(new Tuple(type, n, desc));
                        }

                        if (annotation instanceof RequestBody) {
                            entry.body = getFields(param.getType());
                        }
                    }
                }

                entries.entries.add(entry);
            }
        }

        restApi.add(entries);
    }

    private <T> Fields getFields(Class<T> c) {
        return new Fields(getTuples(c));
    }

    private <T> Collection<Tuple> getTuples(Class<T> c) {
        var col = new ArrayList<Tuple>();
        if (c.isPrimitive() || c.getName().equals("java.lang.String")) {
            col.add(new Tuple(getShortName(c.getTypeName()), ""));
            return col;
        }

        for (var field : c.getFields()) {
            var annotations = field.getDeclaredAnnotations();
            var ignore = false;

            var typeName = new StringBuilder().append(getShortName(field.getType().toString()));
            var gType = field.getGenericType();
            if (gType instanceof ParameterizedType) {
                var paramType = (ParameterizedType) gType;
                var args = paramType.getActualTypeArguments();
                for (var arg : args) {
                    var cArg = (Class) arg;
                    typeName.append("<").append(getShortName(cArg.getName())).append(">");
                }
            }

            var tuple = new Tuple(typeName.toString(), field.getName());

            for (var annotation : annotations) {
                if (annotation instanceof Doc) {
                    var a = (Doc) annotation;
                    tuple.description = a.value();
                }

                //Ignore fields with the @DocIgnore Annotation
                if (annotation instanceof DocIgnore) ignore = true;
            }

            if (!ignore) col.add(tuple);
        }

        return col;
    }

    private String getShortName(String n) {
        var split = n.split("\\.");
        return split[split.length - 1];
    }

    private String getHttpVerb(RequestMapping mapping) {
        var methods = mapping.method();
        if (methods.length > 0) return methods[0].name();

        return "Unknown";
    }

    private String getPath(Annotation a) {
        if (a instanceof RequestMapping) {
            var mapping = (RequestMapping) a;
            var paths = mapping.path();
            if (paths.length > 0) return paths[0];

            return "Unknown";
        }
        else if (isControllerMapping(a)) {
            return getPathFromControllerMapping(a);
        }

        return "Unknown";
    }

    public RequestMapping getRequestMapping(Annotation a) {
        if (a instanceof RequestMapping) return (RequestMapping) a;

        if (isControllerMapping(a)) {
            var cA = a.annotationType();
            return cA.getAnnotation(RequestMapping.class);
        }

        return null;
    }

    private String getPathFromControllerMapping(Annotation a) {
        String[] paths = null;

        if (a instanceof GetMapping) {
            paths = ((GetMapping) a).value();
        }
        else if (a instanceof PostMapping) {
            paths  = ((PostMapping) a).value();
        }
        else if (a instanceof PutMapping) {
            paths  = ((PutMapping) a).value();
        }
        else if (a instanceof DeleteMapping) {
            paths  = ((DeleteMapping) a).value();
        }
        else if (a instanceof PatchMapping) {
            paths  = ((PatchMapping) a).value();
        }

        if (paths != null && paths.length > 0) return paths[0];
        return "Unknown";
    }

    private boolean isControllerMapping(Annotation a) {
        return a instanceof GetMapping || a instanceof PostMapping ||
                a instanceof PutMapping || a instanceof DeleteMapping ||
                a instanceof PatchMapping;
    }

    public Collection<SocketEntry> getSocketApi() { return socketApi; }
    public Collection<RestEntries> getRestApi() { return restApi; }

    public void setSocketServer(SocketIOServer server) { this.server = server; }
}
