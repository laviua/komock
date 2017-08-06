package ua.com.lavi.komock.engine.handler.after;

import org.jetbrains.annotations.NotNull;
import ua.com.lavi.komock.engine.model.Request;
import ua.com.lavi.komock.engine.model.Response;

public class EmptyAfterResponseHandlerImpl implements AfterResponseHandler {
    @Override
    public void handle(@NotNull Request request, @NotNull Response response) {
        // nothing to do
    }
}
