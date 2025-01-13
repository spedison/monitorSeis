package br.jus.tre_sp.sti.codes.seis.vo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BasicConfigure {
    Properties properties = new Properties();

    public BasicConfigure(String filename) throws IOException {
        try (FileInputStream input = new FileInputStream(filename)) {
            properties.load(input);
        }
    }

}
