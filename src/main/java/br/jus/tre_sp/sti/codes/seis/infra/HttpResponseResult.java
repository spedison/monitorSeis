package br.jus.tre_sp.sti.codes.seis.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class HttpResponseResult {
    private final int statusCode;
    private final String body;
}
