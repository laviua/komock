package ua.com.lavi.komock.engine.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

import org.eclipse.jetty.util.resource.Resource;

/**
 * Created by oleksandrloushkin on 25.02.17.
 */
public class ByteResource extends Resource {

    private final byte[] content;

    public ByteResource(byte[] content) {
        this.content = content;
    }

    @Override
    public boolean isContainedIn(Resource resource) throws MalformedURLException {
        return false;
    }

    @Override
    public void close() {
        // nothing to close
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }

    @Override
    public String[] list() {
        return new String[0];
    }

    @Override
    public Resource addPath(String path) throws IOException {
        return null;
    }
}
