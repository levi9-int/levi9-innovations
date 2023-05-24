package org.example.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public class JWTUtil {
    static ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
    private final static String userPoolId = "eu-north-1_KkldHIWwQ";

    static {
        try {
            JWKSource keySource = null;
            keySource = new RemoteJWKSet(
                    new URL("https://cognito-idp."+"eu-north-1"+".amazonaws.com/"+ userPoolId +"/.well-known/jwks.json"));
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
            JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
            jwtProcessor.setJWSKeySelector(keySelector);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public static String getSub(String token, String atr)  {
        SecurityContext ctx = null;
        JWTClaimsSet claimsSet = null;
        try {
            claimsSet = jwtProcessor.process(token, ctx);
            return claimsSet.getStringClaim(atr);
        } catch (BadJOSEException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static List<String> getCognitoGroups(String jwtToken, String atr) {
        SecurityContext ctx = null;
        JWTClaimsSet claimsSet = null;
        try {
            claimsSet = jwtProcessor.process(jwtToken, ctx);
            return claimsSet.getStringListClaim(atr);
        } catch (BadJOSEException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}