import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {

    public static void main(final String... args) throws IOException {
        final var content = resourceAsString("/index.html");
        newHttpServer("0.0.0.0", 8081, staticHtml(content)).start();
    }

    public static Undertow newHttpServer(final String address, final int port, final HttpHandler handler) {
        return Undertow.builder()
            .addHttpListener(port, address)
            .setHandler(handler)
            .build();
    }

    public static HttpHandler staticHtml(final String content) {
        return exchange -> {
            exchange.setStatusCode(OK);
            exchange.getResponseHeaders().add(CONTENT_TYPE, "text/html; charset=UTF-8");
            exchange.getResponseSender().send(content);
        };
    }

    public static String resourceAsString(final String resource) throws IOException {
        try (final var in = Server.class.getResourceAsStream(resource)) {
            if (in == null) throw new FileNotFoundException("Resource " + resource + " does not exist.");
            try (final var result = new ByteArrayOutputStream()) {
                final var buffer = new byte[1024];
                for (int read; (read = in.read(buffer)) != -1;) {
                    result.write(buffer, 0, read);
                }
                return result.toString(UTF_8);
            }
        }
    }

}
