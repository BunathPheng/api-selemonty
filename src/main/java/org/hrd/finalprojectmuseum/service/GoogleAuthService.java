package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.LoginToken;
import org.hrd.finalprojectmuseum.model.enums.Role;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleAuthService {
    LoginToken verifyAndExtractUserInfo(String idTokenString, String role) throws Exception;

}
