package ua.com.lavi.komock.engine.handler.response;

import org.jetbrains.annotations.NotNull;
import ua.com.lavi.komock.engine.model.Request;
import ua.com.lavi.komock.engine.model.Response;

public class EmptyResponseHandler implements ResponseHandler {

    @Override
    public void handle(@NotNull Request request, @NotNull Response response) {
        // nothing to do
    }
}
