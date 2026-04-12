package az.fitnest.user.security;

import io.grpc.*;
import jakarta.servlet.http.HttpServletRequest;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@GrpcGlobalServerInterceptor
@GrpcGlobalClientInterceptor
public class PatternAGrpcInterceptor implements ServerInterceptor, ClientInterceptor {

    private static final Metadata.Key<String> X_USER_ID = Metadata.Key.of("x-user-id", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_TENANT_ID = Metadata.Key.of("x-tenant-id", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_SCOPES = Metadata.Key.of("x-scopes", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_REQUEST_ID = Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_FROM_GATEWAY = Metadata.Key.of("x-from-gateway", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_SERVICE_NAME = Metadata.Key.of("x-service-name", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String gatewayFlag = headers.get(X_FROM_GATEWAY);
        String userIdStr = headers.get(X_USER_ID);
        String requestId = headers.get(X_REQUEST_ID);
        String caller = headers.get(X_SERVICE_NAME);

        if ("1".equals(gatewayFlag) && userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                String scopes = headers.get(X_SCOPES);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (scopes != null && !scopes.isBlank()) {
                    authorities = Arrays.stream(scopes.split(" "))
                            .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                auth.setDetails("PatternA:gRPC:" + caller + ":" + requestId);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
            }
        }
        return next.startCall(call, headers);
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    propagate(request, headers, "X-User-Id", X_USER_ID);
                    propagate(request, headers, "X-Tenant-Id", X_TENANT_ID);
                    propagate(request, headers, "X-Scopes", X_SCOPES);
                    propagate(request, headers, "X-Request-Id", X_REQUEST_ID);
                    propagate(request, headers, "X-From-Gateway", X_FROM_GATEWAY);
                    headers.put(X_SERVICE_NAME, "user-backend");
                }
                super.start(responseListener, headers);
            }
        };
    }

    private void propagate(HttpServletRequest request, Metadata headers, String headerName, Metadata.Key<String> key) {
        String val = request.getHeader(headerName);
        if (val != null && !val.isBlank()) {
            headers.put(key, val);
        }
    }
}
