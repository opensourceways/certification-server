package com.huawei.it.euler.ddd.domain.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhiteListService {
    @Value("${url.whitelist}")
    private String whitelist;

    public boolean isWriteUrl(String url) {
        String[] writeUrlArr = whitelist.split(",");
        for (String writeUrl : writeUrlArr) {
            writeUrl = writeUrl.replaceAll("\\*", "\\.\\*");
            if (writeUrl.matches(url)) {
                return true;
            }
        }
        return false;
    }

}
