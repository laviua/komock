package ua.com.lavi.komock.engine.handler.callback;

import org.jetbrains.annotations.NotNull;
import ua.com.lavi.komock.engine.model.Request;
import ua.com.lavi.komock.engine.model.Response;

public class EmptyCallbackHandlerImpl implements CallbackHandler {

    @Override
    public void handle(@NotNull Request request, @NotNull Response response) {
        // nothing to do
    }
}
