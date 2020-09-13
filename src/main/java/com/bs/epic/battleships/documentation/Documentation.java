package com.bs.epic.battleships.documentation;

import com.bs.epic.battleships.events.LastUid;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import java.util.ArrayList;
import java.util.Collection;

public class Documentation {
    private Collection<Entry> api;
    private SocketIOServer server;

    public Documentation(SocketIOServer server) {
        this.api = new ArrayList<>();
        this.server = server;
    }

    public <T> void addEventListener(String eventName, Class<T> eventClass, DataListener<T> listener) {
        server.addEventListener(eventName, eventClass, listener);

        var entry = new Entry(eventName);

        for (var field : LastUid.class.getFields()) {
            var annotations = field.getDeclaredAnnotations();

            for (var annotation : annotations) {
                if (annotation instanceof Doc) {
                    var a = (Doc) annotation;
                    entry.input.fields.add(new Tuple(
                        field.getType().toString(),
                        field.getName(),
                        a.description()
                    ));
                }
            }
        }

        api.add(entry);
    }
}
