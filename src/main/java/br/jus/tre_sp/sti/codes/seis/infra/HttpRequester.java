package br.jus.tre_sp.sti.codes.seis.infra;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRequester {

    private HttpClient client = HttpClient.newHttpClient();

    public HttpResponseResult sendGet(String url) throws InterruptedException, IOException {
        return executeRequest(url, "", "GET");
    }

    /**
     * Executa uma requisição HTTP com os parâmetros fornecidos.
     *
     * @param url        A URL de destino.
     * @param body       O corpo da requisição (pode ser vazio para GET/DELETE).
     * @param httpMethod O método HTTP (GET, POST, PUT, DELETE, etc.).
     * @return Um objeto contendo o código de status e o corpo da resposta.
     * @throws IOException          Se ocorrer um erro de I/O.
     * @throws InterruptedException Se a requisição for interrompida.
     */
    public HttpResponseResult executeRequest(String url, String body, String httpMethod) throws IOException, InterruptedException, IOException {

        // Construção da requisição HTTP
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url));

        switch (httpMethod.trim().toUpperCase()) {
            case "GET":
                requestBuilder.GET();
                break;

            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
                break;

            case "PUT":
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body));
                break;

            case "DELETE":
                requestBuilder.DELETE();
                break;

            default:
                throw new IllegalArgumentException("Método HTTP não suportado: " + httpMethod);
        }

        HttpRequest request = requestBuilder.build();

        // Enviar a requisição e obter a resposta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Retornar o código HTTP e o corpo da resposta
        return new HttpResponseResult(response.statusCode(), response.body());
    }
}