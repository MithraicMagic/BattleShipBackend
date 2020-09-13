package com.bs.epic.battleships.documentation;

import com.bs.epic.battleships.events.ErrorEvent;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import java.util.ArrayList;
import java.util.Collection;

public class Documentation {
    private Collection<Entry> api;
    private SocketIOServer server;

    private static Documentation documentation = new Documentation();

    private Documentation() {
        this.api = new ArrayList<>();
    }

    public static Documentation get() { return documentation; }

    public <T, U> void addEventListener(String eventName, Class<T> eventClass, Class<U> result, DataListener<T> listener) {
        server.addEventListener(eventName, eventClass, listener);

        var entry = new Entry(eventName);
        entry.input = getFields(eventClass);
        if (result != null) {
            entry.output = getFields(result);
        }
        else {
            entry.output = new Fields(eventName);
        }
        entry.onError = getFields(ErrorEvent.class);

        api.add(entry);
    }

    private <T> Fields getFields(Class<T> c) {
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

        return new Fields(col);
    }

    public Collection<Entry> getApi() { return api; }
    public void setSocketServer(SocketIOServer server) { this.server = server; }
}
