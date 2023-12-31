package pawissanutt.oprc.cli;

import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.net.URI;

@ApplicationScoped
public class ToolProducer {
    @Inject
    Vertx vertx;

    @Produces
    @ApplicationScoped
    WebClient webClient(CommandLine.ParseResult parseResult) {
        var matchedOption = parseResult.matchedOption("proxy");
        String proxyString = System.getenv("OPRC_PROXY");
        if (matchedOption != null) {
            proxyString = matchedOption.getValue().toString();
        }

        WebClientOptions webClientOptions = new WebClientOptions();
        if (proxyString != null) {
            var proxy = URI.create(proxyString);
            if (!proxy.isAbsolute()){
                throw new IllegalStateException("Proxy URL is not valid ("+proxyString+")");
            }

            var type = switch (proxy.getScheme()) {
                case "socks5" -> ProxyType.SOCKS5;
                case "socks4" -> ProxyType.SOCKS4;
                case "http" -> ProxyType.HTTP;
                default -> throw new IllegalArgumentException("Unsupported proxy protocol of " + proxy.getScheme());
            };
            var auth = proxy.getUserInfo();
            var proxyOptions = new ProxyOptions().setType(type)
                    .setHost(proxy.getHost());
            if (proxy.getPort() > 0) {
                proxyOptions.setPort(proxy.getPort());
            }
            if (!auth.isEmpty()) {
                var splitAuth = auth.split(":");
                proxyOptions.setUsername(splitAuth[0]);
                if (splitAuth.length >= 2) {
                    proxyOptions.setPassword(splitAuth[1]);
                }
            }
            webClientOptions.setProxyOptions(proxyOptions);
        }
        return WebClient.create(vertx,
                webClientOptions
        );
    }
}
