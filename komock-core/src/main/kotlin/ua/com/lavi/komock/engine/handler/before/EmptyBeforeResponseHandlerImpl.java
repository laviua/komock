package ua.com.lavi.komock.engine.handler.before;

import org.jetbrains.annotations.NotNull;
import ua.com.lavi.komock.engine.model.Request;
import ua.com.lavi.komock.engine.model.Response;

public class EmptyBeforeResponseHandlerImpl implements BeforeResponseHandler {

    @Override
    public void handle(@NotNull Request request, @NotNull Response response) {
        // nothing to do
    }
}
