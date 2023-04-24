//package sk.stuba.sdg.isbe.configuration;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
//
//@Profile("deployment")
//public class APIKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {
//
//    private final String principalRequestHeader;
//
//    public APIKeyAuthFilter(String principalRequestHeader) {
//        this.principalRequestHeader = principalRequestHeader;
//    }
//
//    @Override
//    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
//        return request.getHeader(principalRequestHeader);
//    }
//
//    @Override
//    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
//        return "N/A";
//    }
//}