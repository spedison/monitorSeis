package br.jus.tre_sp.sti.codes.seis.vo;

import lombok.Getter;

import java.io.IOException;

@Getter
public class TestHttpServerConfigure extends BasicConfigure{

    private final String url;
    private final String mailHost;
    private final String mailPort;
    private final String mailUsername;
    private final String mailPassword;
    private final Boolean sendWithoutError;
    private final String mailDestinations;
    private final String mailTitleSucess;
    private final String mailTitleFailure;
    private final String scriptTest;

    public TestHttpServerConfigure(String filename) throws IOException {
        super(filename);

        this.url = properties.getProperty("url");
        this.mailHost = properties.getProperty("mail.host");
        this.mailPort = properties.getProperty("mail.port");
        this.mailUsername = properties.getProperty("mail.username");
        this.mailPassword = properties.getProperty("mail.password");
        this.sendWithoutError = Boolean.parseBoolean(properties.getProperty("mail.send_without_error", "false"));
        this.mailDestinations = properties.getProperty("mail.destinations");
        this.mailTitleSucess = properties.getProperty("mail.title_sucess", "Titulo de sucesso");
        this.mailTitleFailure = properties.getProperty("mail.title_failure", "Titulo de falha");
        this.scriptTest= properties.getProperty("script.test", "default.groovy");

        if (url == null || mailHost == null || mailPort == null || mailUsername == null || mailPassword == null) {
            throw new IllegalArgumentException("Arquivo de configurações inválido.");
        }
    }

}
