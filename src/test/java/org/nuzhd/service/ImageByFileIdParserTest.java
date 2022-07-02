package org.nuzhd.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ImageByFileIdParserTest {

    @Autowired
    ImageByFileIdParser p;


    @Test
    void getFilePathByFileId() {

    }

    @Test
    void parseImageFromTgServers() throws IOException, InterruptedException {

        String fid = "AgACAgIAAxkBAAMFYrmAqiiRxNhREbT-FNmm-xV34B0AAjvAMRvlAAHJSUK9A4qWEbQfAQADAgADcwADKQQ";

        System.out.println(p.parseImageFromTgServers(fid));
    }
}