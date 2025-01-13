package br.jus.tre_sp;

import br.jus.tre_sp.sti.codes.seis.infra.EmailSender;
import br.jus.tre_sp.sti.codes.seis.infra.HttpRequester;
import br.jus.tre_sp.sti.codes.seis.infra.HttpResponseResult;
import br.jus.tre_sp.sti.codes.seis.vo.TestHttpServerConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            log.error("Informe o nome do arquivo de parâmetros.");
            return;
        }

        TestHttpServerConfigure testConfig = new TestHttpServerConfigure(args[0]);
        log.info("Arquivo de configurações: {} carregado.", args[0]);


        HttpRequester request = new HttpRequester();
        HttpResponseResult result = null;
        Boolean errorOnConnect = null;
        StringBuilder msgError = new StringBuilder();

        try {
            result = request.sendGet(testConfig.getUrl());
            if (result != null && result.getStatusCode() / 100 == 2 && result.getBody().length() > 500) {
                errorOnConnect = false;
            } else {
                msgError.append("Retornou o resultado nullo\n");
                errorOnConnect = true;
            }
        } catch (UnresolvedAddressException ure) {
            errorOnConnect = true;
            String tmp = "Nome do servidor " + testConfig.getUrl() + " nao definido: ";

            msgError
                    .append(tmp)
                    .append(ure.toString())
                    .append("\n");

            log.error(tmp, ure);
        } catch (ConnectException ce) {
            errorOnConnect = true;
            String tmp = "Erro ao conectar com o servidor " + testConfig.getUrl() + " requisição HTTP: ";
            msgError
                    .append(tmp)
                    .append(ce.toString())
                    .append("\n");
            log.error(tmp, ce);
        } catch (Exception e) {
            String tmp = "Erro ao enviar requisição HTTP (Geral) ";
            errorOnConnect = true;
            msgError
                    .append(tmp)
                    .append(e.toString())
                    .append("\n");
            log.error(tmp, e);
        }

        log.debug("Retorno da solicitação {} com o resultado {}", testConfig.getUrl(), result != null ? result.toString() : "<<sem retorno>>");

        EmailSender emailSender = new EmailSender(testConfig.getMailHost(), testConfig.getMailPort(), testConfig.getMailUsername(), testConfig.getMailPassword());

        if (errorOnConnect) {
            emailSender.sendEmail(
                    testConfig.getMailDestinations(),
                    testConfig.getMailTitleFailure(),
                    "Ocorreu um erro ao conectar com o endereço: " +
                            testConfig.getUrl() + " verifique\n" + msgError.toString() + "\n");
        } else if (testConfig.getSendWithoutError()) {
            emailSender.sendEmail(
                    testConfig.getMailDestinations(),
                    testConfig.getMailTitleSucess(),
                    "Verificação sem Erro. :: Link " + testConfig.getUrl());
        }
    }
}