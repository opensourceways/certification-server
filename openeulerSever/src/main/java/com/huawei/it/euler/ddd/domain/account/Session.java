package com.huawei.it.euler.ddd.domain.account;

import com.huawei.it.euler.util.SessionManagement;
import lombok.Data;

import java.security.GeneralSecurityException;

@Data
public class Session {
    public String create(){
        try {
            return SessionManagement.genSessionIdToHex();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
