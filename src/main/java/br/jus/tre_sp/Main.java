package br.jus.tre_sp;

import br.jus.tre_sp.sti.codes.seis.infra.EmailSender;
import br.jus.tre_sp.sti.codes.seis.infra.ExecutorTest;
import br.jus.tre_sp.sti.codes.seis.infra.HttpRequester;
import br.jus.tre_sp.sti.codes.seis.infra.HttpResponseResult;
import br.jus.tre_sp.sti.codes.seis.vo.TestHttpServerConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    HttpRequester request = new HttpRequester();
    HttpResponseResult result = null;
    Boolean errorOnConnect = null;
    StringBuilder msgError = new StringBuilder();
    ExecutorTest executeScript;
    TestHttpServerConfigure testConfig;
    EmailSender emailSender ;

    public static void main(String[] args) throws IOException, InterruptedException {
        (new Main()).execute(args);
    }

    public void executeHttpRequest(){
        try {
            result = request.sendGet(testConfig.getUrl());
        }catch (InterruptedException | IOException e){
            errorOnConnect = true;
            msgError
                    .append("Erro ao enviar requisição HTTP (Geral): ")
                    .append(e.toString())
                    .append("\n");
            log.error("Erro ao enviar requisição HTTP (Geral): ", e);
        }
    }

    public void sendMail(){

        emailSender = new EmailSender(testConfig.getMailHost(), testConfig.getMailPort(), testConfig.getMailUsername(), testConfig.getMailPassword());

        if (errorOnConnect) {
            emailSender.sendEmail(
                    testConfig.getMailDestinations(),
                    testConfig.getMailTitleFailure(),
                    "Ocorreu um erro ao conectar com o endereço: " + testConfig.getUrl()
                            + " verifique : \n" + msgError.toString() + "\n"
                            + "\n --> Checagens com erro    ::: \n" + executeScript.getMsgErrors()
                            + "\n --> Checagens com sucesso ::: \n" + executeScript.getMsgOk()
                            + "\n\n<FIM DA MENSAGEM>"
            );
        } else if (testConfig.getSendWithoutError()) {
            emailSender.sendEmail(
                    testConfig.getMailDestinations(),
                    testConfig.getMailTitleSucess(),
                    "Verificação sem Erro. :: Link "
                            + testConfig.getUrl() + "\n"
                            + executeScript.getMsgOk());
        }
    }

    public void runScriptTest() throws IOException, IllegalArgumentException {
        if (!errorOnConnect){
            Boolean sucess = executeScript.execute(result.getStatusCode(), result.getBody());
            if (sucess != null) {
                errorOnConnect = !sucess;
            } else {
                msgError
                        .append("Retorno do Script foi nullo")
                        .append("\n");
            }
        }
    }


    public void execute(String[] args) throws IOException {

        if (args.length == 0) {
            log.error("Informe o nome do arquivo de parâmetros.");
            return;
        }

        log.debug("Iniciando a aplicação.");

        testConfig = new TestHttpServerConfigure(args[0]);
        log.info("Arquivo de configurações: {} carregado.", args[0]);

        executeScript = new ExecutorTest(testConfig.getScriptTest());
        log.info("Script: {} carregado.", testConfig.getScriptTest());

        executeHttpRequest();
        log.info("Http request executado");

        runScriptTest();
        log.info("Script executado");

        log.debug("Retorno da solicitação {} com o resultado {}", testConfig.getUrl(), result != null ? result.toString() : "<<sem retorno>>");

        sendMail();
        log.info("Email enviado");
    }
}