package com.bs.epic.battleships.documentation;

import com.bs.epic.battleships.events.ErrorEvent;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

public class Documentation {
    private Collection<SocketEntry> socketApi;
    private Collection<RestEntries> restApi;

    private SocketIOServer server;

    private static Documentation documentation = new Documentation();

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
        var nameParts = controller.getName().split("\\.");
        var name = nameParts[nameParts.length - 1];

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
                if (annotation instanceof RequestMapping) {
                    var req = (RequestMapping) annotation;
                    entry = new RestEntry();

                    entry.httpVerb = req.method()[0].name();
                    entry.path = req.path()[0];
                    entry.output = new RestOutput(200);
                }

                if (annotation instanceof Returns && entry != null) {
                    var ret = (Returns) annotation;
                    entry.output.fields = getTuples(ret.object());
                }
            }

            if (entry != null) entries.entries.add(entry);
        }

        restApi.add(entries);
    }

    private <T> Fields getFields(Class<T> c) {
        return new Fields(getTuples(c));
    }

    private <T> Collection<Tuple> getTuples(Class<T> c) {
        var col = new ArrayList<Tuple>();

        for (var field : c.getFields()) {
            var annotations = field.getDeclaredAnnotations();

            for (var annotation : annotations) {
                if (annotation instanceof Doc) {
                    var a = (Doc) annotation;

                    var typeName = field.getType().toString();
                    var splitTypeName= typeName.split("\\.");
                    typeName = splitTypeName[splitTypeName.length - 1];

                    col.add(new Tuple(
                            typeName,
                            field.getName(),
                            a.description()
                    ));
                }
            }
        }

        return col;
    }

    public Collection<SocketEntry> getSocketApi() { return socketApi; }
    public Collection<RestEntries> getRestApi() { return restApi; }

    public void setSocketServer(SocketIOServer server) { this.server = server; }
}
