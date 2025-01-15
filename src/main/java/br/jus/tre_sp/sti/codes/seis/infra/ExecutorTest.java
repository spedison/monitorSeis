package br.jus.tre_sp.sti.codes.seis.infra;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.Getter;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Getter
public class ExecutorTest {

    private final String fileName;
    private static final Logger log = LoggerFactory.getLogger(ExecutorTest.class);

    private String msgErrors = "Não executado a avaliação";
    private String msgOk = "Não executado a avaliação";
    private Boolean result;

    public ExecutorTest(String fileName) {
        this.fileName = fileName;
    }

    public Boolean execute(int httpCode, String page) throws IOException, IllegalArgumentException {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("groovy.lang.Script");
        Binding bindings = new Binding();
        bindings.setVariable("httpCode", httpCode);
        bindings.setVariable("page", page);

        try {
            String script = Files.readString(Path.of(fileName));
            GroovyShell shell = new GroovyShell(bindings, config);
            Object ret = shell.evaluate(script);

            if (ret != null) {
                if (ret instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) ret;
                    this.msgErrors = (String) map.getOrDefault("msgErro", "");
                    this.msgOk = (String) map.getOrDefault("msgOk", "");
                    this.result = (Boolean) map.getOrDefault("result", false);
                    return this.result;
                }
                throw new IllegalArgumentException("Retorno do script não é o tipo esperado Map<String,Object>.");
            } else {
                throw new NullPointerException("Retorno do script é nullo");
            }
        } catch (IOException e) {
            log.error("Erro ao ler script: {}", fileName, e);
            throw e;
        }
    }

}
